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
 * Any instance that can dispatch tasks to the queue.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.github.jklingsporn.coronamq.core.Dispatcher original} non RX-ified interface using Vert.x codegen.
 */

@RxGen(io.github.jklingsporn.coronamq.core.Dispatcher.class)
public class Dispatcher {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Dispatcher that = (Dispatcher) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<Dispatcher> __TYPE_ARG = new TypeArg<>(    obj -> new Dispatcher((io.github.jklingsporn.coronamq.core.Dispatcher) obj),
    Dispatcher::getDelegate
  );

  private final io.github.jklingsporn.coronamq.core.Dispatcher delegate;
  
  public Dispatcher(io.github.jklingsporn.coronamq.core.Dispatcher delegate) {
    this.delegate = delegate;
  }

  public Dispatcher(Object delegate) {
    this.delegate = (io.github.jklingsporn.coronamq.core.Dispatcher)delegate;
  }

  public io.github.jklingsporn.coronamq.core.Dispatcher getDelegate() {
    return delegate;
  }


  /**
   * Dispatch a task with the given label and payload
   * @param label the label
   * @param payload the payload
   * @return a  with the id associated with task.
   */
  public io.reactivex.rxjava3.core.Single<java.lang.String> dispatch(java.lang.String label, io.vertx.core.json.JsonObject payload) { 
    io.reactivex.rxjava3.core.Single<java.lang.String> ret = rxDispatch(label, payload);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * Dispatch a task with the given label and payload
   * @param label the label
   * @param payload the payload
   * @return a  with the id associated with task.
   */
  public io.reactivex.rxjava3.core.Single<java.lang.String> rxDispatch(java.lang.String label, io.vertx.core.json.JsonObject payload) { 
    return AsyncResultSingle.toSingle(delegate.dispatch(label, payload), __value -> __value);
  }

  public static Dispatcher newInstance(io.github.jklingsporn.coronamq.core.Dispatcher arg) {
    return arg != null ? new Dispatcher(arg) : null;
  }

}
