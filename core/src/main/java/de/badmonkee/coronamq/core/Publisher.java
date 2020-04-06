package de.badmonkee.coronamq.core;

import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

/**
 * Any instance that can publish tasks to the queue.
 */
public interface Publisher {

    /**
     * Publish a task with the given label and payload
     * @param label the label
     * @param payload the payload
     * @return a {@link Future} with the id associated with task.
     */
    public Future<UUID> publishTask(String label, JsonObject payload);

}
