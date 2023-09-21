/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.trace;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.holoinsight.server.common.Const;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * AgentConfigurationScheduler class.
 * </p>
 *
 * @author sw1136562366
 */
public class TraceAgentConfigurationScheduler {

  private static final Logger LOGGER =
      LoggerFactory.getLogger(TraceAgentConfigurationScheduler.class);
  @Autowired
  private TraceAgentConfigurationService traceAgentConfigurationService;

  public Cache<String, TraceAgentConfiguration> cache;

  /**
   * <p>
   * init.
   * </p>
   */
  @PostConstruct
  public void init() {
    cache = CacheBuilder.newBuilder().initialCapacity(10000).expireAfterWrite(50, TimeUnit.SECONDS)
        .maximumSize(1_000_000L).build();

    updateCache();
  }



  @Scheduled(cron = "*/30 * * * * *")
  private void updateCache() {
    try {
      List<TraceAgentConfiguration> allFromDB = traceAgentConfigurationService.getALLFromDB();
      cache.cleanUp();
      for (TraceAgentConfiguration agentConfiguration : allFromDB) {
        cache.put(agentConfiguration.getCacheKey(), agentConfiguration);
      }
    } catch (Exception e) {
      LOGGER.error("Update agent configuration error: ", e.getMessage());
    }
  }

  /**
   * <p>
   * getValue.
   * </p>
   */
  public TraceAgentConfiguration getValue(String cacheKey) {
    return cache.getIfPresent(cacheKey);
  }

  public TraceAgentConfiguration getValue(String tenant, String service,
      Map<String, String> extendInfo) {
    TraceAgentConfiguration configuration =
        this.getValue(this.cacheKey(tenant, service, extendInfo));
    // If the service configuration is empty, get the tenant configuration
    if (configuration == null) {
      configuration = this.getValue(this.cacheKey(tenant, "*", extendInfo));
    }
    // If the tenant configuration is empty, get the global configuration
    if (configuration == null) {
      configuration = this.getValue(this.cacheKey("*", "*", Collections.emptyMap()));
    }
    return configuration;
  }

  public String cacheKey(String tenant, String service, Map<String, String> extendInfo) {

    List<String> cacheKeys = subCacheKeys(tenant, service, extendInfo);
    String agentType = extendInfo.getOrDefault("type", "skywalking");
    cacheKeys.add(agentType);
    String language = extendInfo.getOrDefault("language", "java");
    cacheKeys.add(language);

    return StringUtils.join(cacheKeys, Const.TRACE_AGENT_CONFIG_KEY_JOINER);
  }

  public List<String> subCacheKeys(String tenant, String service, Map<String, String> extendInfo) {
    return new ArrayList() {
      {
        add(tenant);
        add(service);
      }
    };
  }

  /**
   * <p>
   * Getter for the field <code>cache</code>.
   * </p>
   */
  public Cache<String, TraceAgentConfiguration> getCache() {
    return cache;
  }
}
