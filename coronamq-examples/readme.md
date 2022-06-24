 A complete example of CoronaMQ can be found in the [CompleteExample](src/main/java/io/github/jklingsporn/coronamq/examples/complete/CompleteExample.java).
 class. It spawns several tasks and measures how long this setup takes to handle all the tasks.
 It assumes that there is a postgres database set up with a tasks-table and the triggers. Either run the provided
 <code>Dockerfile</code> from the parent or add the SQL from <code>coronamq-core/src/main/resources/00_setup.sql</code> to your existing DB.<br>
 Docker example:
 
 1. <code>docker build -t corona-mq . </code>
 2. <code>docker run -p 5432:5432 -e POSTGRES_HOST_AUTH_METHOD=trust coronamq </code>
 In order to watch the metrics, you need to
 
 3. Run prometheus server:
    <code>docker run -p 9090:9090 -v ${PWD}/coronamq-examples/src/main/resources/:/etc/prometheus prom/prometheus</code>
      (WATCH OUT: path to prometheus config only works when running from parent)
      -> 127.0.0.1:9090
 4. Run grafana server:
      <code>docker run -d -p 3000:3000 -it grafana/grafana-oss</code>
      -> goto 127.0.0.1:3000
      -> login / set new password
      -> create a prometheus datasource: http://host.docker.internal:9090
      -> import the dashboard from /coronamq-examples/src/main/resources/dashboard.json
 See also: https://github.com/vertx-howtos/metrics-prometheus-grafana-howto
 