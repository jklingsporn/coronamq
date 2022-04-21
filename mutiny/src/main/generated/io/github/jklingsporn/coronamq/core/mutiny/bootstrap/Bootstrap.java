package io.github.jklingsporn.coronamq.core.mutiny.bootstrap;

import io.github.jklingsporn.coronamq.core.mutiny.Broker;
import io.github.jklingsporn.coronamq.core.mutiny.TaskRepository;
import io.github.jklingsporn.coronamq.core.mutiny.Worker;
import io.smallrye.mutiny.vertx.TypeArg;
import io.vertx.codegen.annotations.Fluent;
import io.smallrye.common.annotation.CheckReturnValue;

/**
 * It is advisable to start the Broker, Workers and repositorys in the correct order. Single node setups required at least
 * a repository and a broker while workers can be added before or after the queue has been "spread" (started).
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap original} non Mutiny-ified interface using Vert.x codegen.
 */

@io.smallrye.mutiny.vertx.MutinyGen(io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap.class)
public class Bootstrap {

  public static final io.smallrye.mutiny.vertx.TypeArg<Bootstrap> __TYPE_ARG = new io.smallrye.mutiny.vertx.TypeArg<>(    obj -> new Bootstrap((io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap) obj),
    Bootstrap::getDelegate
  );

  private final io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap delegate;
  
  public Bootstrap(io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap delegate) {
    this.delegate = delegate;
  }

  public Bootstrap(Object delegate) {
    this.delegate = (io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap)delegate;
  }

  /**
   * Empty constructor used by CDI, do not use this constructor directly.
   **/
  Bootstrap() {
    this.delegate = null;
  }

  public io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap getDelegate() {
    return delegate;
  }

  static final io.smallrye.mutiny.vertx.TypeArg<BootstrapSpreadStep> TYPE_ARG_0 = new TypeArg<BootstrapSpreadStep>(o1 -> BootstrapSpreadStep.newInstance((io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep)o1), o1 -> o1.getDelegate());
  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Bootstrap that = (Bootstrap) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  /**
   * @return a reference to this
   */
  @Fluent
  public Bootstrap withRepository() {
    delegate.withRepository();
    return this;
  }

  /**
   * @param repository the repository
   * @return a reference to this
   */
  @Fluent
  public Bootstrap withRepository(TaskRepository repository) {
    delegate.withRepository(repository.getDelegate());
    return this;
  }

  /**
   * @return a reference to this
   */
  @Fluent
  public Bootstrap withBroker() {
    delegate.withBroker();
    return this;
  }

  /**
   * @param broker 
   * @return a reference to this
   */
  @Fluent
  public Bootstrap withBroker(Broker broker) {
    delegate.withBroker(broker.getDelegate());
    return this;
  }

  /**
   * @param worker a worker
   * @return 
   */
  @Fluent
  public Bootstrap withWorker(Worker worker) {
    delegate.withWorker(worker.getDelegate());
    return this;
  }

  /**
   * Registers and starts all services in the right order.
   * <p>
   * Unlike the <em>bare</em> Vert.x variant, this method returns a {@link io.smallrye.mutiny.Uni Uni}.
   * Don't forget to <em>subscribe</em> on it to trigger the operation.
   * @return the {@link io.smallrye.mutiny.Uni uni} firing the result of the operation when completed, or a failure if the operation failed.
   */
  @CheckReturnValue
  public io.smallrye.mutiny.Uni<BootstrapSpreadStep> spread() {
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.spread().map(x -> BootstrapSpreadStep.newInstance(x)));}

  /**
   * Blocking variant of {@link Bootstrap#spread}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @return the BootstrapSpreadStep instance produced by the operation.
   */
  public BootstrapSpreadStep spreadAndAwait() {
    return spread().await().indefinitely();
  }


  /**
   * Variant of {@link Bootstrap#spread} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link Bootstrap#spread}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link Bootstrap#spread} but you don't need to compose it with other operations.
   */
  public void spreadAndForget() { 
    spread().subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  public static  Bootstrap newInstance(io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap arg) {
    return arg != null ? new Bootstrap(arg) : null;
  }

}
