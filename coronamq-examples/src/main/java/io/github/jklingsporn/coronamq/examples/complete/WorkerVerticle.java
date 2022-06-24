package io.github.jklingsporn.coronamq.examples.complete;

import io.github.jklingsporn.coronamq.core.CoronaMqOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;

/**
 * @author jensklingsporn
 */
public class WorkerVerticle extends AbstractVerticle {

    private DelayedWorker delayedWorker;

    @Override
    public void start(Promise<Void> startFuture) throws Exception {
        delayedWorker = new DelayedWorker(vertx,new CoronaMqOptions());
        delayedWorker.start()
                .onComplete(startFuture);
    }

    @Override
    public void stop(Promise<Void> stopFuture) throws Exception {
        delayedWorker.stop()
                .onComplete(stopFuture);
    }
}
