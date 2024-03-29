package io.github.jklingsporn.coronamq.core.bootstrap;

import io.github.jklingsporn.coronamq.core.Broker;
import io.github.jklingsporn.coronamq.core.TaskRepository;
import io.github.jklingsporn.coronamq.core.Worker;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;

/**
 * It is advisable to start the Broker, Workers and repositories in the correct order. Single node setups required at least
 * a repository and a broker while workers can be added before or after the queue has been "spread" (started).
 */
@VertxGen
public interface Bootstrap {

    /**
     * Adds a {@code TaskRepository} with default configuration to this Bootstrap.
     * @return a reference to this
     */
    @Fluent
    public Bootstrap withRepository();

    /**
     * Adds a {@code TaskRepository} to this Bootstrap.
     * @param repository the repository
     * @return a reference to this
     */
    @Fluent
    public Bootstrap withRepository(TaskRepository repository);

    /**
     * Adds a {@code Broker} with default configuration to this Bootstrap.
     * @return a reference to this
     */
    @Fluent
    public Bootstrap withBroker();

    /**
     * Adds a {@code Broker} to this Bootstrap.
     * @param broker
     * @return a reference to this
     */
    @Fluent
    public Bootstrap withBroker(Broker broker);
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
