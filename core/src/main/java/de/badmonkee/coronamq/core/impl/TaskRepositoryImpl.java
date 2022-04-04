package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import de.badmonkee.coronamq.core.TaskRepository;
import de.badmonkee.coronamq.core.TaskStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.TimeoutStream;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.impl.Arguments;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.servicediscovery.Record;
import io.vertx.servicediscovery.ServiceDiscovery;
import io.vertx.servicediscovery.Status;
import io.vertx.servicediscovery.types.EventBusService;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author jensklingsporn
 */
class TaskRepositoryImpl implements TaskRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskRepositoryImpl.class);

    private final Vertx vertx;
    private final CoronaMqOptions coronaMqOptions;
    private final PgPool pool;
    private final ServiceBinder binder;
    private final ServiceDiscovery serviceDiscovery;
    private final Promise<Record> registrationResult;
    private MessageConsumer<JsonObject> messageConsumer;
    private TimeoutStream metricsUpdater;

    public TaskRepositoryImpl(Vertx vertx, CoronaMqOptions coronaMqOptions, PgPool pool){
        this.vertx = vertx;
        this.coronaMqOptions = coronaMqOptions;
        this.pool = pool;
        this.binder = new ServiceBinder(vertx);
        this.serviceDiscovery = ServiceDiscovery.create(vertx, coronaMqOptions.getServiceDiscoveryOptions());
        this.registrationResult = Promise.promise();
    }

    @Override
    public Future<String> createTask(String label, JsonObject payload) {
        Promise<RowSet<Row>> completion = Promise.promise();

        UUID newId = UUID.randomUUID();
        pool.preparedQuery("INSERT INTO tasks (id,label, payload, status, update_time) VALUES ($1, $2, $3, $4, $5)").execute(Tuple
                .of(
                        newId,
                        label,
                        payload,
                        TaskStatus.NEW.toString(),
                        LocalDateTime.now(Clock.systemUTC())
                ), completion);
        return completion.future().map(newId.toString()).recover(x -> failWithCode(Internal.CODE_ERROR_DISPATCH,x));
    }

    @Override
    public Future<Void> failTask(String id, String reason) {
        Promise<RowSet<Row>> completion = Promise.promise();
        pool.preparedQuery("UPDATE tasks SET status = 'FAILED', payload = payload || $1 WHERE id = $2 AND status = 'RUNNING'").execute(Tuple
                .of(
                        new JsonObject().put("error",new JsonObject().put("cause",reason)),
                        UUID.fromString(id)
                ), completion);
        return completion
                .future()
                .onSuccess(rowSet -> Arguments.require(rowSet.rowCount() == 1,"Not updated"))
                .<Void>mapEmpty()
                .recover(x -> failWithCode(Internal.CODE_ERROR_FAIL,x));
    }

    @Override
    public Future<Void> updateTask(String id, TaskStatus newStatus, TaskStatus oldStatus) {
        Promise<RowSet<Row>> completion = Promise.promise();
        if(newStatus.equals(TaskStatus.COMPLETED)){
            pool.preparedQuery("DELETE FROM tasks WHERE id = $1 AND status = $2").execute(Tuple.of(UUID.fromString(id),oldStatus.toString()),completion);
        }else{
            pool.preparedQuery("UPDATE tasks SET status = $1 WHERE id = $2 AND status = $3").execute(Tuple
                    .of(
                            newStatus.toString(),
                            UUID.fromString(id),
                            oldStatus.toString()
                    ), completion);
        }
        return completion
                .future()
                .onSuccess(rowSet -> Arguments.require(rowSet.rowCount() == 1,"Not updated"))
                .<Void>mapEmpty()
                .recover(x -> failWithCode(Internal.CODE_ERROR_UPDATE,x));
    }

    @Override
    public Future<JsonObject> requestTask(String label) {
        Promise<RowSet<Row>> completion = Promise.promise();
        pool.preparedQuery("UPDATE tasks SET status='RUNNING'\n" +
                "WHERE id = (\n" +
                "  SELECT id\n" +
                "  FROM tasks\n" +
                "  WHERE status='NEW' AND label=$1\n" +
                "  ORDER BY id\n" +
                "  FOR UPDATE SKIP LOCKED\n" +
                "  LIMIT 1\n" +
                ")\n" +
                "RETURNING id, label, payload, status;").execute(Tuple
                .of(
                        label
                ), completion);
        return completion.future().map(rowSet -> {
            if(!rowSet.iterator().hasNext()){
                return null;
            }
            Row newJob = rowSet.iterator().next();
            return new JsonObject()
                    .put("id",newJob.getUUID(0).toString())
                    .put("label", newJob.getString(1))
                    .put("payload", newJob.get(JsonObject.class,2))
                    .put("status",newJob.getString(3))
                    ;
        }).recover(x -> failWithCode(Internal.CODE_ERROR_REQUEST,x));
    }

    @Override
    public Future<JsonObject> getTask(String id) {
        Promise<RowSet<Row>> completion = Promise.promise();
        pool.preparedQuery(
                " SELECT id, label, payload, status FROM tasks WHERE id=$1").execute(
                Tuple.of(UUID.fromString(id)), completion);
        return completion.future()
                .compose(rowSet -> {
                    if(!rowSet.iterator().hasNext()){
                        Future.failedFuture("Task does not exist "+id);
                    }
                    return Future.succeededFuture(rowSet.iterator().next());
                })
                .map(newJob -> new JsonObject()
                        .put("id",newJob.getUUID(0).toString())
                        .put("label", newJob.getString(1))
                        .put("payload", newJob.get(JsonObject.class,2))
                        .put("status",newJob.getString(3)));
    }

    @Override
    public Future<JsonObject> countTasks(String label) {
        Promise<SqlResult<JsonObject>> completion = Promise.promise();
        Collector<Row, ?, JsonObject> collector = Collectors.collectingAndThen(
                Collectors.toList(),
                rows-> {
                    JsonObject res = new JsonObject();
                    rows.stream()
                            .map(Row::toJson)
                            .forEach(row ->
                                    {
                                        JsonObject labelJson = res.getJsonObject(row.getString("label"), new JsonObject());
                                        res.put(row.getString("label"),labelJson);
                                        labelJson.put(row.getString("status"),row.getLong("cnt"));
                                    }
                            );
                    return res;
                });
        String sql = label==null
                ? "SELECT label,status,COUNT(*) as cnt FROM tasks GROUP BY label,status"
                : "SELECT label,status,COUNT(*) as cnt FROM tasks WHERE label = $1 GROUP BY label,status";
        pool.preparedQuery(sql)
                .collecting(collector)
                .execute(label==null?Tuple.tuple():Tuple.of(label),completion);
        return completion.future().map(SqlResult::value);
    }

    @Override
    public Future<Integer> deleteTask(String id) {
        Promise<RowSet<Row>> completion = Promise.promise();
        pool.preparedQuery("DELETE FROM tasks WHERE id = $1").execute( Tuple.of(UUID.fromString(id)),completion);
        return completion.future().map(SqlResult::rowCount);
    }

    private <T> Future<T> failWithCode(int code,Throwable x) {
        return Future.failedFuture(new ServiceException(code, x.getMessage()));
    }

    @Override
    public Future<Void> start() {
        this.messageConsumer = binder.setAddress(coronaMqOptions.getRepositoryAddress()).register(TaskRepository.class, this);
        Promise<Void> registrationPromise = Promise.promise();
        this.messageConsumer.completionHandler(registrationPromise);
        return registrationPromise.future()
                .compose(v->registerToServiceDiscovery())
                .<Void>mapEmpty()
                .onSuccess(v-> startMetrics());
    }

    private Future<Record> registerToServiceDiscovery(){
        Record record = EventBusService.createRecord(Internal.REPOSITORY_SERVICE_RECORD_NAME, Internal.REPOSITORY_SERVICE_RECORD_DISCOVERY, TaskRepository.class);
        //make this service available
        serviceDiscovery.publish(record,registrationResult);
        return registrationResult.future();
    }

    private void startMetrics(){
        if(vertx.isMetricsEnabled()){
            metricsUpdater = vertx.periodicStream(1000)
                    .handler(l -> countTasks(null)
                            .onSuccess(res -> vertx.eventBus().publish(coronaMqOptions.getMetricsAddress(),new JsonObject().put("type","gauge").put("name","task_count").put("data",res)))
                            .onFailure(x->logger.error("Failed fetching task count "+x.getMessage(),x))
                    );
            metricsUpdater.exceptionHandler(x -> logger.error("Failed publishing metrics: "+x.getMessage(),x));
        }
    }

    @Override
    public Future<Void> stop() {
        if(this.messageConsumer == null){
            return Future.succeededFuture();
        }
        return gracefullyUnregisterFromServiceDiscovery()
                .compose(v-> unbindProxy())
                .onComplete(v-> stopMetrics());
    }

    Future<Void> gracefullyUnregisterFromServiceDiscovery(){
        if(!registrationResult.future().isComplete()){
            return Future.failedFuture(new IllegalStateException("Discovery not started."));
        }
        return registrationResult.future()
                //tell everyone that we are leaving
                .map(rec -> vertx.eventBus().publish(
                        coronaMqOptions.getServiceDiscoveryOptions().getAnnounceAddress(),
                        new Record(rec)
                                .setRegistration(null)
                                .setStatus(Status.DOWN)
                                .setMetadata(new JsonObject().put("shutdownInMillis",coronaMqOptions.getRepositoryGracefulShutdownMillis())).toJson())
                )
                //wait a period before we actually go down
                .compose(v-> waitForShutdown())
                //unregister from ServiceDiscovery and close resources
                .compose(v->registrationResult.future())
                .compose(rec->serviceDiscovery.unpublish(rec.getRegistration()))
                .onComplete(v->serviceDiscovery.close());
    }

    private Future<Void> waitForShutdown() {
        if(coronaMqOptions.getRepositoryGracefulShutdownMillis() == 0L){
            return Future.succeededFuture();
        }
        return Internal.await(vertx,coronaMqOptions.getRepositoryGracefulShutdownMillis());
    }

    private Future<Void> unbindProxy() {
        Promise<Void> unregisterPromise = Promise.promise();
        //manually unregister messageConsumer because binder.unregister returns void...
        messageConsumer.unregister(unregisterPromise);
        binder.unregister(messageConsumer);
        return unregisterPromise.future();
    }

    private void stopMetrics(){
        if(vertx.isMetricsEnabled()){
            metricsUpdater.cancel();
            metricsUpdater = null;
        }
    }


}
