/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import io.holoinsight.server.common.UpTime;
import io.holoinsight.server.common.threadpool.CommonThreadPools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.xzchaoo.commons.basic.dispose.Disposable;

/**
 * <p>
 * created at 2022/4/18
 *
 * @author zzhb101
 */
@Component
public class AgentMaintainTask {
  private static final Logger LOGGER = LoggerFactory.getLogger(AgentMaintainTask.class);
  @Autowired
  private AgentService agentService;
  @Autowired
  private AgentConfig agentConfig;
  @Autowired
  private CommonThreadPools commonThreadPools;
  private Disposable d;

  @PostConstruct
  /**
   * 能不能监听started事件?
   */
  public void start() {
    d = commonThreadPools.getDynamicScheduler().dynamic(this::execute,
        agentConfig.getMaintain().getInterval(), () -> agentConfig.getMaintain().getInterval());
  }

  @PreDestroy
  public void stop() {
    if (d != null) {
      d.dispose();
    }
  }

  private void execute() {
    // 刚启动的1小时内不做这个操作
    int seconds = agentConfig.getMaintain().getReadySeconds();
    if (UpTime.TIME + seconds * 1000L > System.currentTimeMillis()) {
      return;
    }

    try {
      agentService.markDeleteExpiredAgents();
      agentService.deleteDeletedAgents();
    } catch (Throwable e) {
      LOGGER.error("delete expired agents error", e);
    }
  }
}
