package de.badmonkee.coronamq.core.mutiny;

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
import io.vertx.core.Future;

/**
 * A wrapper around a {@link io.vertx.mutiny.pgclient.pubsub.PgSubscriber} - instance that listens to changes in the tasks table via NOTIFY/LISTEN.
 * There should be only one broker per application.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link de.badmonkee.coronamq.core.Broker original} non Mutiny-ified interface using Vert.x codegen.
 */

@io.smallrye.mutiny.vertx.MutinyGen(de.badmonkee.coronamq.core.Broker.class)
public class Broker {

  public static final io.smallrye.mutiny.vertx.TypeArg<Broker> __TYPE_ARG = new io.smallrye.mutiny.vertx.TypeArg<>(    obj -> new Broker((de.badmonkee.coronamq.core.Broker) obj),
    Broker::getDelegate
  );

  private final de.badmonkee.coronamq.core.Broker delegate;
  
  public Broker(de.badmonkee.coronamq.core.Broker delegate) {
    this.delegate = delegate;
  }

  public Broker(Object delegate) {
    this.delegate = (de.badmonkee.coronamq.core.Broker)delegate;
  }

  /**
   * Empty constructor used by CDI, do not use this constructor directly.
   **/
  Broker() {
    this.delegate = null;
  }

  public de.badmonkee.coronamq.core.Broker getDelegate() {
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
   * Blocking variant of {@link de.badmonkee.coronamq.core.mutiny.Broker#start}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @return the Void instance produced by the operation.
   */
  public Void startAndAwait() { 
    return start().await().indefinitely();
  }


  /**
   * Variant of {@link de.badmonkee.coronamq.core.mutiny.Broker#start} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link de.badmonkee.coronamq.core.mutiny.Broker#start}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link de.badmonkee.coronamq.core.mutiny.Broker#start} but you don't need to compose it with other operations.
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
   * Blocking variant of {@link de.badmonkee.coronamq.core.mutiny.Broker#stop}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @return the Void instance produced by the operation.
   */
  public Void stopAndAwait() { 
    return stop().await().indefinitely();
  }


  /**
   * Variant of {@link de.badmonkee.coronamq.core.mutiny.Broker#stop} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link de.badmonkee.coronamq.core.mutiny.Broker#stop}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link de.badmonkee.coronamq.core.mutiny.Broker#stop} but you don't need to compose it with other operations.
   */
  public void stopAndForget() { 
    stop().subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  public static  Broker newInstance(de.badmonkee.coronamq.core.Broker arg) {
    return arg != null ? new Broker(arg) : null;
  }

}
