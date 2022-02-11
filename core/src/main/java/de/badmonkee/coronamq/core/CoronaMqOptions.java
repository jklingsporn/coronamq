package de.badmonkee.coronamq.core;

import io.vertx.pgclient.PgConnectOptions;

public class CoronaMqOptions {

    private static final String defaultChannelName = "coronamq_task_update";
    private static final String defaultWorkerAddress = "coronamq.task.run.";
    private static final String defaultDaoAddress = "coronamq.dao";

    private String channelName;
    private String workerAddress;
    private String daoAddress;
    private PgConnectOptions connectOptions;

    public CoronaMqOptions() {
        this(
                defaultChannelName,
                defaultWorkerAddress,
                defaultDaoAddress,
                new PgConnectOptions()
                    .setPort(5432)
                    .setHost("localhost")
                    .setDatabase("postgres")
                    .setUser("coronamq")
                    .setPassword("vertx"));
    }

    public CoronaMqOptions(String channelName,
                           String workerAddress,
                           String daoAddress,
                           PgConnectOptions connectOptions) {
        this.channelName = channelName;
        this.workerAddress = workerAddress;
        this.daoAddress = daoAddress;
        this.connectOptions = connectOptions;
    }

    public String getChannelName() {
        return channelName;
    }

    public CoronaMqOptions setChannelName(String channelName) {
        this.channelName = channelName;
        return this;
    }

    public PgConnectOptions getConnectOptions() {
        return connectOptions;
    }

    public CoronaMqOptions setConnectOptions(PgConnectOptions connectOptions) {
        this.connectOptions = connectOptions;
        return this;
    }

    public String getWorkerAddress() {
        return workerAddress;
    }

    public CoronaMqOptions setWorkerAddress(String workerAddress) {
        this.workerAddress = workerAddress;
        return this;
    }

    public String getDaoAddress() {
        return daoAddress;
    }

    public CoronaMqOptions setDaoAddress(String daoAddress) {
        this.daoAddress = daoAddress;
        return this;
    }
}
