package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import de.badmonkee.coronamq.core.TaskQueueDao;
import de.badmonkee.coronamq.core.TaskStatus;
import de.badmonkee.coronamq.core.Worker;
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
        DAO
    }

    private static final Logger logger = LoggerFactory.getLogger(AbstractWorker.class);

    protected final Vertx vertx;
    private final CoronaMqOptions coronaMqOptions;
    private final String label;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final AtomicBoolean paused = new AtomicBoolean(false);
    /**
     * Signals that the DAO is about to shut down. The worker can try to finish his task but shouldn't
     * accept and request more tasks after that.
     */
    private final AtomicBoolean limbo = new AtomicBoolean(false);
    private final TaskQueueDao dao;
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
        this.dao = TaskQueueDao.createProxy(vertx,coronaMqOptions.getDaoAddress());
        this.serviceDiscovery = ServiceDiscovery.create(vertx, coronaMqOptions.getServiceDiscoveryOptions());
    }

    @Override
    public Future<Void> start() {
        //Listen for tasks that are sent from the broker
        return registerTaskReceivedHandler()
                //Listen for availability changes from the dao
                .onSuccess(v->registerToServiceDiscovery())
                //Check once if the dao is currently available
                .compose(v->checkForDaoAvailability())
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
        //listen for DAO discovery
        discoveryHandler = vertx.eventBus().consumer(coronaMqOptions.getServiceDiscoveryOptions().getAnnounceAddress(), msg -> {
            Record discoverEvent = new Record(msg.body());
            if(Internal.DAO_SERVICE_RECORD_NAME.equals(discoverEvent.getName())){
                if(discoverEvent.getStatus() == Status.UP){
                    onDaoUP();
                }else if(discoverEvent.getStatus().equals(Status.DOWN) && discoverEvent.getMetadata().getLong("shutdownInMillis") != null){
                    onDaoGracefulShutdown(discoverEvent.getMetadata().getLong("shutdownInMillis"));
                }else{
                    onDaoDown();
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
     * The DAO went away.
     */
    protected void onDaoDown(){
        logger.debug("Worker paused");
        paused.set(true);
    }

    /**
     * The DAO will go away in X seconds. No longer request new tasks.
     */
    protected void onDaoGracefulShutdown(long shutdownMillis){
        logger.debug("Worker graceful shutdown");
        limbo.set(true);
    }

    /**
     * The DAO is (back) up
     */
    protected void onDaoUP(){
        logger.debug("Worker resumed");
        paused.set(false);
        limbo.set(false);
    }

    /**
     * While this worker has received a task from the eventbus (broker), the DAO went away
     * @param taskId
     * @param message
     */
    protected void taskReceivedWhilePaused(UUID taskId, JsonObject message){
        logger.debug("Task {} received while worker is paused",taskId);
    }

    /**
     * Right after this worker has received a task from the DAO, the DAO went away. This leaves the
     * task in a bad condition as it is marked RUNNING but nothing is actually happening.
     * TODO: add tasks to a list which is processed when the DAO is available again
     * @param taskId
     * @param message
     */
    protected void taskRequestedWhilePaused(UUID taskId, JsonObject message){
        logger.debug("Task {} requested while worker is paused",taskId);
    }

    /**
     * Right after this worker has completed a task, the DAO went away. This leaves the
     * task in a bad condition as it is marked RUNNING and ready to be set COMPLETED.
     * TODO: add tasks to a list which is processed when the DAO is available again
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

    private Future<@Nullable Record> checkForDaoAvailability() {
        return serviceDiscovery.getRecord(rec -> Internal.DAO_SERVICE_RECORD_NAME.equals(rec.getName()))
                .onComplete(rec -> {
                    if(rec.result()==null || rec.result().getStatus() != Status.UP){
                        onDaoDown();
                    }else{
                        onDaoUP();
                    }
                });
    }

    private Future<Void> handleWork(JsonObject message, TaskOrigin taskOrigin) {
        UUID taskId = getId(message);
        JsonObject payload = getPayload(message);
        Future<Void> setRunningStep = Future.succeededFuture();
        if(isPaused()){
            if(taskOrigin.equals(TaskOrigin.DAO)){
                //the task has been marked RUNNING in the database and then DAO went down
                taskRequestedWhilePaused(taskId,message);
            }else{
                //we've received a new task from the broker - just don't do it
                taskReceivedWhilePaused(taskId, message);
            }
            return currentWork = failOnPausedWorker();
        }else if(taskOrigin.equals(TaskOrigin.BROKER)){
            //set to running state. This might fail in case two worker are racing
            setRunningStep = dao.updateTask(taskId.toString(), TaskStatus.RUNNING, TaskStatus.NEW);
        }
        return currentWork = setRunningStep
                //do the work and set the task in FAILED state in case of exceptions
                .compose(v->run(payload).recover(failTask(taskId,message)))
                .compose(v->{
                    if(isPaused()){
                        //we completed our task, but now the DAO is no longer available
                        taskCompletedWhilePaused(taskId,message);
                        return failOnPausedWorker();
                    }
                    return Future.succeededFuture();
                })
                //complete the task by setting the new status. If this worker is paused it shouldn't come to this point
                .compose(v -> dao.updateTask(taskId.toString(),TaskStatus.COMPLETED,TaskStatus.RUNNING))
                .compose(v-> requestNewTask());
    }

    protected Future<Void> failOnPausedWorker() {
        return Future.failedFuture("Worker is paused");
    }

    private Function<Throwable, Future<Void>> failTask(UUID taskId, JsonObject message) {
        return ex -> {
            logger.error("Failed running task "+ex, ex);
            if(isPaused()){
                taskFailedWhilePaused(taskId,message);
                return failOnPausedWorker();
            }
            return dao.failTask(taskId.toString(), ex.getMessage()).mapEmpty();
        };
    }

    private Future<Void> requestNewTask() {
        if(isPaused() || limbo.get()){
            return Future.succeededFuture();
        }
        return dao.requestTask(label).compose(newTask -> {
            if(newTask == null){
                return Future.succeededFuture();
            }
            return handleWork(newTask,TaskOrigin.DAO);
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
                    handleWork(message.body(),TaskOrigin.BROKER)
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
