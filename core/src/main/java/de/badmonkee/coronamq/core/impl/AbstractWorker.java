package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import de.badmonkee.coronamq.core.TaskQueueDao;
import de.badmonkee.coronamq.core.TaskStatus;
import de.badmonkee.coronamq.core.Worker;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * A worker that can handle one task at a time. If a task is sent to the worker's eventbus address while the worker
 * is busy, the incoming task is discarded by this worker. On {@link #start()} and after a task has been completed,
 * this worker tries to acquire a new task from the queue.
 */
public abstract class AbstractWorker implements Worker {

    private static final Logger logger = LoggerFactory.getLogger(AbstractWorker.class);

    protected final Vertx vertx;
    private final CoronaMqOptions coronaMqOptions;
    private final String label;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private final TaskQueueDao dao;

    private MessageConsumer<JsonObject> messageConsumer;
    private Future<Void> currentWork = Future.succeededFuture();

    public AbstractWorker(Vertx vertx,
                          CoronaMqOptions coronaMqOptions,
                          String label) {
        this.vertx = vertx;
        this.coronaMqOptions = coronaMqOptions;
        this.label = label;
        this.dao = TaskQueueDao.createProxy(vertx,coronaMqOptions.getDaoAddress());
    }

    @Override
    public Future<Void> start() {
        messageConsumer = vertx.eventBus().<JsonObject>consumer(Internal.toWorkerAddress(coronaMqOptions,label), message -> {
                    try {
                        if (!running.getAndSet(true)) {
                            currentWork = handleWork(message.body(),true)
                                    //if failed or not, we need to reset the running state
                                    .onComplete(res -> running.set(false))
                                    //log any exception
                                    .onFailure(ex -> logger.error(ex.getMessage(),ex))
                            ;
                        } else {
                            logger.debug("Rejecting task, already running.");
                        }
                    } catch (Throwable e) {
                        logger.error(e.getMessage(), e);
                        running.set(false);
                    }
                }
        );
        Promise<Void> registered = Promise.promise();
        messageConsumer.completionHandler(registered);
        return registered.future().compose(v->requestNewTask());
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
        return unregistered.future().compose(v -> currentWork);
    }

    private Future<Void> handleWork(JsonObject message, boolean setRunning) {
            UUID taskId = getId(message);
            JsonObject payload = getPayload(message);
            return (setRunning
                    ? dao.updateTask(taskId.toString(), TaskStatus.RUNNING, TaskStatus.NEW)
                    : Future.<Void>succeededFuture())
                    .compose(v->run(payload))
                    .compose(updatedPayload -> dao.updateTask(taskId.toString(),TaskStatus.COMPLETED,TaskStatus.RUNNING))
                    .compose(v-> requestNewTask())
                    .recover(failTask(taskId));
    }

    private Function<Throwable, Future<Void>> failTask(UUID taskId) {
        return ex -> {
            logger.error("Failed running task "+ex, ex);
            return dao.failTask(taskId.toString(), ex.getMessage()).mapEmpty();
        };
    }

    private Future<Void> requestNewTask() {
        return dao.requestTask(label).compose(newTask -> {
            if(newTask == null){
                return Future.succeededFuture();
            }
            return handleWork(newTask,false);
        });
    }

    private JsonObject getPayload(JsonObject message) {
        return message.getJsonObject("payload", new JsonObject());
    }

    private UUID getId(JsonObject message){
        return UUID.fromString(message.getString("id"));
    }

    protected Future<Void> getCurrentWork() {
        return currentWork;
    }

}
