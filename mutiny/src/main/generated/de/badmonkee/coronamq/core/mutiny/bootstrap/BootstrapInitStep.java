package de.badmonkee.coronamq.core.mutiny.bootstrap;

import de.badmonkee.coronamq.core.bootstrap.Bootstrap;
import io.smallrye.mutiny.vertx.TypeArg;
import io.vertx.codegen.annotations.Fluent;
import io.smallrye.common.annotation.CheckReturnValue;

@io.smallrye.mutiny.vertx.MutinyGen(Bootstrap.class)
public class BootstrapInitStep {

  public static final io.smallrye.mutiny.vertx.TypeArg<BootstrapInitStep> __TYPE_ARG = new io.smallrye.mutiny.vertx.TypeArg<>(    obj -> new BootstrapInitStep((Bootstrap) obj),
    BootstrapInitStep::getDelegate
  );

  private final Bootstrap delegate;
  
  public BootstrapInitStep(Bootstrap delegate) {
    this.delegate = delegate;
  }

  public BootstrapInitStep(Object delegate) {
    this.delegate = (Bootstrap)delegate;
  }

  /**
   * Empty constructor used by CDI, do not use this constructor directly.
   **/
  BootstrapInitStep() {
    this.delegate = null;
  }

  public Bootstrap getDelegate() {
    return delegate;
  }

  static final io.smallrye.mutiny.vertx.TypeArg<de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep> TYPE_ARG_0 = new TypeArg<de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep>(o1 -> de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep.newInstance((de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep)o1), o1 -> o1.getDelegate());
  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BootstrapInitStep that = (BootstrapInitStep) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  /**
   * @param worker a worker
   * @return 
   */
  @Fluent
  public de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapInitStep withWorker(de.badmonkee.coronamq.core.mutiny.Worker worker) { 
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
  public io.smallrye.mutiny.Uni<de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep> spread() { 
    return io.smallrye.mutiny.vertx.UniHelper.toUni(delegate.spread().map(x -> BootstrapSpreadStep.newInstance(x)));}

  /**
   * Blocking variant of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapInitStep#spread}.
   * <p>
   * This method waits for the completion of the underlying asynchronous operation.
   * If the operation completes successfully, the result is returned, otherwise the failure is thrown (potentially wrapped in a RuntimeException).
   * @return the BootstrapSpreadStep instance produced by the operation.
   */
  public de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapSpreadStep spreadAndAwait() { 
    return spread().await().indefinitely();
  }


  /**
   * Variant of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapInitStep#spread} that ignores the result of the operation.
   * <p>
   * This method subscribes on the result of {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapInitStep#spread}, but discards the outcome (item or failure).
   * This method is useful to trigger the asynchronous operation from {@link de.badmonkee.coronamq.core.mutiny.bootstrap.BootstrapInitStep#spread} but you don't need to compose it with other operations.
   */
  public void spreadAndForget() { 
    spread().subscribe().with(io.smallrye.mutiny.vertx.UniHelper.NOOP);
  }


  public static  BootstrapInitStep newInstance(Bootstrap arg) {
    return arg != null ? new BootstrapInitStep(arg) : null;
  }

}
