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

import io.vertx.rxjava3.impl.AsyncResultCompletable;
import io.vertx.lang.rx.RxGen;
import io.vertx.lang.rx.TypeArg;

/**
 * A wrapper around a {@link io.vertx.rxjava3.pgclient.pubsub.PgSubscriber} - instance that listens to changes in the tasks table via NOTIFY/LISTEN.
 * There should be only one broker per application.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link io.github.jklingsporn.coronamq.core.Broker original} non RX-ified interface using Vert.x codegen.
 */

@RxGen(io.github.jklingsporn.coronamq.core.Broker.class)
public class Broker {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Broker that = (Broker) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<Broker> __TYPE_ARG = new TypeArg<>(    obj -> new Broker((io.github.jklingsporn.coronamq.core.Broker) obj),
    Broker::getDelegate
  );

  private final io.github.jklingsporn.coronamq.core.Broker delegate;
  
  public Broker(io.github.jklingsporn.coronamq.core.Broker delegate) {
    this.delegate = delegate;
  }

  public Broker(Object delegate) {
    this.delegate = (io.github.jklingsporn.coronamq.core.Broker)delegate;
  }

  public io.github.jklingsporn.coronamq.core.Broker getDelegate() {
    return delegate;
  }


  /**
   * @return a future that is completed when the {@link io.vertx.rxjava3.pgclient.pubsub.PgSubscriber} has performed it's connect-operation.
   */
  public io.reactivex.rxjava3.core.Completable start() { 
    io.reactivex.rxjava3.core.Completable ret = rxStart();
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.CompletableHelper.nullObserver());
    return ret;
  }

  /**
   * @return a future that is completed when the {@link io.vertx.rxjava3.pgclient.pubsub.PgSubscriber} has performed it's connect-operation.
   */
  public io.reactivex.rxjava3.core.Completable rxStart() { 
    return AsyncResultCompletable.toCompletable(delegate.start());
  }

  /**
   * @return a future that is completed when the {@link io.vertx.rxjava3.pgclient.pubsub.PgSubscriber} has performed it's close-operation.
   */
  public io.reactivex.rxjava3.core.Completable stop() { 
    io.reactivex.rxjava3.core.Completable ret = rxStop();
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.CompletableHelper.nullObserver());
    return ret;
  }

  /**
   * @return a future that is completed when the {@link io.vertx.rxjava3.pgclient.pubsub.PgSubscriber} has performed it's close-operation.
   */
  public io.reactivex.rxjava3.core.Completable rxStop() { 
    return AsyncResultCompletable.toCompletable(delegate.stop());
  }

  public static Broker newInstance(io.github.jklingsporn.coronamq.core.Broker arg) {
    return arg != null ? new Broker(arg) : null;
  }

}
