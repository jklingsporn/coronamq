>The simplest way to implement a task queue with Java, Vertx and PostgreSQL. 

# Simple
There are only three participants in CoronaMQ you have to reason about:
1. **Worker(s)** act on new tasks added to the queue. Workers are bound to a label which describes the unit of work.
There can be multiple labels (e.g. PLACE_ORDER, CHECKOUT, etc) and thus workers.
2. The **broker** listens to additions made to the task queue and *send*s these tasks over the EventBus. There should only
be one broker per application.
3. The **TaskQueueDao** is interacting with the queue in the database. You can deploy it together with the broker, but you don't have to.\

There is also the **Publisher**: A publisher can add tasks to the queue by sending a message on the EventBus. The publisher is not
required as you can also publish tasks directly to the EventBus.\

![Corona MQ Overview](doc/img/CoronaMQOverview.png?raw=true "Corona MQ Overview")

# Fast
Thanks to PostgresSQL's [NOTIFY/LISTEN](https://www.postgresql.org/docs/current/sql-notify.html) and the [fastest PostgresSQL driver for Java](https://github.com/eclipse-vertx/vertx-sql-client) 
out there, tasks are instantly pushed to the EventBus. There is no polling. To empty the task queue as soon as possible, workers are 
activated in three situations:
- when they receive a new task from the EventBus
- when they are started they request a new task from the queue
- when they've done completing a task they request a new task from the queue

# Persistent
The tasks are stored in a PostgreSQL database guaranteeing durability and consistency. Your application might already use a PostgreSQL
database in the persistence layer so you don't have to bring another player to your system architecture. The fewer players, the less errors.  

# Once
Many queues out there guarantee `at least once`-delivery which means tasks might get handled twice. But what you really want 
is `exactly once` delivery. You have one job and it should be done once. However, in a real world, there are network timeouts, 
database errors et al so the best you can get is `effectively once` delivery and this is what CoronaMQ aims for.

# Initial setup
- For a quick test, you can build and use the provided docker file.
- All others need to add the following SQL to their existing database:
```
CREATE TYPE task_status AS ENUM ('NEW', 'RUNNING','COMPLETED','FAILED');
CREATE TABLE tasks(id UUID,label text, 	payload JSONB, status task_status, update_time timestamp);

CREATE OR REPLACE FUNCTION task_status_notify()
	RETURNS trigger AS
$$
BEGIN
	PERFORM pg_notify('coronamq_task_update', jsonb_build_object('id', NEW.id::text, 'payload',new.payload, 'label',new.label)::text);
	RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER task_status_change
	AFTER INSERT
	ON tasks
	FOR EACH ROW
EXECUTE PROCEDURE task_status_notify();
``` 

# Usage
The participants have `start`- and `stop`-methods which have to be invoked after they've been created. It is important 
to start and stop them in the right order or otherwise data might get lost.\
**Start order**:
 1. TaskQueueDao
 2. Worker
 3. Broker

**Stop order**:
1. Broker
2. Worker
3. TaskQueueDao

## Code example
```
@Test
public void basicExample(Vertx vertx, VertxTestContext testContext){

    //the broker sends a new task onto the eventbus when it is added
    Broker broker = CoronaMq.broker(vertx);

    //CRUD for tasks
    TaskQueueDao taskQueueDao = CoronaMq.dao(vertx);

    //Some work to do
    SimpleWorker simpleWorker = new SimpleWorker(vertx, new CoronaMqOptions());

    //Add some tasks to the queue
    Publisher publisher = CoronaMq.publisher(vertx);

    testContext
            //start participants in the right order
            .assertComplete(taskQueueDao.start() //dao first
                    .compose(v->broker.start()) //... then the broker
                    .compose(v->simpleWorker.start()) //... 
            )
            //send a new task to the queue
            .compose(v-> publisher.publishTask("test",new JsonObject().put("someValue","hi")))
            //complete the work
            .compose(v-> simpleWorker.getCurrentWork())
            .onSuccess(res -> testContext.completeNow())
            .onFailure(testContext::failNow)
    ;
}
```