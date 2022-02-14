package de.badmonkee.coronamq.core.mutiny.bootstrap;

import java.util.Map;
import java.util.stream.Collectors;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import java.util.function.Consumer;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Publisher;
import io.smallrye.mutiny.vertx.TypeArg;
import io.vertx.codegen.annotations.Fluent;
import io.smallrye.common.annotation.CheckReturnValue;
import io.vertx.core.json.JsonObject;
import io.vertx.core.Future;

@io.smallrye.mutiny.vertx.MutinyGen(de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep.class)
public class BootstrapSpreadStep {

  public static final io.smallrye.mutiny.vertx.TypeArg<BootstrapSpreadStep> __TYPE_ARG = new io.smallrye.mutiny.vertx.TypeArg<>(    obj -> new BootstrapSpreadStep((de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep) obj),
    BootstrapSpreadStep::getDelegate
  );

  private final de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep delegate;
  
  public BootstrapSpreadStep(de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep delegate) {
    this.delegate = delegate;
  }

  public BootstrapSpreadStep(Object delegate) {
    this.delegate = (de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep)delegate;
  }

  /**
   * Empty constructor used by CDI, do not use this constructor directly.
   **/
  BootstrapSpreadStep() {
    this.delegate = null;
  }

  public de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep getDelegate() {
    return delegate;
  }

  static final io.smallrye.mutiny.vertx.TypeArg<de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep> TYPE_ARG_0 = new TypeArg<de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep>(o1 -> de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep.newInstance((de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep)o1), o1 -> o1.getDelegate());
  static final io.smallrye.mutiny.vertx.TypeArg<de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep> TYPE_ARG_1 = new TypeArg<de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep>(o1 -> de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep.newInstance((de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep)o1), o1 -> o1.getDelegate());
  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BootstrapSpreadStep that = (BootstrapSpreadStep) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  /**
   * Adds and deploys a Worker to this boostrap.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param worker a worker
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep> addWorker(de.badmonkee.coronamq.core.mutiny.Worker worker) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.addWorker(worker.getDelegate()).map(x -> BootstrapSpreadStep.newInstance(x)));}

  /**
   * Blocking variant of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#addWorker(de.badmonkee.coronamq.core.mutiny.Worker)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param worker a worker
   * @return the BootstrapSpreadStep instance produced by the operation.
   */
  public de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep addWorkerAndAwait(de.badmonkee.coronamq.core.mutiny.Worker worker) { 
    return addWorker(worker).await().indefinitely();
  }


  /**
   * Variant of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#addWorker(de.badmonkee.coronamq.core.mutiny.Worker)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#addWorker(de.badmonkee.coronamq.core.mutiny.Worker)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#addWorker(de.badmonkee.coronamq.core.mutiny.Worker)} but you don't need to compose it with other operations.
   * @param worker a worker
   */
  public void addWorkerAndForget(de.badmonkee.coronamq.core.mutiny.Worker worker) { 
    addWorker(worker).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * Removes a Worker from this boostrap and unregisters it from the EventBus.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param worker a worker
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep> removeWorker(de.badmonkee.coronamq.core.mutiny.Worker worker) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.removeWorker(worker.getDelegate()).map(x -> BootstrapSpreadStep.newInstance(x)));}

  /**
   * Blocking variant of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#removeWorker(de.badmonkee.coronamq.core.mutiny.Worker)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param worker a worker
   * @return the BootstrapSpreadStep instance produced by the operation.
   */
  public de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep removeWorkerAndAwait(de.badmonkee.coronamq.core.mutiny.Worker worker) { 
    return removeWorker(worker).await().indefinitely();
  }


  /**
   * Variant of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#removeWorker(de.badmonkee.coronamq.core.mutiny.Worker)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#removeWorker(de.badmonkee.coronamq.core.mutiny.Worker)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#removeWorker(de.badmonkee.coronamq.core.mutiny.Worker)} but you don't need to compose it with other operations.
   * @param worker a worker
   */
  public void removeWorkerAndForget(de.badmonkee.coronamq.core.mutiny.Worker worker) { 
    removeWorker(worker).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * Dispatches a task given this bootstrap's configuration
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param label the task's label
   * @param payload the task's payload
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<String> dispatch(String label, JsonObject payload) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.dispatch(label, payload));}

  /**
   * Blocking variant of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#dispatch(String,JsonObject)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param label the task's label
   * @param payload the task's payload
   * @return the String instance produced by the operation.
   */
  public String dispatchAndAwait(String label, JsonObject payload) { 
    return dispatch(label, payload).await().indefinitely();
  }


  /**
   * Variant of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#dispatch(String,JsonObject)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#dispatch(String,JsonObject)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#dispatch(String,JsonObject)} but you don't need to compose it with other operations.
   * @param label the task's label
   * @param payload the task's payload
   */
  public void dispatchAndForget(String label, JsonObject payload) { 
    dispatch(label, payload).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * Stops all services in the right order.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Void> vaccinate() { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.vaccinate());}

  /**
   * Blocking variant of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#vaccinate}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @return the Void instance produced by the operation.
   */
  public Void vaccinateAndAwait() { 
    return vaccinate().await().indefinitely();
  }


  /**
   * Variant of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#vaccinate} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#vaccinate}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep#vaccinate} but you don't need to compose it with other operations.
   */
  public void vaccinateAndForget() { 
    vaccinate().subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  public static  BootstrapSpreadStep newInstance(de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep arg) {
    return arg != null ? new BootstrapSpreadStep(arg) : null;
  }

}
