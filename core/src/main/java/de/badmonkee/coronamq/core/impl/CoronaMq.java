package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.*;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.PoolOptions;

import java.util.UUID;
import java.util.function.Function;

/**
 * Static factory to create the required participants in order to make CoronaMq work.<br>
 * <ul>
 *     <li><b>{@link Broker}</b>: The broker listens to additions made to the task queue and sends these tasks over the EventBus.</li>
 *     <li><b>{@link Worker}</b>: The worker acts on new tasks added to the queue. Workers are bound to a label which describes the unit of work.
 *     There can be multiple labels, e.g. PLACE_ORDER and CHECKOUT</li>
 *     <li><b>{@link TaskQueueDao}</b>: The TaskQueueDao is interacting with the queue in the database.</li>
 * </ul>
 * There is also the <b>{@link Publisher}</b>: A publisher can add tasks to the queue by sending a message on the EventBus. The publisher is not
 * a required as you can also publish tasks by using the static {@link CoronaMq#publishTask(Vertx, String, String, JsonObject)}</li> method.<br>
 * Note that the required participants have <code>start</code>- and <code>stop</code>-methods which have to be invoked after they've been created. It is
 * important to start and stop them in the right order or otherwise data might get lost.<br>
 * Start order:
 * <ul>
 *     <li>1. {@link TaskQueueDao}</li>
 *     <li>2. {@link Worker}</li>
 *     <li>3. {@link Broker}</li>
 * </ul>
 * Stop order:
 * <ul>
 *     <li>1. {@link Broker}</li>
 *     <li>2. {@link Worker}</li>
 *     <li>3. {@link TaskQueueDao}</li>
 * </ul>
 */
public class CoronaMq {

    private CoronaMq(){}

    /**
     * @param vertx the vertx instance
     * @return a new {@link Broker} with default options. There should be only one broker per application.
     * @see #broker(Vertx, CoronaMqOptions)
     */
    public static Broker broker(Vertx vertx){
        return broker(vertx, new CoronaMqOptions());
    }

    /**
     * @param vertx the vertx instance
     * @param coronamqOptions the options
     * @return a new {@link Broker}. There should be only one broker per application.
     * @see #broker(Vertx)
     */
    public static Broker broker(Vertx vertx, CoronaMqOptions coronamqOptions){
        return new BrokerImpl(vertx, coronamqOptions);
    }

    /**
     * @param vertx the vertx instance
     * @return a new {@link Publisher} with default options. A publisher can publish tasks to the queue. You can also statically publish
     * tasks using the {@link #publishTask(Vertx, String, String, JsonObject)} - method.
     * @see #publisher(Vertx, CoronaMqOptions)
     * @see #publishTask(Vertx, String, String, JsonObject)
     */
    public static Publisher publisher(Vertx vertx){
        return publisher(vertx, new CoronaMqOptions());
    }

    /**
     * @param vertx the vertx instance
     * @param coronamqOptions the options
     * @return a new {@link Publisher}. A publisher can publish tasks to the queue. You can also statically publish
     * tasks using the {@link #publishTask(Vertx, String, String, JsonObject)} - method.
     * @see #publisher(Vertx)
     * @see #publishTask(Vertx, String, String, JsonObject)
     */
    public static Publisher publisher(Vertx vertx, CoronaMqOptions coronamqOptions){
        return new PublisherImpl(vertx, coronamqOptions);
    }

    /**
     * Statically publishes a task to the queue.
     * @param vertx the vertx instance.
     * @param publishAddress the EventBus address to send the task to.
     * @param label the label for the task.
     * @param payload the payload for the task
     * @return a {@link Future} that is completed when the task is added to the queue containing the id associated
     * with the task.
     * @see #publisher(Vertx)
     * @see #publisher(Vertx, CoronaMqOptions)
     */
    public static Future<UUID> publishTask(Vertx vertx, String publishAddress, String label, JsonObject payload){
        Promise<Message<JsonObject>> promise = Promise.promise();
        vertx.eventBus().request(publishAddress, new JsonObject().put("label",label).put("payload",payload),promise);
        return promise.future().map(msg -> UUID.fromString(msg.body().getString("id")));
    }

    /**
     * @param vertx the vertx instance
     * @param label the label associated with the work to do
     * @param work the work that has to be done
     * @return a new {@link Worker} instance with default options. A worker to work on tasks from the task queue.
     */
    public static Worker worker(Vertx vertx,
                                String label,
                                Function<JsonObject, Future<Void>> work){
        return worker(vertx, new CoronaMqOptions(), label, work);
    }

    /**
     * @param vertx the vertx instance
     * @param coronaMqOptions the options
     * @param label the label associated with the work to do
     * @param work the work that has to be done
     * @return a new {@link Worker} instance. A worker to work on tasks from the task queue.
     */
    public static Worker worker(Vertx vertx,
                                CoronaMqOptions coronaMqOptions,
                                String label,
                                Function<JsonObject, Future<Void>> work){
        return new DelegatingWorker(vertx, coronaMqOptions, label, work);
    }

    /**
     * @param vertx the vertx instance.
     * @return a new {@link TaskQueueDao} with default options, creating a new {@link PgPool}. The TaskQueueDao performs the
     * necessary actions to operate with the task queue.
     */
    public static TaskQueueDao dao(Vertx vertx){
        CoronaMqOptions coronaMqOptions = new CoronaMqOptions();
        return dao(vertx, coronaMqOptions, PgPool.pool(vertx, coronaMqOptions.getConnectOptions(), new PoolOptions()));
    }

    /**
     * @param vertx the vertx instance
     * @param pgPool the pool to use
     * @return a new {@link TaskQueueDao} with default options. The TaskQueueDao performs the
     * necessary actions to operate with the task queue.
     */
    public static TaskQueueDao dao(Vertx vertx, PgPool pgPool){
        return dao(vertx, new CoronaMqOptions(), pgPool);
    }

    /**
     * @param vertx the vertx instance
     * @param pgPool the pool to use
     * @param coronamqOptions the options
     * @return a new {@link TaskQueueDao}. The TaskQueueDao performs the
     * necessary actions to operate with the task queue.
     */
    public static TaskQueueDao dao(Vertx vertx, CoronaMqOptions coronamqOptions, PgPool pgPool){
        return new TaskQueueDaoImpl(vertx, coronamqOptions, pgPool);
    }


    static class DelegatingWorker extends AbstractWorker{

        private final Function<JsonObject,Future<Void>> delegate;

        public DelegatingWorker(Vertx vertx,
                                CoronaMqOptions coronaMqOptions,
                                String label,
                                Function<JsonObject, Future<Void>> delegate) {
            super(vertx, coronaMqOptions, label);
            this.delegate = delegate;
        }

        @Override
        public Future<Void> run(JsonObject task) {
            return delegate.apply(task);
        }
    }

}
