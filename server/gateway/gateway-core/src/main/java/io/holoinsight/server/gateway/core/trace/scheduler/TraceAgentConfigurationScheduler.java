/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.scheduler;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.holoinsight.server.gateway.core.trace.config.TraceAgentConfiguration;
import io.holoinsight.server.gateway.core.trace.config.TraceAgentConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.util.List;
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

  /**
   * <p>
   * Getter for the field <code>cache</code>.
   * </p>
   */
  public Cache<String, TraceAgentConfiguration> getCache() {
    return cache;
  }
}
