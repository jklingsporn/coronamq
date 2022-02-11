package de.badmonkee.coronamq.core.bootstrap;

import de.badmonkee.coronamq.core.Worker;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.core.Future;

public interface BootstrapInitStep {

    /**
     * Adds a Worker to this bootstrap.
     * @param worker a worker
     * @return
     */
    @Fluent
    public BootstrapInitStep withWorker(Worker worker);

    /**
     * Registers and starts all services in the right order.
     * @return
     */
    public Future<BootstrapSpreadStep> spread();


}
