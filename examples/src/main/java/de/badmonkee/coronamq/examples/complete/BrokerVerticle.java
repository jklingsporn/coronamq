package de.badmonkee.coronamq.examples.complete;

import de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep;
import de.badmonkee.coronamq.core.impl.CoronaMq;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.TimeoutStream;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author jensklingsporn
 */
public class BrokerVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(BrokerVerticle.class);

    private Future<BootstrapSpreadStep> bootstrap;
    private TimeoutStream timeoutStream;
    private LocalDateTime start;


    @Override
    public void start(Promise<Void> startFuture) throws Exception {
        start = LocalDateTime.now();
        //the broker sends a new task onto the eventbus when it is added
        bootstrap = CoronaMq
                .create(vertx)
                .withBroker()
                .withRepository()
                .spread();
        bootstrap
                .<Void>mapEmpty()
                .onComplete(startFuture);

        timeoutStream = vertx.periodicStream(3000)
                .handler(l -> countTasks());
    }

    private Future<JsonObject> countTasks() {
        return bootstrap
                .compose(b -> b.getRepository().countTasks("delayed"))
                .onComplete(tasksCount -> {
                    logger.info("{} ",tasksCount.result());
                    if(tasksCount.result().isEmpty()){
                        logger.info("Shutting down. Completion took {}", Duration.between(start,LocalDateTime.now()));
                        vertx.close();
                    }
                });
    }

    @Override
    public void stop(Promise<Void> stopFuture) throws Exception {
        timeoutStream.cancel();
        bootstrap
                .compose(BootstrapSpreadStep::vaccinate)
                .onComplete(stopFuture);
    }
}
