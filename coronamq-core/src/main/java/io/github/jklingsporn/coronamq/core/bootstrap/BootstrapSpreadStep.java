package io.github.jklingsporn.coronamq.core.bootstrap;

import io.github.jklingsporn.coronamq.core.Broker;
import io.github.jklingsporn.coronamq.core.TaskRepository;
import io.github.jklingsporn.coronamq.core.Worker;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.List;

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

    /**
     * @return the broker or null.
     */
    public Broker getBroker();

    /**
     * @return the repository or null
     */
    public TaskRepository getRepository();

    /**
     * @return all current workers, never null.
     */
    public List<Worker> getWorkers();

}
