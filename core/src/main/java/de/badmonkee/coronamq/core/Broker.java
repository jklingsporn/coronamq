package de.badmonkee.coronamq.core;

import io.vertx.core.Future;

/**
 * A wrapper around a {@link io.vertx.pgclient.pubsub.PgSubscriber} - instance that listens to changes in the tasks table via NOTIFY/LISTEN.
 * There should be only one broker per application.
 */
public interface Broker {

    /**
     * @return a future that is completed when the {@link io.vertx.pgclient.pubsub.PgSubscriber} has performed it's connect-operation.
     */
    public Future<Void> start();

    /**
     * @return a future that is completed when the {@link io.vertx.pgclient.pubsub.PgSubscriber} has performed it's close-operation.
     */
    public Future<Void> stop();

}
