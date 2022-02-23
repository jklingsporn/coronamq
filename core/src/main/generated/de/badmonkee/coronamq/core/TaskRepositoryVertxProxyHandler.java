/*
* Copyright 2014 Red Hat, Inc.
*
* Red Hat licenses this file to you under the Apache License, version 2.0
* (the "License"); you may not use this file except in compliance with the
* License. You may obtain a copy of the License at:
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/

package de.badmonkee.coronamq.core;

import io.vertx.core.Vertx;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHandler;
import io.vertx.serviceproxy.ServiceException;
import io.vertx.serviceproxy.ServiceExceptionMessageCodec;
import io.vertx.serviceproxy.HelperUtils;
/*
  Generated Proxy code - DO NOT EDIT
  @author Roger the Robot
*/

@SuppressWarnings({"unchecked", "rawtypes"})
public class TaskRepositoryVertxProxyHandler extends ProxyHandler {

  public static final long DEFAULT_CONNECTION_TIMEOUT = 5 * 60; // 5 minutes 
  private final Vertx vertx;
  private final TaskRepository service;
  private final long timerID;
  private long lastAccessed;
  private final long timeoutSeconds;
  private final boolean includeDebugInfo;

  public TaskRepositoryVertxProxyHandler(Vertx vertx, TaskRepository service){
    this(vertx, service, DEFAULT_CONNECTION_TIMEOUT);
  }

  public TaskRepositoryVertxProxyHandler(Vertx vertx, TaskRepository service, long timeoutInSecond){
    this(vertx, service, true, timeoutInSecond);
  }

  public TaskRepositoryVertxProxyHandler(Vertx vertx, TaskRepository service, boolean topLevel, long timeoutInSecond){
    this(vertx, service, true, timeoutInSecond, false);
  }

  public TaskRepositoryVertxProxyHandler(Vertx vertx, TaskRepository service, boolean topLevel, long timeoutSeconds, boolean includeDebugInfo) {
      this.vertx = vertx;
      this.service = service;
      this.includeDebugInfo = includeDebugInfo;
      this.timeoutSeconds = timeoutSeconds;
      try {
        this.vertx.eventBus().registerDefaultCodec(ServiceException.class,
            new ServiceExceptionMessageCodec());
      } catch (IllegalStateException ex) {}
      if (timeoutSeconds != -1 && !topLevel) {
        long period = timeoutSeconds * 1000 / 2;
        if (period > 10000) {
          period = 10000;
        }
        this.timerID = vertx.setPeriodic(period, this::checkTimedOut);
      } else {
        this.timerID = -1;
      }
      accessed();
    }


  private void checkTimedOut(long id) {
    long now = System.nanoTime();
    if (now - lastAccessed > timeoutSeconds * 1000000000) {
      close();
    }
  }

    @Override
    public void close() {
      if (timerID != -1) {
        vertx.cancelTimer(timerID);
      }
      super.close();
    }

    private void accessed() {
      this.lastAccessed = System.nanoTime();
    }

  public void handle(Message<JsonObject> msg) {
    try{
      JsonObject json = msg.body();
      String action = msg.headers().get("action");
      if (action == null) throw new IllegalStateException("action not specified");
      accessed();
      switch (action) {
        case "createTask": {
          service.createTask((java.lang.String)json.getValue("label"),
                        (io.vertx.core.json.JsonObject)json.getValue("payload")).onComplete(HelperUtils.createHandler(msg, includeDebugInfo));
          break;
        }
        case "failTask": {
          service.failTask((java.lang.String)json.getValue("id"),
                        (java.lang.String)json.getValue("reason")).onComplete(HelperUtils.createHandler(msg, includeDebugInfo));
          break;
        }
        case "updateTask": {
          service.updateTask((java.lang.String)json.getValue("id"),
                        json.getString("newStatus") == null ? null : de.badmonkee.coronamq.core.TaskStatus.valueOf(json.getString("newStatus")),
                        json.getString("oldStatus") == null ? null : de.badmonkee.coronamq.core.TaskStatus.valueOf(json.getString("oldStatus"))).onComplete(HelperUtils.createHandler(msg, includeDebugInfo));
          break;
        }
        case "requestTask": {
          service.requestTask((java.lang.String)json.getValue("label")).onComplete(HelperUtils.createHandler(msg, includeDebugInfo));
          break;
        }
        case "getTask": {
          service.getTask((java.lang.String)json.getValue("id")).onComplete(HelperUtils.createHandler(msg, includeDebugInfo));
          break;
        }
        case "countTasks": {
          service.countTasks((java.lang.String)json.getValue("label")).onComplete(HelperUtils.createHandler(msg, includeDebugInfo));
          break;
        }
        case "deleteTask": {
          service.deleteTask((java.lang.String)json.getValue("id")).onComplete(HelperUtils.createHandler(msg, includeDebugInfo));
          break;
        }
        case "start": {
          service.start().onComplete(HelperUtils.createHandler(msg, includeDebugInfo));
          break;
        }
        case "stop": {
          service.stop().onComplete(HelperUtils.createHandler(msg, includeDebugInfo));
          break;
        }
        default: throw new IllegalStateException("Invalid action: " + action);
      }
    } catch (Throwable t) {
      if (includeDebugInfo) msg.reply(new ServiceException(500, t.getMessage(), HelperUtils.generateDebugInfo(t)));
      else msg.reply(new ServiceException(500, t.getMessage()));
      throw t;
    }
  }
}