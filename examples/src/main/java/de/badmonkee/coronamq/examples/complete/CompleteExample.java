package de.badmonkee.coronamq.examples.complete;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import de.badmonkee.coronamq.core.Dispatcher;
import de.badmonkee.coronamq.core.impl.CoronaMq;
import de.badmonkee.coronamq.metrics.MicrometerMetricsVerticle;
import io.netty.util.internal.logging.InternalLoggerFactory;
import io.netty.util.internal.logging.JdkLoggerFactory;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.SLF4JLogDelegateFactory;
import io.vertx.micrometer.MicrometerMetricsOptions;
import io.vertx.micrometer.VertxPrometheusOptions;
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
 * Docker example:
 * <code>
 * 1. docker build -t corona-mq .
 * 2. docker run -p 5432:5432 -e POSTGRES_PASSWORD=vertx -e POSTGRES_USER=coronamq corona-mq
 * </code>
 * <ul>
 *     <li>The broker and repository are deployed via the {@link BrokerVerticle}</li>
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

        /*
         *
         * In order to watch the metrics, you need to
         * 1. Run prometheus server:
         *      docker run -p 9090:9090 -v ${PWD}/examples/src/main/resources/:/etc/prometheus prom/prometheus
         *      (WATCH OUT: path to prometheus config only works when running from parent)
         *      -> 127.0.0.1:9090
         * 2. Run grafana server:
         *      docker run -d -p 3000:3000 -it grafana/grafana-oss
         *      -> goto 127.0.0.1:3000
         *      -> login / set new password
         *      -> create a prometheus datasource: http://host.docker.internal:9090
         *      -> import the dashboard from /examples/src/main/resources/dashboard.json
         *
         * See also: https://github.com/vertx-howtos/metrics-prometheus-grafana-howto
         *
         */
        MicrometerMetricsOptions metricsOptions = new MicrometerMetricsOptions()
                .setEnabled(true)
                .setPrometheusOptions(new VertxPrometheusOptions().setEnabled(true));
        VertxOptions vertxOptions = new VertxOptions()
                .setMetricsOptions(metricsOptions);
        Vertx vertx = Vertx.vertx(vertxOptions);

        Promise<String> brokerDeployment = Promise.promise();
        Promise<String> workerDeployment = Promise.promise();
        Promise<String> metricsDeployment = Promise.promise();
        boolean randomDelay = args.length>0 && Boolean.parseBoolean(args[1]);
        Supplier<Long> delaySupplier = randomDelay
                ? () -> ThreadLocalRandom.current().nextLong(10,100)
                : () -> 50L;
        try{
            vertx.deployVerticle(BrokerVerticle.class, new DeploymentOptions(), brokerDeployment);
            vertx.deployVerticle(
                    WorkerVerticle.class,
                    //tweak the instance count and see how completion time varies
                    new DeploymentOptions().setInstances(10),
                    workerDeployment);
            vertx.deployVerticle(new MicrometerMetricsVerticle(new CoronaMqOptions()), new DeploymentOptions(), metricsDeployment);
            CompositeFuture
                    .all(brokerDeployment.future(),workerDeployment.future(),metricsDeployment.future())
                    //everything is in place, now create some tasks
                    .compose(deploymentId -> dispatchTasks(vertx,delaySupplier))
                    .onFailure(ex -> logger.error(ex.getMessage(),ex))
            ;

        }catch (Throwable e){
            logger.error(e.getMessage(),e);
        }

    }

    private static Future<Void> dispatchTasks(Vertx vertx, Supplier<Long> delaySupplier) {
        Dispatcher dispatcher = CoronaMq.dispatcher(vertx);
        return CompositeFuture.all(LongStream
                .range(0,40000)
                .mapToObj(d -> dispatcher.dispatch("delayed",new JsonObject().put("delay", delaySupplier.get())))
                .collect(Collectors.toList())
        ).mapEmpty();

    }

}
