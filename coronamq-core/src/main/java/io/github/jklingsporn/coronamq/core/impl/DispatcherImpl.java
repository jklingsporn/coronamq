package io.github.jklingsporn.coronamq.core.impl;

import io.github.jklingsporn.coronamq.core.CoronaMqOptions;
import io.github.jklingsporn.coronamq.core.Dispatcher;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author jensklingsporn
 */
class DispatcherImpl implements Dispatcher {

    private final Vertx vertx;
    private final CoronaMqOptions coronaMqOptions;

    public DispatcherImpl(Vertx vertx, CoronaMqOptions coronaMqOptions) {
        this.vertx = vertx;
        this.coronaMqOptions = coronaMqOptions;
    }

    @Override
    public Future<String> dispatch(String label, JsonObject payload) {
        return CoronaMq.dispatch(vertx, coronaMqOptions.getRepositoryAddress(),label,payload);
    }
}
