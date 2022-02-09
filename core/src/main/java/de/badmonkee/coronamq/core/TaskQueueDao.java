package de.badmonkee.coronamq.core;

import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

/**
 * Necessary actions to operate with the task queue.
 */
@ProxyGen
public interface TaskQueueDao {

    /**
     * Create a new task in the queue after a task has been published.
     * @param label the label
     * @param payload the payload
     * @return  {@link Future} that is completed when the task is added to the queue containing the id associated
     * with the task.
     */
    public Future<UUID> createTask(String label, JsonObject payload);


    /**
     * Fails a running task.
     * @param id the unique id of the task
     * @param reason the reason why the task failed
     * @return a {@link Future} containing the task's update result.
     */
    public Future<Void> failTask(String id, String reason);

    /**
     * Update the status of a task based on it's id and current status.
     * @param id the unique id of the task
     * @param newStatus the new status of the task
     * @param oldStatus the expected old status
     * @return a {@link Future} containing the task's update result.
     */
    public Future<Void> updateTask(String id, TaskStatus newStatus, TaskStatus oldStatus);

    /**
     * Atomically requests a new task from the queue.
     * @param label the label
     * @return a {@link Future} containing a new task or null if there is no task in the queue.
     */
    public Future<JsonObject> requestTask(String label);

    /**
     * @param id the id.
     * @return the task with the given id. If the task does not exist, an exception is raised.
     */
    public Future<JsonObject> getTask(String id);

    /**
     * @param label the label
     * @return the amount of existing tasks.
     */
    public Future<Long> countTasks(String label);

    /**
     * @param id the id.
     * @return a {@link Future} that is completed when the task has been deleted.
     */
    public Future<Integer> deleteTask(String id);

    /**
     * Asynchronously starts this dao.
     * @return a {@link Future} containing the status of the start operation.
     */
    public Future<Void> start();

    /**
     * Asynchronously stops this dao.
     * @return a {@link Future} containing the status of the stop operation.
     */
    public Future<Void> stop();

}
