/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.plugin;

import io.holoinsight.server.home.biz.plugin.model.ChainPlugin;
import io.holoinsight.server.home.biz.plugin.model.PluginContext;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.biz.plugin.model.PluginType;
import io.holoinsight.server.home.biz.plugin.model.ScheduleTimeEnum;
import io.holoinsight.server.home.biz.plugin.model.WaitPlugin;
import io.holoinsight.server.home.common.exception.HoloinsightAlertIllegalArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 通知流程
 *
 * @author masaimu
 * @version 2022-10-28 17:32:00
 */
@Component
@PluginModel(name = "io.holoinsight.plugin.NotifyChain", version = "1")
public class NotifyChain extends ChainPlugin implements Runnable {
  private static final Logger LOGGER = LoggerFactory.getLogger(NotifyChain.class);

  public List<ChainPlugin> chains;
  // next executable plugin in chains
  public int index;
  private String traceId;
  private PluginScheduleQueue scheduleQueue;
  private Object input;
  private PluginContext context;

  public NotifyChain(PluginScheduleQueue queue) {
    this.scheduleQueue = queue;
  }

  public void schedule() throws Exception {
    if (CollectionUtils.isEmpty(chains)) {
      return;
    }

    if (index >= this.chains.size()) {
      return;
    }
    int start = index;
    for (int i = start; i < chains.size(); i++) {
      try {
        ChainPlugin plugin = this.chains.get(i);
        boolean success = plugin.input(this.input, this.context);
        if (!success) {
          break;
        }
        if (plugin instanceof WaitPlugin) {
          // 遇到 wait 插件，放入调度队列，完成本次调度
          if (this.scheduleQueue == null) {
            continue;
          }
          this.scheduleQueue.addQueue((ScheduleTimeEnum) plugin.output(), this);
          break;
        }
        plugin.handle();
        this.input = plugin.output();
      } finally {
        index++;
      }
    }
  }

  public String getTraceId() {
    return traceId;
  }

  public void setTraceId(String traceId) {
    this.traceId = traceId;
  }

  @Override
  public boolean input(Object input, PluginContext context) {
    this.input = input;
    this.context = context;
    return true;
  }

  @Override
  public Object output() {
    return null;
  }

  @Override
  public void handle() throws Exception {
    schedule();
  }

  @Override
  public void run() {
    try {
      handle();
    } catch (HoloinsightAlertIllegalArgumentException e) {
      LOGGER.error(
          "[HoloinsightAlertIllegalArgumentException][1] {} fail to handle notify chain for {}",
          this.traceId, e.getMessage(), e);
    } catch (Throwable e) {
      LOGGER.error("[HoloinsightAlertInternalException][1] {} fail to handle notify chain for {}",
          this.traceId, e.getMessage(), e);
    } finally {
      if (context != null && context.latch != null) {
        context.latch.countDown();
      }
    }
  }

  @Override
  public PluginType getPluginType() {
    return PluginType.notification;
  }
}
