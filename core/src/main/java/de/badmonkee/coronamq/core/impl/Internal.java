package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;

/**
 * @author jensklingsporn
 */
class Internal {

    private Internal(){}

    static final int CODE_ERROR_DISPATCH = 1;
    static final int CODE_ERROR_UPDATE = 2;
    static final int CODE_ERROR_REQUEST = 3;
    static final int CODE_ERROR_FAIL = 4;

    static final String DAO_SERVICE_RECORD_NAME = "corona-mq-dao";
    static final String DAO_SERVICE_RECORD_DISCOVERY = "corona.mq.dao.discovery";

    static String toWorkerAddress(CoronaMqOptions options, String label){
        return options.getWorkerAddress()+label;
    }

    static Future<Void> await(Vertx vertx, long timeoutMillis){
        Promise<Void> elapsedPromise = Promise.promise();
        Handler<Long> elapsedHandler = l -> elapsedPromise.complete();
        TimeoutStream timeoutStream = vertx.timerStream(timeoutMillis);
        timeoutStream.handler(elapsedHandler);
        timeoutStream.exceptionHandler(elapsedPromise::fail);
        return elapsedPromise.future();
    }

    static void sendTask(Vertx vertx, CoronaMqOptions coronaMqOptions, JsonObject task) {
        vertx.eventBus().send(Internal.toWorkerAddress(coronaMqOptions, task.getString("label")), task);
    }

}
