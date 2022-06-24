package io.github.jklingsporn.coronamq.core.mutiny;

import io.smallrye.common.annotation.CheckReturnValue;
import io.vertx.core.json.JsonObject;
import io.github.jklingsporn.coronamq.core.TaskStatus;

/**
 * Necessary actions to operate with the task queue.
 *
 * NOTE: This class has been automatically generated from the {@link io.github.jklingsporn.coronamq.core.TaskRepository original} non Mutiny-ified interface using Vert.x codegen.
 */

@io.smallrye.mutiny.vertx.MutinyGen(io.github.jklingsporn.coronamq.core.TaskRepository.class)
public class TaskRepository {

  public static final io.smallrye.mutiny.vertx.TypeArg<TaskRepository> __TYPE_ARG = new io.smallrye.mutiny.vertx.TypeArg<>(    obj -> new TaskRepository((io.github.jklingsporn.coronamq.core.TaskRepository) obj),
    TaskRepository::getDelegate
  );

  private final io.github.jklingsporn.coronamq.core.TaskRepository delegate;
  
  public TaskRepository(io.github.jklingsporn.coronamq.core.TaskRepository delegate) {
    this.delegate = delegate;
  }

  public TaskRepository(Object delegate) {
    this.delegate = (io.github.jklingsporn.coronamq.core.TaskRepository)delegate;
  }

  /**
   * Empty constructor used by CDI, do not use this constructor directly.
   **/
  TaskRepository() {
    this.delegate = null;
  }

  public io.github.jklingsporn.coronamq.core.TaskRepository getDelegate() {
    return delegate;
  }

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TaskRepository that = (TaskRepository) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static TaskRepository createProxy(io.vertx.mutiny.core.Vertx vertx, String address) {
    TaskRepository ret = TaskRepository.newInstance((io.github.jklingsporn.coronamq.core.TaskRepository) io.github.jklingsporn.coronamq.core.TaskRepository.createProxy(vertx.getDelegate(), address));
    return ret;
  }

  /**
   * Create a new task in the queue after a task has been dispatched.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param label the label
   * @param payload the payload
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<String> createTask(String label, JsonObject payload) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.createTask(label, payload));}

  /**
   * Blocking variant of {@link TaskRepository#createTask(String,JsonObject)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param label the label
   * @param payload the payload
   * @return the String instance produced by the operation.
   */
  public String createTaskAndAwait(String label, JsonObject payload) { 
    return createTask(label, payload).await().indefinitely();
  }


