package io.github.jklingsporn.coronamq.core;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

@DataObject
public class CoronaMqOptions {

    private static final String defaultChannelName = "coronamq_task_update";
    private static final String defaultWorkerAddress = "coronamq.task.run.";
    private static final String defaultRepositoryAddress = "coronamq.repository";
    private static final String defaultMetricsAddress = "coronamq.metrics";
    private static final long defaultRepositoryGracefulShutdownMillis = 1000;

    private static final ServiceDiscoveryOptions serviceDiscoveryOptions = new ServiceDiscoveryOptions()
            .setAnnounceAddress("coronamq.discovery.announce")
            .setUsageAddress("coronamq.discovery.usage");

    private String channelName;
    private String workerAddress;
    private String repositoryAddress;
    private String metricsAddress;
    private long repositoryGracefulShutdownMillis;
    private PgConnectOptions connectOptions;

    public CoronaMqOptions() {
        this(
                defaultChannelName,
                defaultWorkerAddress,
                defaultRepositoryAddress,
                defaultMetricsAddress,
                defaultRepositoryGracefulShutdownMillis,
                new PgConnectOptions()
                    .setPort(5432)
                    .setHost("localhost")
                    .setDatabase("postgres")
                    .setUser("coronamq")
                    .setPassword("vertx"));
    }

    public CoronaMqOptions(String channelName,
                           String workerAddress,
                           String repositoryAddress,
                           String metricsAddress,
                           long repositoryGracefulShutdownMillis,
                           PgConnectOptions connectOptions) {
        this.channelName = channelName;
        this.workerAddress = workerAddress;
        this.repositoryAddress = repositoryAddress;
        this.metricsAddress = metricsAddress;
        this.repositoryGracefulShutdownMillis = repositoryGracefulShutdownMillis;
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

    public String getRepositoryAddress() {
        return repositoryAddress;
    }

    public CoronaMqOptions setRepositoryAddress(String repositoryAddress) {
        this.repositoryAddress = repositoryAddress;
        return this;
    }

    public ServiceDiscoveryOptions getServiceDiscoveryOptions(){
        return serviceDiscoveryOptions;
    }

    public long getRepositoryGracefulShutdownMillis() {
        return repositoryGracefulShutdownMillis;
    }

    public CoronaMqOptions setRepositoryGracefulShutdownMillis(long repositoryGracefulShutdownMillis) {
        if(repositoryGracefulShutdownMillis<0){
            throw new IllegalArgumentException("value can not be negative");
        }
        this.repositoryGracefulShutdownMillis = repositoryGracefulShutdownMillis;
        return this;
    }

    public String getMetricsAddress() {
        return metricsAddress;
    }

    public CoronaMqOptions setMetricsAddress(String metricsAddress) {
        this.metricsAddress = metricsAddress;
        return this;
    }
}
