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

package de.badmonkee.coronamq.core.rxjava3.bootstrap;

import io.vertx.rxjava3.RxHelper;
import io.vertx.rxjava3.ObservableHelper;
import io.vertx.rxjava3.FlowableHelper;
import io.vertx.rxjava3.impl.AsyncResultMaybe;
import io.vertx.rxjava3.impl.AsyncResultSingle;
import io.vertx.rxjava3.impl.AsyncResultCompletable;
import io.vertx.rxjava3.WriteStreamObserver;
import io.vertx.rxjava3.WriteStreamSubscriber;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Collectors;
import io.vertx.core.Handler;
import io.vertx.core.AsyncResult;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.lang.rx.RxGen;
import io.vertx.lang.rx.TypeArg;
import io.vertx.lang.rx.MappingIterator;


@RxGen(de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep.class)
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

  public static final TypeArg<BootstrapSpreadStep> __TYPE_ARG = new TypeArg<>(    obj -> new BootstrapSpreadStep((de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep) obj),
    BootstrapSpreadStep::getDelegate
  );

  private final de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep delegate;
  
  public BootstrapSpreadStep(de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep delegate) {
    this.delegate = delegate;
  }

  public BootstrapSpreadStep(Object delegate) {
    this.delegate = (de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep)delegate;
  }

  public de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep getDelegate() {
    return delegate;
  }

  private static final TypeArg<de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> TYPE_ARG_0 = new TypeArg<de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep>(o1 -> de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep.newInstance((de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep)o1), o1 -> o1.getDelegate());
  private static final TypeArg<de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> TYPE_ARG_1 = new TypeArg<de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep>(o1 -> de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep.newInstance((de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep)o1), o1 -> o1.getDelegate());

  /**
   * Adds and deploys a Worker to this boostrap.
   * @param worker a worker
   * @return the <code>Future</code> that completes when the worker is registered to the EventBus.
   */
  public io.reactivex.rxjava3.core.Single<de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> addWorker(de.badmonkee.coronamq.core.rxjava3.Worker worker) { 
    io.reactivex.rxjava3.core.Single<de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> ret = rxAddWorker(worker);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * Adds and deploys a Worker to this boostrap.
   * @param worker a worker
   * @return the <code>Future</code> that completes when the worker is registered to the EventBus.
   */
  public io.reactivex.rxjava3.core.Single<de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> rxAddWorker(de.badmonkee.coronamq.core.rxjava3.Worker worker) { 
    return AsyncResultSingle.toSingle(delegate.addWorker(worker.getDelegate()), __value -> de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep.newInstance((de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep)__value));
  }

  /**
   * Removes a Worker from this boostrap and unregisters it from the EventBus.
   * @param worker a worker
   * @return the <code>Future</code> that completes when the worker is unregistered from the EventBus.
   */
  public io.reactivex.rxjava3.core.Single<de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> removeWorker(de.badmonkee.coronamq.core.rxjava3.Worker worker) { 
    io.reactivex.rxjava3.core.Single<de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> ret = rxRemoveWorker(worker);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * Removes a Worker from this boostrap and unregisters it from the EventBus.
   * @param worker a worker
   * @return the <code>Future</code> that completes when the worker is unregistered from the EventBus.
   */
  public io.reactivex.rxjava3.core.Single<de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep> rxRemoveWorker(de.badmonkee.coronamq.core.rxjava3.Worker worker) { 
    return AsyncResultSingle.toSingle(delegate.removeWorker(worker.getDelegate()), __value -> de.badmonkee.coronamq.core.rxjava3.bootstrap.BootstrapSpreadStep.newInstance((de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep)__value));
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

  public static BootstrapSpreadStep newInstance(de.badmonkee.coronamq.core.bootstrap.BootstrapSpreadStep arg) {
    return arg != null ? new BootstrapSpreadStep(arg) : null;
  }

}