  /**
   * Variant of {@link TaskRepository#createTask(String,JsonObject)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link TaskRepository#createTask(String,JsonObject)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link TaskRepository#createTask(String,JsonObject)} but you don't need to compose it with other operations.
   * @param label the label
   * @param payload the payload
   */
  public void createTaskAndForget(String label, JsonObject payload) { 
    createTask(label, payload).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * Fails a running task.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param id the unique id of the task
   * @param reason the reason why the task failed
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Void> failTask(String id, String reason) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.failTask(id, reason));}

  /**
   * Blocking variant of {@link TaskRepository#failTask(String,String)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param id the unique id of the task
   * @param reason the reason why the task failed
   * @return the Void instance produced by the operation.
   */
  public Void failTaskAndAwait(String id, String reason) { 
    return failTask(id, reason).await().indefinitely();
  }


  /**
   * Variant of {@link TaskRepository#failTask(String,String)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link TaskRepository#failTask(String,String)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link TaskRepository#failTask(String,String)} but you don't need to compose it with other operations.
   * @param id the unique id of the task
   * @param reason the reason why the task failed
   */
  public void failTaskAndForget(String id, String reason) { 
    failTask(id, reason).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * Update the status of a task based on it's id and current status.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param id the unique id of the task
   * @param newStatus the new status of the task
   * @param oldStatus the expected old status
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Void> updateTask(String id, TaskStatus newStatus, TaskStatus oldStatus) {
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.updateTask(id, newStatus, oldStatus));}

  /**
   * Blocking variant of {@link TaskRepository#updateTask(String,TaskStatus,TaskStatus)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param id the unique id of the task
   * @param newStatus the new status of the task
   * @param oldStatus the expected old status
   * @return the Void instance produced by the operation.
   */
  public Void updateTaskAndAwait(String id, TaskStatus newStatus, TaskStatus oldStatus) {
    return updateTask(id, newStatus, oldStatus).await().indefinitely();
  }


  /**
   * Variant of {@link TaskRepository#updateTask(String,TaskStatus,TaskStatus)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link TaskRepository#updateTask(String,TaskStatus,TaskStatus)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link TaskRepository#updateTask(String,TaskStatus,TaskStatus)} but you don't need to compose it with other operations.
   * @param id the unique id of the task
   * @param newStatus the new status of the task
   * @param oldStatus the expected old status
   */
  public void updateTaskAndForget(String id, TaskStatus newStatus, TaskStatus oldStatus) {
    updateTask(id, newStatus, oldStatus).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * Atomically requests a new task from the queue.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param label the label
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<JsonObject> requestTask(String label) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.requestTask(label));}

  /**
   * Blocking variant of {@link TaskRepository#requestTask(String)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param label the label
   * @return the JsonObject instance produced by the operation.
   */
  public JsonObject requestTaskAndAwait(String label) { 
    return requestTask(label).await().indefinitely();
  }


  /**
   * Variant of {@link TaskRepository#requestTask(String)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link TaskRepository#requestTask(String)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link TaskRepository#requestTask(String)} but you don't need to compose it with other operations.
   * @param label the label
   */
  public void requestTaskAndForget(String label) { 
    requestTask(label).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param id the id.
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<JsonObject> getTask(String id) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.getTask(id));}

  /**
   * Blocking variant of {@link TaskRepository#getTask(String)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param id the id.
   * @return the JsonObject instance produced by the operation.
   */
  public JsonObject getTaskAndAwait(String id) { 
    return getTask(id).await().indefinitely();
  }


  /**
   * Variant of {@link TaskRepository#getTask(String)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link TaskRepository#getTask(String)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link TaskRepository#getTask(String)} but you don't need to compose it with other operations.
   * @param id the id.
   */
  public void getTaskAndForget(String id) { 
    getTask(id).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param label the label
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<JsonObject> countTasks(String label) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.countTasks(label));}

  /**
   * Blocking variant of {@link TaskRepository#countTasks(String)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param label the label
   * @return the JsonObject instance produced by the operation.
   */
  public JsonObject countTasksAndAwait(String label) { 
    return countTasks(label).await().indefinitely();
  }


  /**
   * Variant of {@link TaskRepository#countTasks(String)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link TaskRepository#countTasks(String)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link TaskRepository#countTasks(String)} but you don't need to compose it with other operations.
   * @param label the label
   */
  public void countTasksAndForget(String label) { 
    countTasks(label).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param id the id.
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Integer> deleteTask(String id) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.deleteTask(id));}

  /**
   * Blocking variant of {@link TaskRepository#deleteTask(String)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param id the id.
   * @return the Integer instance produced by the operation.
   */
  public Integer deleteTaskAndAwait(String id) { 
    return deleteTask(id).await().indefinitely();
  }


  /**
   * Variant of {@link TaskRepository#deleteTask(String)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link TaskRepository#deleteTask(String)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link TaskRepository#deleteTask(String)} but you don't need to compose it with other operations.
   * @param id the id.
   */
  public void deleteTaskAndForget(String id) { 
    deleteTask(id).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * Asynchronously starts this repository.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Void> start() { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.start());}

  /**
   * Blocking variant of {@link TaskRepository#start}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @return the Void instance produced by the operation.
   */
  public Void startAndAwait() { 
    return start().await().indefinitely();
  }


  /**
   * Variant of {@link TaskRepository#start} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link TaskRepository#start}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link TaskRepository#start} but you don't need to compose it with other operations.
   */
  public void startAndForget() { 
    start().subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * Asynchronously stops this repository.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Void> stop() { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.stop());}

  /**
   * Blocking variant of {@link TaskRepository#stop}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @return the Void instance produced by the operation.
   */
  public Void stopAndAwait() { 
    return stop().await().indefinitely();
  }


  /**
   * Variant of {@link TaskRepository#stop} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link TaskRepository#stop}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link TaskRepository#stop} but you don't need to compose it with other operations.
   */
  public void stopAndForget() { 
    stop().subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  public static  TaskRepository newInstance(io.github.jklingsporn.coronamq.core.TaskRepository arg) {
    return arg != null ? new TaskRepository(arg) : null;
  }

}
