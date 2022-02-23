package de.badmonkee.coronamq.core;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.pgclient.PgConnectOptions;
import io.vertx.servicediscovery.ServiceDiscoveryOptions;

@DataObject
public class CoronaMqOptions {

    private static final String defaultChannelName = "coronamq_task_update";
    private static final String defaultWorkerAddress = "coronamq.task.run.";
    private static final String defaultDaoAddress = "coronamq.dao";
    private static final long defaultDaoGracefulShutdownMillis = 1000;

    private static final ServiceDiscoveryOptions serviceDiscoveryOptions = new ServiceDiscoveryOptions()
            .setAnnounceAddress("coronamq.discovery.announce")
            .setUsageAddress("coronamq.discovery.usage");

    private String channelName;
    private String workerAddress;
    private String daoAddress;
    private long daoGracefulShutdownMillis;
    private PgConnectOptions connectOptions;

    public CoronaMqOptions() {
        this(
                defaultChannelName,
                defaultWorkerAddress,
                defaultDaoAddress,
                defaultDaoGracefulShutdownMillis,
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
                           long daoGracefulShutdownMillis,
                           PgConnectOptions connectOptions) {
        this.channelName = channelName;
        this.workerAddress = workerAddress;
        this.daoAddress = daoAddress;
        this.daoGracefulShutdownMillis = daoGracefulShutdownMillis;
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

    public ServiceDiscoveryOptions getServiceDiscoveryOptions(){
        return serviceDiscoveryOptions;
    }

    public long getDaoGracefulShutdownMillis() {
        return daoGracefulShutdownMillis;
    }

    public CoronaMqOptions setDaoGracefulShutdownMillis(long daoGracefulShutdownMillis) {
        if(daoGracefulShutdownMillis<0){
            throw new IllegalArgumentException("value can not be negative");
        }
        this.daoGracefulShutdownMillis = daoGracefulShutdownMillis;
        return this;
    }
}
