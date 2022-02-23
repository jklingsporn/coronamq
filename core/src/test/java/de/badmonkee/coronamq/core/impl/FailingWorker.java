package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import de.badmonkee.coronamq.core.impl.AbstractWorker;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

class FailingWorker extends AbstractWorker {

    Promise<Void> completion = Promise.promise();

    public FailingWorker(Vertx vertx,
                         CoronaMqOptions coronaMqOptions) {
        super(vertx, coronaMqOptions, "test");
    }

    @Override
    public Future<Void> run(JsonObject task) {
        completion.fail("Work has failed");
        return completion.future();
    }

    @Override
    protected Future<Void> getCurrentWork() {
        //we need to make sure that super.getCurrentWork is not just returning the default succeeded future
        return completion.future().recover(v -> super.getCurrentWork());
    }
}
