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

package io.github.jklingsporn.coronamq.core.rxjava3;

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

/**
 * A worker to work on tasks from the task queue. A worker is bound to a 'label' which describes the unit of work of this
 * worker.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.github.jklingsporn.coronamq.core.Worker original} non RX-ified interface using Vert.x codegen.
 */

@RxGen(io.github.jklingsporn.coronamq.core.Worker.class)
public class Worker {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Worker that = (Worker) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<Worker> __TYPE_ARG = new TypeArg<>(    obj -> new Worker((io.github.jklingsporn.coronamq.core.Worker) obj),
    Worker::getDelegate
  );

  private final io.github.jklingsporn.coronamq.core.Worker delegate;
  
  public Worker(io.github.jklingsporn.coronamq.core.Worker delegate) {
    this.delegate = delegate;
  }

  public Worker(Object delegate) {
    this.delegate = (io.github.jklingsporn.coronamq.core.Worker)delegate;
  }

  public io.github.jklingsporn.coronamq.core.Worker getDelegate() {
    return delegate;
  }


  /**
   * Do some async work with the task.
   * @param task the current task's payload
   * @return a  after the work is done.
   */
  public io.reactivex.rxjava3.core.Completable run(io.vertx.core.json.JsonObject task) { 
    io.reactivex.rxjava3.core.Completable ret = rxRun(task);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.CompletableHelper.nullObserver());
    return ret;
  }

  /**
   * Do some async work with the task.
   * @param task the current task's payload
   * @return a  after the work is done.
   */
  public io.reactivex.rxjava3.core.Completable rxRun(io.vertx.core.json.JsonObject task) { 
    return AsyncResultCompletable.toCompletable(delegate.run(task));
  }

  public io.reactivex.rxjava3.core.Completable start() { 
    io.reactivex.rxjava3.core.Completable ret = rxStart();
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.CompletableHelper.nullObserver());
    return ret;
  }

  public io.reactivex.rxjava3.core.Completable rxStart() { 
    return AsyncResultCompletable.toCompletable(delegate.start());
  }

  public io.reactivex.rxjava3.core.Completable stop() { 
    io.reactivex.rxjava3.core.Completable ret = rxStop();
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.CompletableHelper.nullObserver());
    return ret;
  }

  public io.reactivex.rxjava3.core.Completable rxStop() { 
    return AsyncResultCompletable.toCompletable(delegate.stop());
  }

  public static Worker newInstance(io.github.jklingsporn.coronamq.core.Worker arg) {
    return arg != null ? new Worker(arg) : null;
  }

}
