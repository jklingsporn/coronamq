package de.badmonkee.coronamq.examples.complete;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;

/**
 * @author jensklingsporn
 */
public class WorkerVerticle extends AbstractVerticle {

    private DelayedWorker delayedWorker;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        delayedWorker = new DelayedWorker(vertx,new CoronaMqOptions());
        delayedWorker.start()
                .onComplete(startFuture);
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        delayedWorker.stop()
                .onComplete(stopFuture);
    }
}
