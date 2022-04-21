package io.github.jklingsporn.coronamq.core.mutiny;

import io.smallrye.common.annotation.CheckReturnValue;
import io.vertx.core.json.JsonObject;

/**
 * Any instance that can dispatch tasks to the queue.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.github.jklingsporn.coronamq.core.Dispatcher original} non Mutiny-ified interface using Vert.x codegen.
 */

@io.smallrye.mutiny.vertx.MutinyGen(io.github.jklingsporn.coronamq.core.Dispatcher.class)
public class Dispatcher {

  public static final io.smallrye.mutiny.vertx.TypeArg<Dispatcher> __TYPE_ARG = new io.smallrye.mutiny.vertx.TypeArg<>(    obj -> new Dispatcher((io.github.jklingsporn.coronamq.core.Dispatcher) obj),
    Dispatcher::getDelegate
  );

  private final io.github.jklingsporn.coronamq.core.Dispatcher delegate;
  
  public Dispatcher(io.github.jklingsporn.coronamq.core.Dispatcher delegate) {
    this.delegate = delegate;
  }

  public Dispatcher(Object delegate) {
    this.delegate = (io.github.jklingsporn.coronamq.core.Dispatcher)delegate;
  }

  /**
   * Empty constructor used by CDI, do not use this constructor directly.
   **/
  Dispatcher() {
    this.delegate = null;
  }

  public io.github.jklingsporn.coronamq.core.Dispatcher getDelegate() {
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
    Dispatcher that = (Dispatcher) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  /**
   * Dispatch a task with the given label and payload
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param label the label
   * @param payload the payload
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<String> dispatch(String label, JsonObject payload) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.dispatch(label, payload));}

  /**
   * Blocking variant of {@link Dispatcher#dispatch(String,JsonObject)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param label the label
   * @param payload the payload
   * @return the String instance produced by the operation.
   */
  public String dispatchAndAwait(String label, JsonObject payload) { 
    return dispatch(label, payload).await().indefinitely();
  }


  /**
   * Variant of {@link Dispatcher#dispatch(String,JsonObject)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link Dispatcher#dispatch(String,JsonObject)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link Dispatcher#dispatch(String,JsonObject)} but you don't need to compose it with other operations.
   * @param label the label
   * @param payload the payload
   */
  public void dispatchAndForget(String label, JsonObject payload) { 
    dispatch(label, payload).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  public static  Dispatcher newInstance(io.github.jklingsporn.coronamq.core.Dispatcher arg) {
    return arg != null ? new Dispatcher(arg) : null;
  }

}
