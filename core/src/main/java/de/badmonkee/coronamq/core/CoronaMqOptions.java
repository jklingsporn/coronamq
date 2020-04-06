package de.badmonkee.coronamq.core;

import io.vertx.pgclient.PgConnectOptions;

public class CoronaMqOptions {

    private static final String defaultChannelName = "coronamq_task_update";
    private static final String defaultWorkerAddress = "coronamq.task.run.";
    private static final String defaultTaskFailureAddress = "coronamq.task.fail";
    private static final String defaultTaskPublishAddress = "coronamq.task.publish";
    private static final String defaultTaskUpdateAddress = "coronamq.task.update";
    private static final String defaultTaskRequestAddress = "coronamq.task.request";

    private String channelName;
    private String workerAddress;
    private String taskPublishAddress;
    private String taskUpdateAddress;
    private String taskRequestAddress;
    private String taskFailureAddress;
    private PgConnectOptions connectOptions;

    public CoronaMqOptions() {
        this(
                defaultChannelName,
                defaultWorkerAddress,
                defaultTaskPublishAddress,
                defaultTaskUpdateAddress,
                defaultTaskRequestAddress,
                defaultTaskFailureAddress,
                new PgConnectOptions()
                    .setPort(5432)
                    .setHost("127.0.0.1")
                    .setDatabase("postgres")
                    .setUser("coronamq")
                    .setPassword("vertx"));
    }

    public CoronaMqOptions(String channelName,
                           String workerAddress,
                           String taskPublishAddress,
                           String taskUpdateAddress,
                           String taskRequestAddress,
                           String taskFailureAddress,
                           PgConnectOptions connectOptions) {
        this.channelName = channelName;
        this.workerAddress = workerAddress;
        this.taskPublishAddress = taskPublishAddress;
        this.taskUpdateAddress = taskUpdateAddress;
        this.taskRequestAddress = taskRequestAddress;
        this.taskFailureAddress = taskFailureAddress;
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

    public String getTaskPublishAddress() {
        return taskPublishAddress;
    }

    public CoronaMqOptions setTaskPublishAddress(String taskPublishAddress) {
        this.taskPublishAddress = taskPublishAddress;
        return this;
    }

    public String getTaskUpdateAddress() {
        return taskUpdateAddress;
    }

    public CoronaMqOptions setTaskUpdateAddress(String taskUpdateAddress) {
        this.taskUpdateAddress = taskUpdateAddress;
        return this;
    }

    public String getTaskRequestAddress() {
        return taskRequestAddress;
    }

    public CoronaMqOptions setTaskRequestAddress(String taskRequestAddress) {
        this.taskRequestAddress = taskRequestAddress;
        return this;
    }

    public String getTaskFailureAddress() {
        return taskFailureAddress;
    }

    public CoronaMqOptions setTaskFailureAddress(String taskFailureAddress) {
        this.taskFailureAddress = taskFailureAddress;
        return this;
    }
}
