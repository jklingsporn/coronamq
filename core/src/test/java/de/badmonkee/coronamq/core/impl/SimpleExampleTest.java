package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.Broker;
import de.badmonkee.coronamq.core.Dispatcher;
import de.badmonkee.coronamq.core.TaskRepository;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author jensklingsporn
 */
@ExtendWith(VertxExtension.class)
@Testcontainers
public class SimpleExampleTest {

    @Container
    static PostgresTestContainer database = new PostgresTestContainer();

    @Test
    public void basicExample(Vertx vertx, VertxTestContext testContext){
        //the broker sends a new task onto the eventbus when it is added
        Broker broker = CoronaMq.broker(vertx,database.getCoronaMqOptions());

        //CRUD for tasks, can be deployed as verticle
        TaskRepository taskRepository = CoronaMq.repository(vertx,database.getCoronaMqOptions());

        //Some work to do
        SimpleWorker simpleWorker = new SimpleWorker(vertx, database.getCoronaMqOptions());

        //Required to add some tasks to the queue
        Dispatcher dispatcher = CoronaMq.dispatcher(vertx);

        testContext
                //start participants in the right order
                .assertComplete(taskRepository.start()
                        .compose(v->broker.start())
                        .compose(v->simpleWorker.start())
                )
                //send a new task to the queue
                .compose(v-> dispatcher.dispatch("test",new JsonObject().put("someValue","hi")))
                //complete the work
                .compose(v-> simpleWorker.getCurrentWork())
                .onSuccess(res -> testContext.completeNow())
                .onFailure(testContext::failNow)
        ;
    }

    @Test
    public void failureExample(Vertx vertx, VertxTestContext testContext){

        //the broker sends a new task onto the eventbus when it is added
        Broker broker = CoronaMq.broker(vertx, database.getCoronaMqOptions());

        //CRUD for tasks, can be deployed as verticle
        TaskRepository taskRepository = CoronaMq.repository(vertx, database.getCoronaMqOptions());

        //Some work to fail
        FailingWorker failedWorker = new FailingWorker(vertx, database.getCoronaMqOptions());

        //Required to add some tasks to the queue
        Dispatcher dispatcher = CoronaMq.dispatcher(vertx);

        testContext
                .assertComplete(CompositeFuture.all(
                        broker.start(),
                        taskRepository.start())
                        .compose(v->failedWorker.start())
                )
                //send a new task to the queue
                .compose(v-> dispatcher
                        .dispatch("test",new JsonObject().put("will it fail",true))
                        .compose(id -> failedWorker.getCurrentWork()
                                .compose(ex -> taskRepository.getTask(id.toString()))
                                .onSuccess(json -> testContext.verify(()->Assertions.assertEquals("Work has failed",json
                                        .getJsonObject("payload")
                                        .getJsonObject("error")
                                        .getString("cause"))))
                                .compose(json -> taskRepository.deleteTask(id.toString()))
                                .onSuccess(deleted -> testContext.verify(()->Assertions.assertEquals(1,deleted.intValue())))
                                .onSuccess(res -> testContext.completeNow())
                        )
                )
                .onFailure(testContext::failNow);
    }

}
