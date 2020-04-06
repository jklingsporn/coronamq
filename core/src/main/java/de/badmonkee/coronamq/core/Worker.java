package de.badmonkee.coronamq.core;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * A worker to work on tasks from the task queue. A worker is bound to a 'label' which describes the unit of work of this
 * worker.
 */
public interface Worker {

    /**
     * Do some async work with the task.
     * @param task the current task's payload
     * @return a {@link Future} after the work is done.
     */
    public Future<Void> run(JsonObject task);

    public Future<Void> start();

    public Future<Void> stop();

}
