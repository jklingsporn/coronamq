package de.badmonkee.coronamq.core.impl;

import de.badmonkee.coronamq.core.Broker;
import de.badmonkee.coronamq.core.CoronaMqOptions;
import de.badmonkee.coronamq.core.TaskQueueDao;
import de.badmonkee.coronamq.core.Worker;
import de.badmonkee.coronamq.core.bootstrap.BootstrapInitStep;
import de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

class BootstrapImpl implements BootstrapInitStep, BootstrapSpreadStep {

    private final Vertx vertx;
    private final CoronaMqOptions options;
    private final Broker broker;
    private final TaskQueueDao dao;
    private final CopyOnWriteArrayList<Worker> workers = new CopyOnWriteArrayList<>();
    private final AtomicBoolean spread = new AtomicBoolean(false);
    private final AtomicBoolean vaccinated = new AtomicBoolean(false);

    private Future<Void> state = Future.succeededFuture();

    BootstrapImpl(Vertx vertx, CoronaMqOptions options) {
        this.vertx = vertx;
        this.options = options;
        this.broker = CoronaMq.broker(vertx,options);
        this.dao = CoronaMq.dao(vertx,options);
    }


    @Override
    public BootstrapInitStep withWorker(Worker worker) {
        if(spread.get()){
            throw new IllegalStateException("Already spread");
        }
        if (vaccinated.get()) {
            throw new IllegalStateException("Already vaccinated");
        }
        workers.add(worker);
        return this;
    }

    @Override
    public Future<BootstrapSpreadStep> spread() {
        if(!spread.compareAndSet(false,true)){
            return Future.failedFuture(new IllegalStateException("Already spread"));
        }
        if (vaccinated.get()) {
            return Future.failedFuture(new IllegalStateException("Already vaccinated"));
        }
        synchronized(this){
            this.state = dao.start()
                    .compose(v -> broker.start())
                    .compose(v -> CompositeFuture.all(workers.stream().map(Worker::start).collect(Collectors.toList())).<Void>mapEmpty());
        }
        return this.state.map(this);
    }

    @Override
    public Future<BootstrapSpreadStep> addWorker(Worker worker) {
        if (!spread.get()) {
            return Future.failedFuture(new IllegalStateException("Not yet spread"));
        }
        if (vaccinated.get()) {
            return Future.failedFuture(new IllegalStateException("Already vaccinated"));
        }
        this.workers.add(worker);
        synchronized(this){
            this.state = this.state.compose(v -> worker.start());
        }
        return state.map(this);
    }

    @Override
    public Future<BootstrapSpreadStep> removeWorker(Worker worker) {
        if (!spread.get()) {
            return Future.failedFuture(new IllegalStateException("Not yet spread"));
        }
        if (vaccinated.get()) {
            return Future.failedFuture(new IllegalStateException("Already vaccinated"));
        }
        if(!workers.remove(worker)){
            return Future.failedFuture(new IllegalStateException("Worker does not exist"));
        }
        synchronized(this){
            this.state = this.state.compose(v->worker.stop());
        }
        return this.state.map(this);
    }

    @Override
    public Future<UUID> publishTask(String label, JsonObject payload) {
        return CoronaMq.publishTask(vertx,options.getDaoAddress(),label,payload);
    }

    @Override
    public Future<Void> vaccinate() {
        if (!spread.get()) {
            return Future.failedFuture(new IllegalStateException("Not yet spread"));
        }
        if (!vaccinated.compareAndSet(false,true)) {
            return Future.failedFuture(new IllegalStateException("Already vaccinated"));
        }
        synchronized(this){
            List<Worker> workersCopy = new ArrayList<>(workers);
            workers.clear();
            this.state = this.state
                    .compose(v->broker.stop())
                    .compose(v->CompositeFuture.all(workersCopy.stream().map(Worker::stop).collect(Collectors.toList())).<Void>mapEmpty())
                    .compose(v->dao.stop())
            ;
        }
        return this.state;
    }
}
