package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import de.badmonkee.coronamq.core.Publisher;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.UUID;

/**
 * @author jensklingsporn
 */
class PublisherImpl implements Publisher {

    private final Vertx vertx;
    private final CoronaMqOptions coronaMqOptions;

    public PublisherImpl(Vertx vertx, CoronaMqOptions coronaMqOptions) {
        this.vertx = vertx;
        this.coronaMqOptions = coronaMqOptions;
    }

    @Override
    public Future<UUID> publishTask(String label, JsonObject payload) {
        return CoronaMq.publishTask(vertx, coronaMqOptions.getTaskPublishAddress(),label,payload);
    }
}
