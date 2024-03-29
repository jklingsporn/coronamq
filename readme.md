>The simplest way to implement a task queue with Java, Vertx and PostgreSQL. 

# Simple
There are only three participants in CoronaMQ you have to reason about:
1. **Worker(s)** process tasks that are added to the queue. Workers are bound to a label which describes the unit of work.
There can be multiple labels (e.g. PLACE_ORDER, CHECKOUT, etc) and thus workers.
2. The **broker** listens to inserts made to the task queue and *send*s these tasks over the EventBus. 
3. The **TaskRepository** is interacting with the queue in the database. You can deploy it together with the broker, 
but you don't have to.

There is also the **Dispatcher**: A dispatcher can add tasks to the queue by sending a message on the EventBus. Another
way to add tasks is by adding them directly into the [tasks-table](#initial-setup). 


![Corona MQ Overview](doc/img/CoronaMQOverview.png?raw=true "Corona MQ Overview")

# Fast 
Thanks to PostgresSQL's [NOTIFY/LISTEN](https://www.postgresql.org/docs/current/sql-notify.html) and the
[fastest PostgresSQL driver for Java](https://github.com/eclipse-vertx/vertx-sql-client) [^1] 
out there, tasks are instantly pushed to the EventBus. There is no polling. To empty the task queue as fast as possible, 
workers additionally request tasks when they are deployed and after they've completed a task.

# Persistent 
The tasks are stored in a PostgreSQL database guaranteeing durability and consistency. Your application might already 
use a PostgreSQL database in the persistence layer, so you don't have to bring another player to your system architecture.
The fewer players, the fewer errors.  

# Once
Many queues out there guarantee `at least once`-delivery which means tasks might get handled twice. But what you really want 
is `exactly once` delivery. You have one job - and it should be done once. However, in a real world, there are network timeouts, 
database errors et al. so the best you can get is `effectively once` delivery and this is what CoronaMQ aims for.

# Usage 

## Maven
CoronaMQ supports the following API-flavors:

### vertx-Future
```
<dependency>
    <groupId>io.github.jklingsporn</groupId>
    <artifactId>coronamq-core</artifactId>
    <version>0.2</version>
</dependency>
```
### Mutiny
```
<dependency>
    <groupId>io.github.jklingsporn</groupId>
    <artifactId>coronamq-mutiny</artifactId>
    <version>0.2</version>
</dependency>
```
### rxjava3
```
<dependency>
    <groupId>io.github.jklingsporn</groupId>
    <artifactId>coronamq-rxjava</artifactId>
    <version>0.2</version>
</dependency>
```

## Initial setup
- To run the examples, you need a running docker daemon.
- For a more advanced test, you can use the provided docker file.
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

## Code example

The following example uses the `Future`-API
```
@Test
public void boostrapExample(Vertx vertx, VertxTestContext testContext){
    //configurable options
    CoronaMqOptions coronaMqOptions = new CoronaMqOptions();
    
    //a simple worker that just completes any task it receives
    SimpleWorker worker = new SimpleWorker(vertx, coronaMqOptions);
    
    //an example configuration with a broker, repository and worker
    Future<BootstrapSpreadStep> spread = CoronaMq.create(vertx,coronaMqOptions)
            .withBroker()
            .withRepository()
            .withWorker(worker)
            .spread(); //spread the wor(l)d
            
    testContext
            .assertComplete(spread)
            //send a new task with "test"-label to the queue
            .compose(s-> s.dispatch("test",new JsonObject().put("someValue","hi")))
            .onComplete(testContext.succeeding(UUID::fromString))
            //complete the work
            .compose(v-> worker.getCurrentWork())
            //shut down all components
            .compose(v->spread.compose(BootstrapSpreadStep::vaccinate))
            .onComplete(testContext.succeedingThenComplete())
    ;
}
```
More examples can be found in the [examples-module](/coronamq-examples).

# Awesome
This project is a showcase for various cool features that go beyond a simple "Hello world"-example:
- [Vertx Service Discovery](https://vertx.io/docs/vertx-service-discovery/java/) to detect the repository's availability
- [Vertx Service Proxies](https://vertx.io/docs/vertx-service-proxy/java/) to remotely interoperate with the repository using
it's interface
- [Vertx Codegen](https://github.com/vert-x3/vertx-codegen) to generate [Mutiny](https://smallrye.io/smallrye-mutiny/)-
  and [RxJava](https://github.com/ReactiveX/RxJava)-APIs
- [Testcontainers](https://www.testcontainers.org/) for running the integration tests
- [Micrometer Metrics](https://vertx.io/docs/vertx-micrometer-metrics/java/) to see what's going on 

# Community has spoken
I originally created the project under the name PoXMQ (**Po**stgres Vert**X** **MQ**) but wasn't
completely satisfied with it. So I made a poll on [twitter](https://twitter.com/klingspoon/status/1245657559484076034) - what could possibly go wrong?
![Never feed the trolls](doc/img/NeverFeedTheTrolls.png?raw=true "never feed the trolls")

# Footnotes :mask:
[^1]: https://www.techempower.com/benchmarks/#section=data-r15&hw=ph&test=db