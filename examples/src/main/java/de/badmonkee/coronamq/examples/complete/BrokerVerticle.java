package de.badmonkee.coronamq.examples.complete;

import de.badmonkee.coronamq.core.Broker;
import de.badmonkee.coronamq.core.TaskQueueDao;
import de.badmonkee.coronamq.core.impl.CoronaMq;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.TimeoutStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author jensklingsporn
 */
public class BrokerVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory.getLogger(BrokerVerticle.class);

    private Broker broker;
    private TaskQueueDao taskQueueDao;
    private TimeoutStream timeoutStream;
    private LocalDateTime start;


    @Override
    public void start(Promise<Void> startFuture) throws Exception {
        start = LocalDateTime.now();
        //the broker sends a new task onto the eventbus when it is added
        broker = CoronaMq.broker(vertx);
        taskQueueDao = CoronaMq.dao(vertx);

        taskQueueDao.start()
            .compose(v->broker.start())
            .onComplete(startFuture);

        timeoutStream = vertx.periodicStream(3000)
                .handler(l -> countTasks());
    }

    private Future<Long> countTasks() {
        return taskQueueDao.countTasks("delayed")
                .onComplete(tasksCount -> {
                    logger.info("{} tasks remaining",tasksCount.result());
                    if(tasksCount.result().equals(0L)){
                        logger.info("Shutting down. Completion took {}", Duration.between(start,LocalDateTime.now()));
                        vertx.close();
                    }
                });
    }

    @Override
    public void stop(Promise<Void> stopFuture) throws Exception {
        timeoutStream.cancel();
        broker.stop()
                .compose(v->taskQueueDao.stop())
                .onComplete(stopFuture);
    }
}
