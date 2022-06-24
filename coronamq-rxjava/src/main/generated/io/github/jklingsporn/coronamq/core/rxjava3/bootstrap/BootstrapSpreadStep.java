/*
 * Copyright 2014 Red Hat, Inc.
 *
 * Red Hat licenses this file to you under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package io.github.jklingsporn.coronamq.core.rxjava3.bootstrap;

import io.vertx.lang.rx.RxGen;
import io.vertx.lang.rx.TypeArg;
import io.vertx.rxjava3.impl.AsyncResultCompletable;
import io.vertx.rxjava3.impl.AsyncResultSingle;

import java.util.stream.Collectors;


@RxGen(io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep.class)
public class BootstrapSpreadStep {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BootstrapSpreadStep that = (BootstrapSpreadStep) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<BootstrapSpreadStep> __TYPE_ARG = new TypeArg<>(    obj -> new BootstrapSpreadStep((io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep) obj),
    BootstrapSpreadStep::getDelegate
  );

  private final io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep delegate;
  
  public BootstrapSpreadStep(io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep delegate) {
    this.delegate = delegate;
  }

  public BootstrapSpreadStep(Object delegate) {
    this.delegate = (io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep)delegate;
  }

  public io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep getDelegate() {
    return delegate;
  }

  private static final TypeArg<io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> TYPE_ARG_0 = new TypeArg<io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep>(o1 -> io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep.newInstance((io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep)o1), o1 -> o1.getDelegate());
  private static final TypeArg<io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> TYPE_ARG_1 = new TypeArg<io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep>(o1 -> io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep.newInstance((io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep)o1), o1 -> o1.getDelegate());
  private static final TypeArg<io.github.jklingsporn.coronamq.core.rxjava3.Worker> TYPE_ARG_2 = new TypeArg<io.github.jklingsporn.coronamq.core.rxjava3.Worker>(o1 -> io.github.jklingsporn.coronamq.core.rxjava3.Worker.newInstance((io.github.jklingsporn.coronamq.core.Worker)o1), o1 -> o1.getDelegate());

  /**
   * Adds and deploys a Worker to this boostrap.
   * @param worker a worker
   * @return the <code>Future</code> that completes when the worker is registered to the EventBus.
   */
  public io.reactivex.rxjava3.core.Single<io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> addWorker(io.github.jklingsporn.coronamq.core.rxjava3.Worker worker) { 
    io.reactivex.rxjava3.core.Single<io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> ret = rxAddWorker(worker);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * Adds and deploys a Worker to this boostrap.
   * @param worker a worker
   * @return the <code>Future</code> that completes when the worker is registered to the EventBus.
   */
  public io.reactivex.rxjava3.core.Single<io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> rxAddWorker(io.github.jklingsporn.coronamq.core.rxjava3.Worker worker) { 
    return AsyncResultSingle.toSingle(delegate.addWorker(worker.getDelegate()), __value -> io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep.newInstance((io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep)__value));
  }

  /**
   * Removes a Worker from this boostrap and unregisters it from the EventBus.
   * @param worker a worker
   * @return the <code>Future</code> that completes when the worker is unregistered from the EventBus.
   */
  public io.reactivex.rxjava3.core.Single<io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> removeWorker(io.github.jklingsporn.coronamq.core.rxjava3.Worker worker) { 
    io.reactivex.rxjava3.core.Single<io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> ret = rxRemoveWorker(worker);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * Removes a Worker from this boostrap and unregisters it from the EventBus.
   * @param worker a worker
   * @return the <code>Future</code> that completes when the worker is unregistered from the EventBus.
   */
  public io.reactivex.rxjava3.core.Single<io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> rxRemoveWorker(io.github.jklingsporn.coronamq.core.rxjava3.Worker worker) { 
    return AsyncResultSingle.toSingle(delegate.removeWorker(worker.getDelegate()), __value -> io.github.jklingsporn.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep.newInstance((io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep)__value));
  }

  /**
   * Dispatches a task given this bootstrap's configuration
   * @param label the task's label
   * @param payload the task's payload
   * @return the id of the task.
   */
  public io.reactivex.rxjava3.core.Single<java.lang.String> dispatch(java.lang.String label, io.vertx.core.json.JsonObject payload) { 
    io.reactivex.rxjava3.core.Single<java.lang.String> ret = rxDispatch(label, payload);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * Dispatches a task given this bootstrap's configuration
   * @param label the task's label
   * @param payload the task's payload
   * @return the id of the task.
   */
  public io.reactivex.rxjava3.core.Single<java.lang.String> rxDispatch(java.lang.String label, io.vertx.core.json.JsonObject payload) { 
    return AsyncResultSingle.toSingle(delegate.dispatch(label, payload), __value -> __value);
  }

  /**
   * Stops all services in the right order.
   * @return a <code>Future</code> that completes once all participants have been unregistered.
   */
  public io.reactivex.rxjava3.core.Completable vaccinate() { 
    io.reactivex.rxjava3.core.Completable ret = rxVaccinate();
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.CompletableHelper.nullObserver());
    return ret;
  }

  /**
   * Stops all services in the right order.
   * @return a <code>Future</code> that completes once all participants have been unregistered.
   */
  public io.reactivex.rxjava3.core.Completable rxVaccinate() { 
    return AsyncResultCompletable.toCompletable(delegate.vaccinate());
  }

  /**
   * @return the broker or null.
   */
  public io.github.jklingsporn.coronamq.core.rxjava3.Broker getBroker() { 
    io.github.jklingsporn.coronamq.core.rxjava3.Broker ret = io.github.jklingsporn.coronamq.core.rxjava3.Broker.newInstance((io.github.jklingsporn.coronamq.core.Broker)delegate.getBroker());
    return ret;
  }

  /**
   * @return the repository or null
   */
  public io.github.jklingsporn.coronamq.core.rxjava3.TaskRepository getRepository() { 
    io.github.jklingsporn.coronamq.core.rxjava3.TaskRepository ret = io.github.jklingsporn.coronamq.core.rxjava3.TaskRepository.newInstance((io.github.jklingsporn.coronamq.core.TaskRepository)delegate.getRepository());
    return ret;
  }

  /**
   * @return all current workers, never null.
   */
  public java.util.List<io.github.jklingsporn.coronamq.core.rxjava3.Worker> getWorkers() { 
    java.util.List<io.github.jklingsporn.coronamq.core.rxjava3.Worker> ret = delegate.getWorkers().stream().map(elt -> io.github.jklingsporn.coronamq.core.rxjava3.Worker.newInstance((io.github.jklingsporn.coronamq.core.Worker)elt)).collect(Collectors.toList());
    return ret;
  }

  public static BootstrapSpreadStep newInstance(io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep arg) {
    return arg != null ? new BootstrapSpreadStep(arg) : null;
  }

}
