package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import de.badmonkee.coronamq.core.TaskQueueDao;
import de.badmonkee.coronamq.core.TaskStatus;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.MessageConsumer;
import io.vertx.core.impl.Arguments;
import io.vertx.core.json.JsonObject;
import io.vertx.pgclient.PgPool;
import io.vertx.serviceproxy.ServiceBinder;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.SqlResult;
import io.vertx.sqlclient.Tuple;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author jensklingsporn
 */
class TaskQueueDaoImpl implements TaskQueueDao {

    private final CoronaMqOptions coronaMqOptions;
    private final PgPool pool;
    private final ServiceBinder binder;
    private MessageConsumer<JsonObject> messageConsumer;


    public TaskQueueDaoImpl(Vertx vertx, CoronaMqOptions coronaMqOptions, PgPool pool){
        this.coronaMqOptions = coronaMqOptions;
        this.pool = pool;
        this.binder = new ServiceBinder(vertx);
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
        this.messageConsumer = binder.setAddress(coronaMqOptions.getDaoAddress()).register(TaskQueueDao.class, this);
        Promise<Void> registrationPromise = Promise.promise();
        this.messageConsumer.completionHandler(registrationPromise);
        return registrationPromise.future();
    }

    @Override
    public Future<Void> stop() {
        if(this.messageConsumer == null){
            return Future.succeededFuture();
        }
        Promise<Void> unregisterPromise = Promise.promise();
        //manually unregister messageConsumer because binder.unregister returns void...
        messageConsumer.unregister(unregisterPromise);
        binder.unregister(messageConsumer);
        return unregisterPromise.future();
    }

    private <T> Future<T> failWithCode(int code,Throwable x) {
        return Future.failedFuture(new ServiceException(code, x.getMessage()));
    }
}
