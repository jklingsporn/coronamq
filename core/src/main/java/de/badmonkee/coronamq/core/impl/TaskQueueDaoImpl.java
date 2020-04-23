package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.TaskQueueDao;
import de.badmonkee.coronamq.core.TaskStatus;
import de.badmonkee.coronamq.core.CoronaMqOptions;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.impl.Arguments;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author jensklingsporn
 */
class TaskQueueDaoImpl implements TaskQueueDao {

    private final Vertx vertx;
    private final CoronaMqOptions coronaMqOptions;
    private final PgPool pool;
    private Collection<MessageConsumer<JsonObject>> messageConsumers;


    public TaskQueueDaoImpl(Vertx vertx, CoronaMqOptions coronaMqOptions, PgPool pool){
        this.vertx = vertx;
        this.coronaMqOptions = coronaMqOptions;
        this.pool = pool;
    }

    @Override
    public Future<UUID> createTask(String label, JsonObject payload) {
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
        return completion.future().map(newId);
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
                .mapEmpty();
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
                .mapEmpty();
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
        });
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
    public Future<Long> countTasks(String label) {
        Promise<RowSet<Row>> completion = Promise.promise();
        pool.preparedQuery("SELECT COUNT(*) FROM tasks WHERE label = $1").execute( Tuple.of(label),completion);
        return completion.future().map(res -> res.iterator().next().getLong(0));
    }

    @Override
    public Future<Integer> deleteTask(String id) {
        Promise<RowSet<Row>> completion = Promise.promise();
        pool.preparedQuery("DELETE FROM tasks WHERE id = $1").execute( Tuple.of(UUID.fromString(id)),completion);
        return completion.future().map(SqlResult::rowCount);
    }

    @Override
    public Future<Void> start() {
        MessageConsumer<JsonObject> publishConsumer = vertx.eventBus().<JsonObject>consumer(coronaMqOptions.getTaskPublishAddress(), msg ->
                createTask(msg.body().getString("label"), msg.body().getJsonObject("payload"))
                        .onSuccess(uuid -> msg.reply(new JsonObject().put("id", uuid.toString())))
                        .onFailure(err -> msg.fail(Internal.CODE_ERROR_PUBLISH, err.getMessage()))
        );
        MessageConsumer<JsonObject> updateConsumer = vertx.eventBus().<JsonObject>consumer(coronaMqOptions.getTaskUpdateAddress(), msg ->
                updateTask(
                        (msg.body().getString("id")),
                        TaskStatus.valueOf(msg.body().getString("newStatus")),
                        TaskStatus.valueOf(msg.body().getString("oldStatus"))
                )
                        .onSuccess(msg::reply)
                        .onFailure(err -> msg.fail(Internal.CODE_ERROR_UPDATE, err.getMessage()))
        );
        MessageConsumer<JsonObject> requestConsumer = vertx.eventBus().<JsonObject>consumer(coronaMqOptions.getTaskRequestAddress(), msg ->
                requestTask(msg.body().getString("label"))
                        .onSuccess(msg::reply)
                        .onFailure(err -> msg.fail(Internal.CODE_ERROR_REQUEST, err.getMessage()))
        );
        MessageConsumer<JsonObject> failConsumer = vertx.eventBus().<JsonObject>consumer(coronaMqOptions.getTaskFailureAddress(), msg ->
                failTask(
                        (msg.body().getString("id")),
                        msg.body().getString("cause")
                )
                        .onSuccess(msg::reply)
                        .onFailure(err -> msg.fail(Internal.CODE_ERROR_FAIL, err.getMessage()))
        );
        this.messageConsumers = Arrays.asList(
                publishConsumer,
                updateConsumer,
                requestConsumer,
                failConsumer
        );
        return CompositeFuture.all(this.messageConsumers
                .stream()
                .map(c -> {
                    Promise<Void> promise = Promise.promise();
                    c.completionHandler(promise);
                    return promise.future();
                })
                .collect(Collectors.toList()))
                .mapEmpty();
    }

    @Override
    public Future<Void> stop() {
        if(this.messageConsumers == null){
            return Future.succeededFuture();
        }
        return CompositeFuture.all(this.messageConsumers
                .stream()
                .map(c -> {
                    Promise<Void> promise = Promise.promise();
                    c.unregister(promise);
                    return promise.future();
                })
                .collect(Collectors.toList()))
                .mapEmpty();
    }
}
