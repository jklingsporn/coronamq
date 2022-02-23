package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.*;
import de.badmonkee.coronamq.core.bootstrap.Bootstrap;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;
import io.vertx.sqlclient.PoolOptions;

import java.util.function.Function;

/**
 * Static factory to create the required participants in order to make CoronaMq work.<br>
 * <ul>
 *     <li><b>{@link Broker}</b>: The broker listens to additions made to the task queue and sends these tasks over the EventBus.</li>
 *     <li><b>{@link Worker}</b>: The worker acts on new tasks added to the queue. Workers are bound to a label which describes the unit of work.
 *     There can be multiple labels, e.g. PLACE_ORDER and CHECKOUT</li>
 *     <li><b>{@link TaskQueueDao}</b>: The TaskQueueDao is interacting with the queue in the database.</li>
 * </ul>
 * There is also the <b>{@link Dispatcher}</b>: A dispatcher can add tasks to the queue by sending a message on the EventBus. The dispatcher is not
 * required as you can also dispatch tasks by using the static {@link CoronaMq#dispatch(Vertx, String, String, JsonObject)}</li> method.<br>
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

    private static final CoronaMqOptions DEFAULT_OPTIONS = new CoronaMqOptions();

    /**
     * The recommended way to create a CoronaMq distribution using a fluent DSL.
     * @param vertx the vertx instance
     * @return a {@code Bootstrap} with default options.
     */
    public static Bootstrap create(Vertx vertx){
        return create(vertx, DEFAULT_OPTIONS);
    }

    /**
     * The recommended way to create a CoronaMq distribution using a fluent DSL.
     * @param vertx the vertx instance
     * @param coronaMqOptions the options.
     * @return a {@code Bootstrap} using the provided options.
     */
    public static Bootstrap create(Vertx vertx, CoronaMqOptions coronaMqOptions){
        return new BootstrapImpl(vertx, coronaMqOptions);
    }

    /**
     * @param vertx the vertx instance
     * @return a new {@link Broker} with default options. There should be only one broker per application.
     * @see #broker(Vertx, CoronaMqOptions)
     */
    public static Broker broker(Vertx vertx){
        return broker(vertx, DEFAULT_OPTIONS);
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
     * @return a new {@link Dispatcher} with default options. A dispatcher can dispatch tasks to the queue. You can also statically dispatch
     * tasks using the {@link #dispatch(Vertx, String, String, JsonObject)} - method.
     * @see #dispatcher(Vertx, CoronaMqOptions)
     * @see #dispatch(Vertx, String, String, JsonObject)
     */
    public static Dispatcher dispatcher(Vertx vertx){
        return dispatcher(vertx, DEFAULT_OPTIONS);
    }

    /**
     * @param vertx the vertx instance
     * @param coronamqOptions the options
     * @return a new {@link Dispatcher}. A dispatcher can dispatch tasks to the queue. You can also statically dispatch
     * tasks using the {@link #dispatch(Vertx, String, String, JsonObject)} - method.
     * @see #dispatcher(Vertx)
     * @see #dispatch(Vertx, String, String, JsonObject)
     */
    public static Dispatcher dispatcher(Vertx vertx, CoronaMqOptions coronamqOptions){
        return new DispatcherImpl(vertx, coronamqOptions);
    }

    /**
     * Statically dispatches a task to the queue.
     * @param vertx the vertx instance.
     * @param label the label for the task.
     * @param payload the payload for the task
     * @return a {@link Future} that is completed when the task is added to the queue containing the id associated
     * with the task.
     * @see #dispatcher(Vertx)
     * @see #dispatcher(Vertx, CoronaMqOptions)
     */
    public static Future<String> dispatch(Vertx vertx, String label, JsonObject payload){
        return dispatch(vertx, DEFAULT_OPTIONS.getDaoAddress(),label,payload);
    }

    /**
     * Statically dispatches a task to the queue.
     * @param vertx the vertx instance.
     * @param dispatchAddress the EventBus address to send the task to. Must match <code>CoronaMqOptions#getDaoAddress</code>
     * @param label the label for the task.
     * @param payload the payload for the task
     * @return a {@link Future} that is completed when the task is added to the queue containing the id associated
     * with the task.
     * @see #dispatcher(Vertx)
     * @see #dispatcher(Vertx, CoronaMqOptions)
     */
    public static Future<String> dispatch(Vertx vertx, String dispatchAddress, String label, JsonObject payload){
        Promise<Message<String>> promise = Promise.promise();
        vertx.eventBus().request(dispatchAddress, new JsonObject().put("label",label).put("payload",payload),new DeliveryOptions().addHeader("action","createTask"), promise);
        return promise.future().map(msg -> msg.body());
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
        return worker(vertx, DEFAULT_OPTIONS, label, work);
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
        return dao(vertx, DEFAULT_OPTIONS, PgPool.pool(vertx, DEFAULT_OPTIONS.getConnectOptions(), new PoolOptions()));
    }

    /**
     * @param vertx the vertx instance
     * @param pgPool the pool to use
     * @return a new {@link TaskQueueDao} with default options. The TaskQueueDao performs the
     * necessary actions to operate with the task queue.
     */
    public static TaskQueueDao dao(Vertx vertx, PgPool pgPool){
        return dao(vertx, DEFAULT_OPTIONS, pgPool);
    }

    /**
     * @param vertx the vertx instance
     * @param coronaMqOptions the options
     * @return a new {@link TaskQueueDao}. The TaskQueueDao performs the
     * necessary actions to operate with the task queue.
     */
    public static TaskQueueDao dao(Vertx vertx, CoronaMqOptions coronaMqOptions){
        return new TaskQueueDaoImpl(vertx, coronaMqOptions, PgPool.pool(vertx, coronaMqOptions.getConnectOptions(), new PoolOptions()));
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


    static ServiceDiscovery serviceDiscoveryInstance = null;
    static synchronized ServiceDiscovery serviceDiscovery(Vertx vertx){
        if(serviceDiscoveryInstance == null){
            serviceDiscoveryInstance = ServiceDiscovery.create(vertx, new ServiceDiscoveryOptions()
                    .setName("corona.mq")
                    .setAnnounceAddress("coronamq.discovery.announce")
                    .setUsageAddress("coronamq.discovery.usage")
            );
        }
        return serviceDiscoveryInstance;
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
