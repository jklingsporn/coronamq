package de.badmonkee.coronamq.metrics;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import de.badmonkee.coronamq.core.TaskStatus;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.micrometer.PrometheusScrapingHandler;
import io.vertx.micrometer.backends.BackendRegistries;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class MicrometerMetricsVerticle extends AbstractVerticle {


    private final CoronaMqOptions options;
    private final MeterRegistry meterRegistry;
    private MessageConsumer<JsonObject> consumer;
    private Future<HttpServer> httpServerFuture;

    public MicrometerMetricsVerticle(CoronaMqOptions options) {
        this.options = options;
        this.meterRegistry = BackendRegistries.getDefaultNow();
        Objects.requireNonNull(meterRegistry,"Meter Registry not started");
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        if(!vertx.isMetricsEnabled()){
            startPromise.fail("Metrics not enabled");
            return;
        }
        Router router = Router.router(vertx);
        router.route("/metrics").handler(PrometheusScrapingHandler.create());
        httpServerFuture = vertx.createHttpServer()
                .requestHandler(router)
                .listen(8080);
        //only if the server can be started
        httpServerFuture.onComplete(v->{
            consumer = vertx.eventBus().consumer(options.getMetricsAddress(), msg -> {
                String type = msg.body().getString("type");
                String name = msg.body().getString("name");
                JsonObject data = msg.body().getJsonObject("data");
                switch(type){
                    case "gauge":
                        if(name.equals("task_count")){
                            data.forEach(e->{
                                String label = e.getKey();
                                Tag labelTag = Tag.of("label",label);
                                @SuppressWarnings("unchecked")
                                JsonObject values = data.getJsonObject(label);
                                Stream.of(TaskStatus.values())
                                        .forEach(status -> meterRegistry.gauge(name, Arrays.asList(labelTag,Tag.of("status",status.name())),values.getLong(status.name(),0L)));
                            });
                            break;
                        }
                    default:
                        throw new UnsupportedOperationException(type);
                }
            });
            consumer.completionHandler(startPromise);
        });
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        if(vertx.isMetricsEnabled() && consumer!=null && httpServerFuture!=null){
            httpServerFuture
                    .compose(HttpServer::close)
                    .onComplete(v->consumer.unregister(stopPromise))
                    .onComplete(v->meterRegistry.close());
        }
    }
}
