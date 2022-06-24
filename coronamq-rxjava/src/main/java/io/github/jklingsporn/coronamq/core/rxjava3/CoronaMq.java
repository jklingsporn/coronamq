package io.github.jklingsporn.coronamq.core.rxjava3;

import io.github.jklingsporn.coronamq.core.CoronaMqOptions;
import io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.Bootstrap;
import io.vertx.core.Vertx;

public class CoronaMq {

    private CoronaMq(){}

    private static final CoronaMqOptions DEFAULT_OPTIONS = new CoronaMqOptions();

    /**
     * The recommended way to create a CoronaMq distribution using a fluent DSL.
     * @param vertx the vertx instance
     * @return a {@code Bootstrap} with default options.
     */
    public static Bootstrap create(Vertx vertx){
        return create(vertx, DEFAULT_OPTIONS);
    }

    /**
     * The recommended way to create a CoronaMq distribution using a fluent DSL.
     * @param vertx the vertx instance
     * @param coronaMqOptions the options.
     * @return a {@code Bootstrap} using the provided options.
     */
    public static Bootstrap create(Vertx vertx, CoronaMqOptions coronaMqOptions){
        return new Bootstrap(io.github.jklingsporn.coronamq.core.impl.CoronaMq.create(vertx, coronaMqOptions));
    }
}
