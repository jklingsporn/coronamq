package io.github.jklingsporn.coronamq.core.mutiny;

import io.smallrye.common.annotation.CheckReturnValue;

/**
 * A wrapper around a {@link io.vertx.mutiny.pgclient.pubsub.PgSubscriber} - instance that listens to changes in the tasks table via NOTIFY/LISTEN.
 * There should be only one broker per application.
 *
 * NOTE: This class has been automatically generated from the {@link io.github.jklingsporn.coronamq.core.Broker original} non Mutiny-ified interface using Vert.x codegen.
 */

@io.smallrye.mutiny.vertx.MutinyGen(io.github.jklingsporn.coronamq.core.Broker.class)
public class Broker {

  public static final io.smallrye.mutiny.vertx.TypeArg<Broker> __TYPE_ARG = new io.smallrye.mutiny.vertx.TypeArg<>(    obj -> new Broker((io.github.jklingsporn.coronamq.core.Broker) obj),
    Broker::getDelegate
  );

  private final io.github.jklingsporn.coronamq.core.Broker delegate;
  
  public Broker(io.github.jklingsporn.coronamq.core.Broker delegate) {
    this.delegate = delegate;
  }

  public Broker(Object delegate) {
    this.delegate = (io.github.jklingsporn.coronamq.core.Broker)delegate;
  }

  /**
   * Empty constructor used by CDI, do not use this constructor directly.
   **/
  Broker() {
    this.delegate = null;
  }

  public io.github.jklingsporn.coronamq.core.Broker getDelegate() {
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
    Broker that = (Broker) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  /**
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Void> start() { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.start());}

  /**
   * Blocking variant of {@link Broker#start}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @return the Void instance produced by the operation.
   */
  public Void startAndAwait() { 
    return start().await().indefinitely();
  }


  /**
   * Variant of {@link Broker#start} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link Broker#start}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link Broker#start} but you don't need to compose it with other operations.
   */
  public void startAndForget() { 
    start().subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  /**
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Void> stop() { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.stop());}

  /**
   * Blocking variant of {@link Broker#stop}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @return the Void instance produced by the operation.
   */
  public Void stopAndAwait() { 
    return stop().await().indefinitely();
  }


  /**
   * Variant of {@link Broker#stop} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link Broker#stop}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link Broker#stop} but you don't need to compose it with other operations.
   */
  public void stopAndForget() { 
    stop().subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  public static  Broker newInstance(io.github.jklingsporn.coronamq.core.Broker arg) {
    return arg != null ? new Broker(arg) : null;
  }

}
