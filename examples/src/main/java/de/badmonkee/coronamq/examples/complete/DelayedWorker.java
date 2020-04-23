package de.badmonkee.coronamq.examples.complete;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import de.badmonkee.coronamq.core.impl.AbstractWorker;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * @author jensklingsporn
 */
class DelayedWorker extends AbstractWorker {

    public DelayedWorker(Vertx vertx, CoronaMqOptions coronaMqOptions) {
        super(vertx, coronaMqOptions, "delayed");
    }

    @Override
    public Future<Void> run(JsonObject task) {
        Promise<Void> promise = Promise.promise();
        //simulate work
        vertx.setTimer(task.getLong("delay"),e->promise.complete());
        return promise.future();
    }
}
