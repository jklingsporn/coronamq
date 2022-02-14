package de.badmonkee.coronamq.core;

import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

/**
 * Any instance that can dispatch tasks to the queue.
 */
@VertxGen
public interface Dispatcher {

    /**
     * Dispatch a task with the given label and payload
     * @param label the label
     * @param payload the payload
     * @return a {@link Future} with the id associated with task.
     */
    public Future<String> dispatch(String label, JsonObject payload);

}
