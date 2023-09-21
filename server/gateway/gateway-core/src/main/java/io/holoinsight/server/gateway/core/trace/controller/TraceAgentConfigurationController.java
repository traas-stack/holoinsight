/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.holoinsight.server.common.trace.TraceAgentConfiguration;
import io.holoinsight.server.common.trace.TraceAgentConfigurationScheduler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * AgentConfigurationController class.
 * </p>
 *
 * @author sw1136562366
 */

@RequestMapping({"/api/agent/configuration", "/internal/api/gateway/agent/configuration"})
public class TraceAgentConfigurationController {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TraceAgentConfigurationController.class);

  @Autowired
  private TraceAgentConfigurationScheduler agentConfigurationScheduler;

  /**
   * <p>
   * getAgentConfiguration.
   * </p>
   */
  @PostMapping("/query")
  @ResponseBody
  public TraceAgentConfiguration getAgentConfiguration(@RequestBody Map<String, String> request) {
    String tenant = request.get("tenant");
    String service = request.get("service");
    Map<String, String> extendMap = new HashMap<>();
    try {
      if (!StringUtils.isEmpty(request.get("extendInfo"))) {
        extendMap = new ObjectMapper().readValue(request.get("extendInfo"), HashMap.class);
      }
      return agentConfigurationScheduler.getValue(tenant, service, extendMap);
    } catch (Exception e) {
      LOGGER.error(String.format("Query trace agent configuration error, request: %s,", extendMap),
          e.getMessage());
    }
    return null;
  }

}
