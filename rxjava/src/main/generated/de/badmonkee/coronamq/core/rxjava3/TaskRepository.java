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

package de.badmonkee.coronamq.core.rxjava3;

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
 * Necessary actions to operate with the task queue.
 *
 * <p/>
 * NOTE: This class has been automatically generated from the {@link de.badmonkee.coronamq.core.TaskRepository original} non RX-ified interface using Vert.x codegen.
 */

@RxGen(de.badmonkee.coronamq.core.TaskRepository.class)
public class TaskRepository {

  @Override
  public String toString() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TaskRepository that = (TaskRepository) o;
    return delegate.equals(that.delegate);
  }
  
  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  public static final TypeArg<TaskRepository> __TYPE_ARG = new TypeArg<>(    obj -> new TaskRepository((de.badmonkee.coronamq.core.TaskRepository) obj),
    TaskRepository::getDelegate
  );

  private final de.badmonkee.coronamq.core.TaskRepository delegate;
  
  public TaskRepository(de.badmonkee.coronamq.core.TaskRepository delegate) {
    this.delegate = delegate;
  }

  public TaskRepository(Object delegate) {
    this.delegate = (de.badmonkee.coronamq.core.TaskRepository)delegate;
  }

  public de.badmonkee.coronamq.core.TaskRepository getDelegate() {
    return delegate;
  }


  public static de.badmonkee.coronamq.core.rxjava3.TaskRepository createProxy(io.vertx.rxjava3.core.Vertx vertx, java.lang.String address) { 
    de.badmonkee.coronamq.core.rxjava3.TaskRepository ret = de.badmonkee.coronamq.core.rxjava3.TaskRepository.newInstance((de.badmonkee.coronamq.core.TaskRepository)de.badmonkee.coronamq.core.TaskRepository.createProxy(vertx.getDelegate(), address));
    return ret;
  }

  /**
   * Create a new task in the queue after a task has been dispatched.
   * @param label the label
   * @param payload the payload
   * @return  that is completed when the task is added to the queue containing the id associated with the task.
   */
  public io.reactivex.rxjava3.core.Single<java.lang.String> createTask(java.lang.String label, io.vertx.core.json.JsonObject payload) { 
    io.reactivex.rxjava3.core.Single<java.lang.String> ret = rxCreateTask(label, payload);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * Create a new task in the queue after a task has been dispatched.
   * @param label the label
   * @param payload the payload
   * @return  that is completed when the task is added to the queue containing the id associated with the task.
   */
  public io.reactivex.rxjava3.core.Single<java.lang.String> rxCreateTask(java.lang.String label, io.vertx.core.json.JsonObject payload) { 
    return AsyncResultSingle.toSingle(delegate.createTask(label, payload), __value -> __value);
  }

  /**
   * Fails a running task.
   * @param id the unique id of the task
   * @param reason the reason why the task failed
   * @return a  containing the task's update result.
   */
  public io.reactivex.rxjava3.core.Completable failTask(java.lang.String id, java.lang.String reason) { 
    io.reactivex.rxjava3.core.Completable ret = rxFailTask(id, reason);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.CompletableHelper.nullObserver());
    return ret;
  }

  /**
   * Fails a running task.
   * @param id the unique id of the task
   * @param reason the reason why the task failed
   * @return a  containing the task's update result.
   */
  public io.reactivex.rxjava3.core.Completable rxFailTask(java.lang.String id, java.lang.String reason) { 
    return AsyncResultCompletable.toCompletable(delegate.failTask(id, reason));
  }

  /**
   * Update the status of a task based on it's id and current status.
   * @param id the unique id of the task
   * @param newStatus the new status of the task
   * @param oldStatus the expected old status
   * @return a succeeded  if the task could be updated or a failed  otherwise.
   */
  public io.reactivex.rxjava3.core.Completable updateTask(java.lang.String id, de.badmonkee.coronamq.core.TaskStatus newStatus, de.badmonkee.coronamq.core.TaskStatus oldStatus) { 
    io.reactivex.rxjava3.core.Completable ret = rxUpdateTask(id, newStatus, oldStatus);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.CompletableHelper.nullObserver());
    return ret;
  }

  /**
   * Update the status of a task based on it's id and current status.
   * @param id the unique id of the task
   * @param newStatus the new status of the task
   * @param oldStatus the expected old status
   * @return a succeeded  if the task could be updated or a failed  otherwise.
   */
  public io.reactivex.rxjava3.core.Completable rxUpdateTask(java.lang.String id, de.badmonkee.coronamq.core.TaskStatus newStatus, de.badmonkee.coronamq.core.TaskStatus oldStatus) { 
    return AsyncResultCompletable.toCompletable(delegate.updateTask(id, newStatus, oldStatus));
  }

  /**
   * Atomically requests a new task from the queue.
   * @param label the label
   * @return a  containing a new task or null if there is no task in the queue.
   */
  public io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> requestTask(java.lang.String label) { 
    io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> ret = rxRequestTask(label);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * Atomically requests a new task from the queue.
   * @param label the label
   * @return a  containing a new task or null if there is no task in the queue.
   */
  public io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> rxRequestTask(java.lang.String label) { 
    return AsyncResultSingle.toSingle(delegate.requestTask(label), __value -> __value);
  }

  /**
   * @param id the id.
   * @return the task with the given id. If the task does not exist, an exception is raised.
   */
  public io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> getTask(java.lang.String id) { 
    io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> ret = rxGetTask(id);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * @param id the id.
   * @return the task with the given id. If the task does not exist, an exception is raised.
   */
  public io.reactivex.rxjava3.core.Single<io.vertx.core.json.JsonObject> rxGetTask(java.lang.String id) { 
    return AsyncResultSingle.toSingle(delegate.getTask(id), __value -> __value);
  }

  /**
   * @param label the label
   * @return the amount of existing tasks.
   */
  public io.reactivex.rxjava3.core.Single<java.lang.Long> countTasks(java.lang.String label) { 
    io.reactivex.rxjava3.core.Single<java.lang.Long> ret = rxCountTasks(label);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * @param label the label
   * @return the amount of existing tasks.
   */
  public io.reactivex.rxjava3.core.Single<java.lang.Long> rxCountTasks(java.lang.String label) { 
    return AsyncResultSingle.toSingle(delegate.countTasks(label), __value -> __value);
  }

  /**
   * @param id the id.
   * @return a  that is completed when the task has been deleted.
   */
  public io.reactivex.rxjava3.core.Single<java.lang.Integer> deleteTask(java.lang.String id) { 
    io.reactivex.rxjava3.core.Single<java.lang.Integer> ret = rxDeleteTask(id);
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.SingleHelper.nullObserver());
    return ret;
  }

  /**
   * @param id the id.
   * @return a  that is completed when the task has been deleted.
   */
  public io.reactivex.rxjava3.core.Single<java.lang.Integer> rxDeleteTask(java.lang.String id) { 
    return AsyncResultSingle.toSingle(delegate.deleteTask(id), __value -> __value);
  }

  /**
   * Asynchronously starts this repository.
   * @return a  containing the status of the start operation.
   */
  public io.reactivex.rxjava3.core.Completable start() { 
    io.reactivex.rxjava3.core.Completable ret = rxStart();
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.CompletableHelper.nullObserver());
    return ret;
  }

  /**
   * Asynchronously starts this repository.
   * @return a  containing the status of the start operation.
   */
  public io.reactivex.rxjava3.core.Completable rxStart() { 
    return AsyncResultCompletable.toCompletable(delegate.start());
  }

  /**
   * Asynchronously stops this repository.
   * @return a  containing the status of the stop operation.
   */
  public io.reactivex.rxjava3.core.Completable stop() { 
    io.reactivex.rxjava3.core.Completable ret = rxStop();
    ret = ret.cache();
    ret.subscribe(io.vertx.rxjava3.CompletableHelper.nullObserver());
    return ret;
  }

  /**
   * Asynchronously stops this repository.
   * @return a  containing the status of the stop operation.
   */
  public io.reactivex.rxjava3.core.Completable rxStop() { 
    return AsyncResultCompletable.toCompletable(delegate.stop());
  }

  public static TaskRepository newInstance(de.badmonkee.coronamq.core.TaskRepository arg) {
    return arg != null ? new TaskRepository(arg) : null;
  }

}
