package io.github.jklingsporn.coronamq.core.impl;

import io.github.jklingsporn.coronamq.core.CoronaMqOptions;
import io.github.jklingsporn.coronamq.core.TaskRepository;
import io.github.jklingsporn.coronamq.core.TaskStatus;
import io.github.jklingsporn.coronamq.core.Worker;
import io.vertx.codegen.annotations.Nullable;
import io.vertx.core.*;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * A worker that can handle one task at a time. If a task is sent to the worker's eventbus address while the worker
 * is busy, the incoming task is discarded by this worker. On {@link #start()} and after a task has been completed,
 * this worker tries to acquire a new task from the queue.
 */
public abstract class AbstractWorker implements Worker {

    enum TaskOrigin{
        BROKER,
        REPOSITORY
    }

    enum TaskProcessState {
        /**
         * Successfully marked the task running.
         */
        SET_RUNNING_OK,
        /**
         * Failed marking the task running. Usually because another worker already took it.
         */
        SET_RUNNING_FAILED,
        /**
         * Successfully executed the task by this worker.
         */
        RUN_OK,
        /**
         * Unsuccessfully executed the task by this worker.
         */
        RUN_FAILED,
        /**
         * Successfully marked the task completed.
         */
        COMPLETED,
        PAUSED
    }

    private static final Logger logger = LoggerFactory.getLogger(AbstractWorker.class);

    protected final Vertx vertx;
    private final CoronaMqOptions coronaMqOptions;
    private final String label;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean paused = new AtomicBoolean(false);
    /**
     * Signals that the repository is about to shut down. The worker can try to finish his task but shouldn't
     * accept and request more tasks after that.
     */
    private final AtomicBoolean limbo = new AtomicBoolean(false);
    private final TaskRepository repository;
    private final ServiceDiscovery serviceDiscovery;

    private MessageConsumer<JsonObject> messageConsumer;
    private MessageConsumer<JsonObject> discoveryHandler;
    private Future<Void> currentWork = Future.succeededFuture();


    public AbstractWorker(Vertx vertx,
                          CoronaMqOptions coronaMqOptions,
                          String label) {
        this.vertx = vertx;
        this.coronaMqOptions = coronaMqOptions;
        this.label = label;
        this.repository = TaskRepository.createProxy(vertx,coronaMqOptions.getRepositoryAddress());
        this.serviceDiscovery = ServiceDiscovery.create(vertx, coronaMqOptions.getServiceDiscoveryOptions());
    }

    @Override
    public Future<Void> start() {
        //Listen for tasks that are sent from the broker
        return registerTaskReceivedHandler()
                //Listen for availability changes from the repository
                .onSuccess(v->registerToServiceDiscovery())
                //Check once if the repository is currently available
                .compose(v-> checkForRepositoryAvailability())
                //request a new task without composing here: we don't want to wait for the task-result
                .onSuccess(v->requestNewTask())
                .mapEmpty()
                ;
    }

    private Future<Void> registerTaskReceivedHandler() {
        messageConsumer = vertx.eventBus().consumer(Internal.toWorkerAddress(coronaMqOptions,label), new TaskReceivedHandler());
        Promise<Void> registered = Promise.promise();
        messageConsumer.completionHandler(registered);
        return registered.future();
    }

    private void registerToServiceDiscovery(){
        //listen for repository discovery
        discoveryHandler = vertx.eventBus().consumer(coronaMqOptions.getServiceDiscoveryOptions().getAnnounceAddress(), msg -> {
            Record discoverEvent = new Record(msg.body());
            if(Internal.REPOSITORY_SERVICE_RECORD_NAME.equals(discoverEvent.getName())){
                if(discoverEvent.getStatus() == Status.UP){
                    onRepositoryUP();
                }else if(discoverEvent.getStatus().equals(Status.DOWN) && discoverEvent.getMetadata().getLong("shutdownInMillis") != null){
                    onRepositoryGracefulShutdown(discoverEvent.getMetadata().getLong("shutdownInMillis"));
                }else{
                    onRepositoryDown();
                }
            }
        });
    }

    @Override
    public Future<Void> stop() {
        Promise<Void> unregistered = Promise.promise();
        if(messageConsumer == null){
            unregistered.complete();
        }else{
            messageConsumer.unregister(unregistered);
        }
        //unregister and wait for the current work to be completed
        return unregistered.future()
                .compose(v -> currentWork
                        .onFailure(x->logger.error("Final task failed",x))
                        .recover(x->Future.succeededFuture())
                )
                .compose(v-> unregisterFromServiceDiscovery());
    }

    private Future<Void> unregisterFromServiceDiscovery(){
        Future<Void> shutdown = Future.succeededFuture();
        if(discoveryHandler != null){
            shutdown = discoveryHandler.unregister();
        }
        return shutdown
                .onComplete(v->serviceDiscovery.close());
    }

    /**
     * The repository went away.
     */
    protected void onRepositoryDown(){
        logger.debug("Worker paused");
        paused.set(true);
    }

    /**
     * The repository will go away in X seconds. No longer request new tasks.
     * @param shutdownMillis
     */
    protected void onRepositoryGracefulShutdown(long shutdownMillis){
        logger.debug("Worker graceful shutdown");
        limbo.set(true);
    }

    /**
     * The repository is (back) up
     */
    protected void onRepositoryUP(){
        logger.debug("Worker resumed");
        paused.set(false);
        limbo.set(false);
    }

    /**
     * While this worker has received a task from the eventbus (broker), the repository went away
     * @param taskId
     * @param message
     */
    protected void taskReceivedWhilePaused(UUID taskId, JsonObject message){
        logger.debug("Task {} received while worker is paused",taskId);
    }

    /**
     * Right after this worker has received a task from the repository, the repository went away. This leaves the
     * task in a bad condition as it is marked RUNNING but nothing is actually happening.
     * TODO: add tasks to a list which is processed when the repository is available again
     * @param taskId
     * @param message
     */
    protected void taskRequestedWhilePaused(UUID taskId, JsonObject message){
        logger.debug("Task {} requested while worker is paused",taskId);
    }

    /**
     * Right after this worker has completed a task, the repository went away. This leaves the
     * task in a bad condition as it is marked RUNNING and ready to be set COMPLETED.
     * TODO: add tasks to a list which is processed when the repository is available again
     * @param taskId
     * @param message
     */
    protected void taskCompletedWhilePaused(UUID taskId, JsonObject message){
        logger.debug("Task {} completed while worker is paused",taskId);
    }

    /**
     * Right after a RUNNING task has been failed through an exception of this worker.
     * @param taskId
     * @param message
     */
    protected void taskFailedWhilePaused(UUID taskId, JsonObject message){
        logger.debug("Task {} failed while worker is paused",taskId);
    }

    /**
     * The current unit of work
     * @return
     */
    protected Future<Void> getCurrentWork() {
        return currentWork;
    }

    protected final boolean isPaused() {
        return paused.get();
    }

    private Future<@Nullable Record> checkForRepositoryAvailability() {
        return serviceDiscovery.getRecord(rec -> Internal.REPOSITORY_SERVICE_RECORD_NAME.equals(rec.getName()))
                .onComplete(rec -> {
                    if(rec.result()==null || rec.result().getStatus() != Status.UP){
                        onRepositoryDown();
                    }else{
                        onRepositoryUP();
                    }
                });
    }

    /**
     * Every worker follows the given protocol whenever a task arrives
     * <ol>
     *     <li>Check if the worker is paused (because repository is offline) and fail early if so.</li>
     *     <li>Set the state of the task to RUNNING in the database if it is received from the broker.
     *     This might fail if another worker is requesting the task in between. If the task fails, request a new task.</li>
     *     <li>Run the task. If the execution fails for whatever reason, set the state to FAILED and request a new task.</li>
     *     <li>If the task completes, check if the worker is paused. If it paused, complete with a failure and don't
     *     request a new task. Set the task COMPLETED otherwise and request a new task.</li>
     * </ol>
     * @param message the task payload
     * @param taskOrigin the origin
     * @return the result of the current task process
     */
    private Future<Void> processTask(JsonObject message, TaskOrigin taskOrigin) {
        UUID taskId = getId(message);
        JsonObject payload = getPayload(message);
        Future<TaskProcessState> setRunningStep = Future.succeededFuture(TaskProcessState.SET_RUNNING_OK);
        if(isPaused()){
            if(taskOrigin.equals(TaskOrigin.REPOSITORY)){
                //the task has been marked RUNNING in the database and then repository went down
                taskRequestedWhilePaused(taskId,message);
            }else{
                //we've received a new task from the broker - just don't do it
                taskReceivedWhilePaused(taskId, message);
            }
            return currentWork = failOnPausedWorker();
        }else if(taskOrigin.equals(TaskOrigin.BROKER)){
            //set to running state. This might fail in case two worker are racing
            setRunningStep = repository.updateTask(taskId.toString(), TaskStatus.RUNNING, TaskStatus.NEW)
                    .map(v -> TaskProcessState.SET_RUNNING_OK)
                    .recover(x -> Future.succeededFuture(TaskProcessState.SET_RUNNING_FAILED));
        }
        return currentWork = setRunningStep
                //do the work and set the RUN_OK status
                .compose(state->{
                    if(state.equals(TaskProcessState.SET_RUNNING_OK)){
                        return run(payload).map(v2-> TaskProcessState.RUN_OK).recover(failTask(taskId,message));
                    }
                    return Future.succeededFuture(state);
                })
                .compose(state->{
                    if(state.equals(TaskProcessState.RUN_OK) && isPaused()){
                        //we completed our task, but now the repository is no longer available
                        taskCompletedWhilePaused(taskId,message);
                        return failOnPausedWorker().map(v -> TaskProcessState.PAUSED);
                    }
                    return Future.succeededFuture(state);
                })
                //complete the task by setting the new status. If this worker is paused it shouldn't come to this point
                .compose(state -> {
                    if(state.equals(TaskProcessState.RUN_OK)){
                        return repository.updateTask(taskId.toString(),TaskStatus.COMPLETED,TaskStatus.RUNNING).map(v-> TaskProcessState.COMPLETED);
                    }
                    return Future.succeededFuture(state);
                })
                .compose(state -> {
                    boolean isCompleted = state.equals(TaskProcessState.COMPLETED);
                    boolean otherWorkerWon = state.equals(TaskProcessState.SET_RUNNING_FAILED);
                    boolean currentExecutionFailed = state.equals(TaskProcessState.RUN_FAILED);
                    if(isCompleted
                            || otherWorkerWon
                            || currentExecutionFailed){
                        return requestNewTask();
                    }
                    return Future.succeededFuture();
                });
    }

    protected Future<Void> failOnPausedWorker() {
        return Future.failedFuture("Worker is paused");
    }

    private Function<Throwable, Future<TaskProcessState>> failTask(UUID taskId, JsonObject message) {
        return ex -> {
            logger.error("Failed running task "+ex, ex);
            if(isPaused()){
                taskFailedWhilePaused(taskId,message);
                //because failOnPausedWorker returns a failed future, TaskProcessState will never be set
                return failOnPausedWorker().map(v-> TaskProcessState.PAUSED);
            }
            return repository.failTask(taskId.toString(), ex.getMessage()).map(v-> TaskProcessState.RUN_FAILED);
        };
    }

    private Future<Void> requestNewTask() {
        if(isPaused() || limbo.get()){
            return Future.succeededFuture();
        }
        return repository.requestTask(label).compose(newTask -> {
            if(newTask == null){
                return Future.succeededFuture();
            }
            return processTask(newTask,TaskOrigin.REPOSITORY);
        });
    }

    private JsonObject getPayload(JsonObject message) {
        return message.getJsonObject("payload", new JsonObject());
    }

    private UUID getId(JsonObject message){
        return UUID.fromString(message.getString("id"));
    }

    class TaskReceivedHandler implements Handler<Message<JsonObject>>{

        @Override
        public void handle(Message<JsonObject> message) {
            try {
                if (!running.getAndSet(true)) {
                    processTask(message.body(),TaskOrigin.BROKER)
                            //if failed or not, we need to reset the running state
                            .onComplete(res -> running.set(false))
                            //log any exception
                            .onFailure(ex -> logger.error(ex.getMessage(),ex));
                } else {
                    logger.debug("Rejecting task, already running.");
                }
            } catch (Throwable e) {
                logger.error(e.getMessage(), e);
                running.set(false);
            }
        }
    }

}
