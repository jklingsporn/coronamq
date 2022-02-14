package de.badmonkee.coronamq.core.bootstrap;

import de.badmonkee.coronamq.core.Worker;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

@VertxGen
public interface BootstrapSpreadStep {

    /**
     * Adds and deploys a Worker to this boostrap.
     * @param worker a worker
     * @return the <code>Future</code> that completes when the worker is registered to the EventBus.
     */
    public Future<BootstrapSpreadStep> addWorker(Worker worker);

    /**
     * Removes a Worker from this boostrap and unregisters it from the EventBus.
     * @param worker a worker
     * @return the <code>Future</code> that completes when the worker is unregistered from the EventBus.
     */
    public Future<BootstrapSpreadStep> removeWorker(Worker worker);

    /**
     * Dispatches a task given this bootstrap's configuration
     * @param label the task's label
     * @param payload the task's payload
     * @return the id of the task.
     */
    public Future<String> dispatch(String label, JsonObject payload);

    /**
     * Stops all services in the right order.
     * @return a <code>Future</code> that completes once all participants have been unregistered.
     */
    public Future<Void> vaccinate();

}
