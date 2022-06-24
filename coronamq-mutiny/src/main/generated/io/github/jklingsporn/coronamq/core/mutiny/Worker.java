package io.github.jklingsporn.coronamq.core.mutiny;

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

/**
 * A worker to work on tasks from the task queue. A worker is bound to a 'label' which describes the unit of work of this
 * worker.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.github.jklingsporn.coronamq.core.Worker original} non Mutiny-ified interface using Vert.x codegen.
 */

@io.smallrye.mutiny.vertx.MutinyGen(io.github.jklingsporn.coronamq.core.Worker.class)
public class Worker {

  public static final io.smallrye.mutiny.vertx.TypeArg<Worker> __TYPE_ARG = new io.smallrye.mutiny.vertx.TypeArg<>(    obj -> new Worker((io.github.jklingsporn.coronamq.core.Worker) obj),
    Worker::getDelegate
  );

  private final io.github.jklingsporn.coronamq.core.Worker delegate;
  
  public Worker(io.github.jklingsporn.coronamq.core.Worker delegate) {
    this.delegate = delegate;
  }

  public Worker(Object delegate) {
    this.delegate = (io.github.jklingsporn.coronamq.core.Worker)delegate;
  }

  /**
   * Empty constructor used by CDI, do not use this constructor directly.
   **/
  Worker() {
    this.delegate = null;
  }

  public io.github.jklingsporn.coronamq.core.Worker getDelegate() {
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
    Worker that = (Worker) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  /**
   * Do some async work with the task.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @param task the current task's payload
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Void> run(JsonObject task) { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.run(task));}

  /**
   * Blocking variant of {@link io.github.jklingsporn.coronamq.core.mutiny.Worker#run(JsonObject)}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @param task the current task's payload
   * @return the Void instance produced by the operation.
   */
  public Void runAndAwait(JsonObject task) { 
    return run(task).await().indefinitely();
  }


  /**
   * Variant of {@link io.github.jklingsporn.coronamq.core.mutiny.Worker#run(JsonObject)} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link io.github.jklingsporn.coronamq.core.mutiny.Worker#run(JsonObject)}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link io.github.jklingsporn.coronamq.core.mutiny.Worker#run(JsonObject)} but you don't need to compose it with other operations.
   * @param task the current task's payload
   */
  public void runAndForget(JsonObject task) { 
    run(task).subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Void> start() { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.start());}

  public Void startAndAwait() { 
    return start().await().indefinitely();
  }


  public void startAndForget() { 
    start().subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  @CheckReturnValue
  public io.smallrye.mutiny.Uni<Void> stop() { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.stop());}

  public Void stopAndAwait() { 
    return stop().await().indefinitely();
  }


  public void stopAndForget() { 
    stop().subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  public static  Worker newInstance(io.github.jklingsporn.coronamq.core.Worker arg) {
    return arg != null ? new Worker(arg) : null;
  }

}
