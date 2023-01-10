/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import io.holoinsight.server.home.biz.plugin.PluginScheduleQueue;
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

  public static final String SMS_NOTIFY = "sms";
  public static final String EMAIL_NOTIFY = "email";
  public static final String DD_ROBOT_NOTIFY = "dingding_robot";
  public static final String PHONE_NOTIFY = "phone";
  public static final String WEBHOOK_NOTIFY = "webhook";

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

  public void schedule() {
    try {
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
          plugin.input(this.input, this.context);
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
    } catch (Exception e) {
      LOGGER.error("fail to schedule notify chain {} index {} for {}", getTraceId(), index,
          e.getMessage(), e);
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
    } catch (Exception e) {
      LOGGER.error("fail to run schedule chain {} for {}", getTraceId(), e.getMessage(), e);
    }
  }

  @Override
  public PluginType getPluginType() {
    return PluginType.notification;
  }
}
