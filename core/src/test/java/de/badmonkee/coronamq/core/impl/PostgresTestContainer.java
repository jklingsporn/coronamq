package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.CoronaMqOptions;
import io.vertx.pgclient.PgConnectOptions;
import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresTestContainer extends PostgreSQLContainer<PostgresTestContainer> {

    private CoronaMqOptions testOptions;

    public PostgresTestContainer() {
        super("postgres:14.1");
    }

    @Override
    public void start() {
        withUsername("coronamq")
                .withPassword("vertx")
                .withInitScript("testcontainers/init.sql")
                .withDatabaseName("postgres");
        super.start();
        testOptions = new CoronaMqOptions().setConnectOptions(new PgConnectOptions()
                .setPort(getFirstMappedPort())
                .setHost(getHost())
                .setDatabase("postgres")
                .setUser("coronamq")
                .setPassword("vertx"));
    }

    public CoronaMqOptions getCoronaMqOptions() {
        return testOptions;
    }
}
