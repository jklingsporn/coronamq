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

import io.github.jklingsporn.coronamq.core.rxjava3.Broker;
import io.github.jklingsporn.coronamq.core.rxjava3.TaskRepository;
import io.github.jklingsporn.coronamq.core.rxjava3.Worker;
import io.vertx.rxjava3.impl.AsyncResultSingle;
import io.vertx.lang.rx.RxGen;
import io.vertx.lang.rx.TypeArg;

/**
 * It is advisable to start the Broker, Workers and repositorys in the correct order. Single node setups required at least
 * a repository and a broker while workers can be added before or after the queue has been "spread" (started).
 *
 * NOTE: This class has been automatically generated from the {@link io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap original} non RX-ified interface using Vert.x codegen.
 */

@RxGen(io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap.class)
public class Bootstrap {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Bootstrap that = (Bootstrap) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<Bootstrap> __TYPE_ARG = new TypeArg<>(    obj -> new Bootstrap((io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap) obj),
    Bootstrap::getDelegate
  );

  private final io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap delegate;
  
  public Bootstrap(io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap delegate) {
    this.delegate = delegate;
  }

  public Bootstrap(Object delegate) {
    this.delegate = (io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap)delegate;
  }

  public io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap getDelegate() {
    return delegate;
  }

  private static final TypeArg<BootstrapSpreadStep> TYPE_ARG_0 = new TypeArg<BootstrapSpreadStep>(o1 -> BootstrapSpreadStep.newInstance((io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep)o1), o1 -> o1.getDelegate());

  /**
   * Adds a <code>TaskRepository</code> with default configuration to this Bootstrap.
   * @return a reference to this
   */
  public Bootstrap withRepository() {
    delegate.withRepository();
    return this;
  }

  /**
   * Adds a <code>TaskRepository</code> to this Bootstrap.
   * @param repository the repository
   * @return a reference to this
   */
  public Bootstrap withRepository(TaskRepository repository) {
    delegate.withRepository(repository.getDelegate());
    return this;
  }

  /**
   * Adds a <code>Broker</code> with default configuration to this Bootstrap.
   * @return a reference to this
   */
  public Bootstrap withBroker() {
    delegate.withBroker();
    return this;
  }

  /**
   * Adds a <code>Broker</code> to this Bootstrap.
   * @param broker 
   * @return a reference to this
   */
  public Bootstrap withBroker(Broker broker) {
    delegate.withBroker(broker.getDelegate());
    return this;
  }

  /**
   * Adds a Worker to this bootstrap.
   * @param worker a worker
   * @return 
   */
  public Bootstrap withWorker(Worker worker) {
    delegate.withWorker(worker.getDelegate());
    return this;
  }

  /**
   * Registers and starts all services in the right order.
   * @return 
   */
  public io.reactivex.rxjava3.core.Single<BootstrapSpreadStep> spread() {
    io.reactivex.rxjava3.core.Single<BootstrapSpreadStep> ret = rxSpread();
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * Registers and starts all services in the right order.
   * @return 
   */
  public io.reactivex.rxjava3.core.Single<BootstrapSpreadStep> rxSpread() {
    return AsyncResultSingle.toSingle(delegate.spread(), __value -> BootstrapSpreadStep.newInstance((io.github.jklingsporn.coronamq.core.bootstrap.BootstrapSpreadStep)__value));
  }

  public static Bootstrap newInstance(io.github.jklingsporn.coronamq.core.bootstrap.Bootstrap arg) {
    return arg != null ? new Bootstrap(arg) : null;
  }

}
