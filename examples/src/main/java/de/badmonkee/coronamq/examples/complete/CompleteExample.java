package de.badmonkee.coronamq.examples.complete;

import de.badmonkee.coronamq.core.Publisher;
import de.badmonkee.coronamq.core.impl.CoronaMq;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

/**
 * A complete example of CoronaMQ. It spawns 1000 tasks and measures how long this setup takes to handle all the tasks.
 * It assumes that there is a postgres database set up with a tasks-table and the triggers. Either run the provided
 * <code>Dockerfile</code> from the parent or add the SQL from <code>core/src/main/resources/00_setup.sql</code> to your existing DB.<br>
 * <ul>
 *     <li>The broker and dao are deployed via the {@link BrokerVerticle}</li>
 *     <li>The worker just waits for some time (which is defined by the task's payload) until it completes the task.
 *     The worker is bound to the {@link WorkerVerticle}</li>
 *     <li>You can play around with the instance-count of the {@link WorkerVerticle} to see how it changes the execution time.</li>
 * </ul>
 * @author jensklingsporn
 */
public class CompleteExample {

    private static final Logger logger = LoggerFactory.getLogger(CompleteExample.class);

    public static void main(String[] args) {
        InternalLoggerFactory.setDefaultFactory(JdkLoggerFactory.INSTANCE);
        System.setProperty("vertx.logger-delegate-factory-class-name", SLF4JLogDelegateFactory.class.getName());
        Vertx vertx = Vertx.vertx();
        Promise<String> brokerDeployment = Promise.promise();
        Promise<String> workerDeployment = Promise.promise();
        boolean randomDelay = args.length>0 && Boolean.parseBoolean(args[1]);
        Supplier<Long> delaySupplier = randomDelay
                ? () -> ThreadLocalRandom.current().nextLong(10,100)
                : () -> 50L;
        try{
            //deploy broker and dao first
            vertx.deployVerticle(BrokerVerticle.class, new DeploymentOptions(), brokerDeployment);
            brokerDeployment
                    .future()
                    .compose(deploymentId -> {
                        vertx.deployVerticle(
                                WorkerVerticle.class,
                                //tweak the instance count and see how completion time varies
                                new DeploymentOptions().setInstances(10),
                                workerDeployment);
                        return workerDeployment.future();
                    })
                    //everything is in place, now create some tasks
                    .compose(deploymentId -> publishTasks(vertx,delaySupplier))
                    .onFailure(ex -> logger.error(ex.getMessage(),ex))
            ;

        }catch (Throwable e){
            logger.error(e.getMessage(),e);
        }

    }

    private static Future<Void> publishTasks(Vertx vertx, Supplier<Long> delaySupplier) {
        Publisher publisher = CoronaMq.publisher(vertx);
        return CompositeFuture.all(LongStream
                .range(0,1000)
                .mapToObj(d -> publisher.publishTask("delayed",new JsonObject().put("delay", delaySupplier.get())))
                .collect(Collectors.toList())
        ).mapEmpty();

    }

}
