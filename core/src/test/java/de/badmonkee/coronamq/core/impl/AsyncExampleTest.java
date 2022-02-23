package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.TaskRepository;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

/**
 * @author jensklingsporn
 */
@ExtendWith(VertxExtension.class)
@Testcontainers
public class AsyncExampleTest {

    @Container
    static PostgresTestContainer database = new PostgresTestContainer();

    @Test
    public void simpleOutOfOrderStart(Vertx vertx, VertxTestContext testContext){
        SimpleWorker worker = new SimpleWorker(vertx, database.getCoronaMqOptions());
        TaskRepository repository = CoronaMq.repository(vertx, database.getCoronaMqOptions().setRepositoryGracefulShutdownMillis(500));
        testContext.assertComplete(worker.start())
                //worker is ready, but paused
                .onSuccess(v->testContext.verify(()->Assertions.assertTrue(worker.isPaused())))
                .compose(v-> repository.start())
                //worker is no longer paused, but wait a bit until the handler is notified
                .compose(v-> Internal.await(vertx,100))
                .onSuccess(v->testContext.verify(()->Assertions.assertFalse(worker.isPaused())))
                .compose(v->repository.stop())
                .compose(v->worker.stop())
                .onComplete(testContext.succeedingThenComplete());
    }

    @Test
    public void publishToPausedWorkerShouldFail(Vertx vertx, VertxTestContext testContext){
        SimpleWorker worker = new SimpleWorker(vertx, database.getCoronaMqOptions());
        testContext
                .assertComplete(worker.start())
                //pretending message received from the broker
                .onSuccess(v->Internal.sendTask(vertx,database.getCoronaMqOptions(), new JsonObject().put("label","test").put("id", UUID.randomUUID().toString())))
                //make sure the task is failed
                .compose(v-> testContext.assertFailure(worker.getCurrentWork()))
                .recover(x -> {
                    //check it's because of the paused worker
                    testContext.verify(()->Assertions.assertEquals("Worker is paused",x.getMessage()));
                    return Future.succeededFuture();
                })
                //shut down
                .compose(v->worker.stop())
                .onComplete(testContext.succeedingThenComplete())
        ;

    }


}
