package de.badmonkee.coronamq.core.bootstrap;

import de.badmonkee.coronamq.core.Worker;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;

/**
 * It is advisable to start the Broker, Workers and DAOs in the correct order. For easy setups which include at least
 * the Broker and the DAO, this class can be used. Workers can be added before or after the queue has been "spread"
 * (started).
 */
@VertxGen
public interface Bootstrap {

    /**
     * Adds a Worker to this bootstrap.
     * @param worker a worker
     * @return
     */
    @Fluent
    public Bootstrap withWorker(Worker worker);

    /**
     * Registers and starts all services in the right order.
     * @return
     */
    public Future<BootstrapSpreadStep> spread();


}
