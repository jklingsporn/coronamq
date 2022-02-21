package de.badmonkee.coronamq.core;

import de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep;
import de.badmonkee.coronamq.core.impl.CoronaMq;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

@ExtendWith(VertxExtension.class)
@Testcontainers
public class BootstrapExampleTest{

    @Container
    static PostgresTestContainer database = new PostgresTestContainer();

    @Test
    public void boostrapExample(Vertx vertx, VertxTestContext testContext){
        CoronaMqOptions coronaMqOptions = database.getCoronaMqOptions();
        SimpleWorker worker = new SimpleWorker(vertx, coronaMqOptions);
        Future<BootstrapSpreadStep> spread = CoronaMq.create(vertx,coronaMqOptions)
                .withBroker()
                .withDao()
                .withWorker(worker)
                .spread();
        testContext
                .assertComplete(spread)
                //send a new task to the queue
                .compose(s-> s.dispatch("test",new JsonObject().put("someValue","hi")))
                .onComplete(testContext.succeeding(UUID::fromString))
                //complete the work
                .compose(v-> worker.getCurrentWork())
                //shut down all components
                .compose(v->spread.compose(BootstrapSpreadStep::vaccinate))
                .onSuccess(res -> testContext.completeNow())
                .onFailure(testContext::failNow)
        ;
    }
}
