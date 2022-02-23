package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.Broker;
import de.badmonkee.coronamq.core.CoronaMqOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.pubsub.PgChannel;
import io.vertx.pgclient.pubsub.PgSubscriber;

/**
 * @author jensklingsporn
 */
class BrokerImpl implements Broker {

    private final Vertx vertx;
    private final PgSubscriber subscriber;
    private final CoronaMqOptions coronaMqOptions;
    private Future<PgChannel> started;

    public BrokerImpl(Vertx vertx, CoronaMqOptions coronamqOptions) {
        this.vertx = vertx;
        this.subscriber = PgSubscriber.subscriber(vertx, coronamqOptions.getConnectOptions());
        this.coronaMqOptions = coronamqOptions;
    }

    @Override
    public Future<Void> start() {
        if(started != null){
            return started.mapEmpty();
        }
        Promise<Void> connect = Promise.promise();
        subscriber.connect(connect);
        started = connect.future().map(v ->
        {
            PgChannel channel = subscriber.channel(coronaMqOptions.getChannelName());
            channel.pause(); //wait for start signal from a repository
            channel
                    .handler(payload -> Internal.sendTask(vertx,coronaMqOptions,new JsonObject(payload)));
            return channel;
        });
        return started
                .mapEmpty()
                ;
    }

    @Override
    public Future<Void> stop() {
        if(started != null && started.isComplete()){
            return subscriber.close();
        }
        return Future.succeededFuture();
    }
}
