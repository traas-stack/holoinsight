/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.controller;

import io.holoinsight.server.gateway.core.trace.config.AgentConfiguration;
import io.holoinsight.server.gateway.core.trace.scheduler.AgentConfigurationScheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 * AgentConfigurationController class.
 * </p>
 *
 * @author sw1136562366
 */
@ResponseBody
@RequestMapping({"/api/agent/configuration", "/internal/api/gateway/agent/configuration"})
public class AgentConfigurationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(AgentConfigurationController.class);

  @Autowired
  private AgentConfigurationScheduler agentConfigurationScheduler;

  /**
   * <p>
   * getAgentConfiguration.
   * </p>
   */
  @GetMapping("/query")
  public AgentConfiguration getAgentConfiguration(@RequestParam String cacheKey) {
    try {
      return agentConfigurationScheduler.getValue(cacheKey);
    } catch (Exception e) {
      LOGGER.error("Query agent configuration error: ", e.getMessage());
    }

    return null;
  }

}
