package io.github.jklingsporn.coronamq.core.impl;

import io.github.jklingsporn.coronamq.core.CoronaMqOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.concurrent.atomic.AtomicBoolean;

class SimpleWorker extends AbstractWorker {

    Promise<Void> completion = Promise.promise();
    AtomicBoolean failed = new AtomicBoolean(false);

    public SimpleWorker(Vertx vertx,
                        CoronaMqOptions coronaMqOptions) {
        super(vertx, coronaMqOptions, "test");
    }

    @Override
    public Future<Void> run(JsonObject task) {
        complete();
        return completion.future();
    }

    void complete() {
        completion.complete();
    }

    @Override
    protected Future<Void> getCurrentWork() {
        //we need to make sure that super.getCurrentWork is not just returning the default succeeded future
        return completion.future().compose(v -> super.getCurrentWork());
    }

    @Override
    protected Future<Void> failOnPausedWorker() {
        Future<Void> failed = super.failOnPausedWorker();
        this.failed.set(true);
        Internal.await(vertx,100).onSuccess(v->completion.complete());
        return failed;
    }
}
