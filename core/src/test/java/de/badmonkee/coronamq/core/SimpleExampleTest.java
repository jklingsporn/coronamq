package de.badmonkee.coronamq.core;

import de.badmonkee.coronamq.core.impl.AbstractWorker;
import de.badmonkee.coronamq.core.impl.CoronaMq;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author jensklingsporn
 */
@ExtendWith(VertxExtension.class)
public class SimpleExampleTest {

    @Test
    public void basicExample(Vertx vertx, VertxTestContext testContext){

        //the broker sends a new task onto the eventbus when it is added
        Broker broker = CoronaMq.broker(vertx);

        //CRUD for tasks, can be deployed as verticle
        TaskQueueDao taskQueueDao = CoronaMq.dao(vertx);

        //Some work to do
        SimpleWorker simpleWorker = new SimpleWorker(vertx, new CoronaMqOptions());

        //Required to add some tasks to the queue
        Publisher publisher = CoronaMq.publisher(vertx);

        testContext
                //start participants in the right order
                .assertComplete(taskQueueDao.start()
                        .compose(v->broker.start())
                        .compose(v->simpleWorker.start())
                )
                //send a new task to the queue
                .compose(v-> publisher.publishTask("test",new JsonObject().put("someValue","hi")))
                //complete the work
                .compose(v-> simpleWorker.getCurrentWork())
                .onSuccess(res -> testContext.completeNow())
                .onFailure(testContext::failNow)
        ;
    }

    @Test
    public void failureExample(Vertx vertx, VertxTestContext testContext){

        //the broker sends a new task onto the eventbus when it is added
        Broker broker = CoronaMq.broker(vertx);

        //CRUD for tasks, can be deployed as verticle
        TaskQueueDao taskQueueDao = CoronaMq.dao(vertx);

        //Some work to fail
        FailedWorker failedWorker = new FailedWorker(vertx, new CoronaMqOptions());

        //Required to add some tasks to the queue
        Publisher publisher = CoronaMq.publisher(vertx);

        testContext
                .assertComplete(CompositeFuture.all(
                        broker.start(),
                        taskQueueDao.start())
                        .compose(v->failedWorker.start())
                )
                //send a new task to the queue
                .compose(v-> publisher
                        .publishTask("test",new JsonObject().put("will it fail",true))
                        .compose(id -> failedWorker.getCurrentWork()
                                .compose(ex -> taskQueueDao.getTask(id.toString()))
                                .onSuccess(json -> testContext.verify(()->Assertions.assertEquals("Work has failed",json
                                        .getJsonObject("payload")
                                        .getJsonObject("error")
                                        .getString("cause"))))
                                .compose(json -> taskQueueDao.deleteTask(id.toString()))
                                .onSuccess(deleted -> testContext.verify(()->Assertions.assertEquals(1,deleted.intValue())))
                                .onSuccess(res -> testContext.completeNow())
                        )
                )
                .onFailure(testContext::failNow)
        ;
    }

    class SimpleWorker extends AbstractWorker {

        Promise<Void> completion = Promise.promise();

        public SimpleWorker(Vertx vertx,
                            CoronaMqOptions coronaMqOptions) {
            super(vertx, coronaMqOptions,"test");
        }

        @Override
        public Future<Void> run(JsonObject task) {
            completion.complete();
            return completion.future();
        }

        @Override
        protected Future<Void> getCurrentWork() {
            //we need to make sure that super.getCurrentWork is not just returning the default succeeded future
            return completion.future().compose(v->super.getCurrentWork());
        }
    }

    class FailedWorker extends AbstractWorker {

        Promise<Void> completion = Promise.promise();

        public FailedWorker(Vertx vertx,
                            CoronaMqOptions coronaMqOptions) {
            super(vertx, coronaMqOptions,"test");
        }

        @Override
        public Future<Void> run(JsonObject task) {
            completion.fail("Work has failed");
            return completion.future();
        }

        @Override
        protected Future<Void> getCurrentWork() {
            //we need to make sure that super.getCurrentWork is not just returning the default succeeded future
            return completion.future().recover(v->super.getCurrentWork());
        }
    }

}
