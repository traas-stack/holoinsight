/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.holoinsight.server.common.Const;
import io.holoinsight.server.gateway.core.trace.config.TraceAgentConfiguration;
import io.holoinsight.server.gateway.core.trace.scheduler.TraceAgentConfigurationScheduler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    HashMap<String, String> extendMap = null;
    try {
      if (request.get("extendInfo") != null) {
        extendMap = new ObjectMapper().readValue(request.get("extendInfo"), HashMap.class);
      }
      TraceAgentConfiguration configuration =
          agentConfigurationScheduler.getValue(cacheKey(tenant, service, extendMap));
      // If the service configuration is empty, get the tenant configuration
      if (configuration == null) {
        configuration = agentConfigurationScheduler.getValue(cacheKey(tenant, "*", extendMap));
      }
      // If the tenant configuration is empty, get the global configuration
      if (configuration == null) {
        configuration = agentConfigurationScheduler.getValue(cacheKey("*", "*", extendMap));
      }
      return configuration;
    } catch (Exception e) {
      LOGGER.error(String.format("Query trace agent configuration error, request: %s,", extendMap),
          e.getMessage());
    }
    return null;
  }

  public String cacheKey(String tenant, String service, Map<String, String> extendInfo) {
    List<String> cacheKeys = new ArrayList() {
      {
        add(cacheTenant(tenant, extendInfo));
        add(service);
      }
    };
    if (extendInfo != null) {
      String agentType = extendInfo.get("type");
      if (!StringUtils.isEmpty(agentType)) {
        cacheKeys.add(agentType);
      }
      String language = extendInfo.get("language");
      if (!StringUtils.isEmpty(language)) {
        cacheKeys.add(language);
      }
    }
    return StringUtils.join(cacheKeys, Const.TRACE_AGENT_CONFIG_KEY_JOINER);
  }

  public String cacheTenant(String tenant, Map<String, String> extendInfo) {
    return tenant;
  }

}
