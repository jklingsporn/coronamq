package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.Broker;
import de.badmonkee.coronamq.core.CoronaMqOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.pubsub.PgSubscriber;

/**
 * @author jensklingsporn
 */
class BrokerImpl implements Broker {

    private final Vertx vertx;
    private final PgSubscriber subscriber;
    private final CoronaMqOptions coronaMqOptions;
    private Future<Void> started;

    public BrokerImpl(Vertx vertx, CoronaMqOptions coronamqOptions) {
        this.vertx = vertx;
        this.subscriber = PgSubscriber.subscriber(vertx, coronamqOptions.getConnectOptions());
        this.coronaMqOptions = coronamqOptions;
    }

    @Override
    public Future<Void> start() {
        Promise<Void> connect = Promise.promise();
        subscriber.connect(connect);
        started = connect.future().onComplete(v -> {
            subscriber
                    .channel(coronaMqOptions.getChannelName())
                    .handler(payload -> {
                        JsonObject task = new JsonObject(payload);
                        vertx.eventBus().send(Internal.toWorkerAddress(coronaMqOptions,task.getString("label")), task);
                    });
        });
        return started;
    }

    @Override
    public Future<Void> stop() {
        if(started != null && started.isComplete()){
            subscriber.close();
        }
        return Future.succeededFuture();
    }
}
