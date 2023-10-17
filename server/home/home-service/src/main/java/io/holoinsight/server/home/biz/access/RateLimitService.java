/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.access;

import io.holoinsight.server.home.biz.access.model.AccessLimitedException;
import io.holoinsight.server.home.biz.access.model.MonitorAccessConfig;
import io.holoinsight.server.home.biz.access.model.RateLimitedException;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author jsy1001de
 * @version 1.0: RateLimitService.java, v 0.1 2022年06月08日 4:46 下午 jinsong.yjs Exp $
 */
@Service
public class RateLimitService {

  @Autowired
  private AccessConfigService accessConfigService;

  private static final ConcurrentHashMap<String, RateLimiter> LIMITERS = new ConcurrentHashMap<>();

  /**
   * 先判访问控制， 再判黑，再限流
   * 
   * @param accessKey
   */
  public void acquire(String accessKey, String metric) {
    // 获取配置
    final MonitorAccessConfig accessConfig =
        accessConfigService.getAccessConfigDOMap().get(accessKey);

    // 判访问控制
    if (!accessConfig.isAccessAll()) {
      if (!checkMetricRange(metric, accessConfig.getAccessRange())) {
        throw new AccessLimitedException("no access to " + metric + " [" + accessKey + "]");
      }
    }

    // AccessID维度是否黑名单
    if (accessConfig.getMetricQps() == 0) {
      throw new RateLimitedException("accessId in blacklist, " + accessConfig.getAccessId());
    }

    // AccessID维度是否限流
    if (accessConfig.getMetricQps() > 0) {
      final long rate = Math.max(5, accessConfig.getMetricQps());
      RateLimiter rateLimiter = LIMITERS.computeIfAbsent(accessKey, s -> RateLimiter.create(rate));

      // not equal
      if (Math.abs(rateLimiter.getRate() - rate) > 1) {
        rateLimiter = RateLimiter.create(rate);
        LIMITERS.put(accessKey, rateLimiter);
      }

      final boolean succ = rateLimiter.tryAcquire();

      if (!succ) {
        throw new RateLimitedException("accessId rate limited, " + accessConfig.getAccessId());
      }
    }

  }

  private Boolean checkMetricRange(String metricName, Set<String> metricRanges) {
    if (CollectionUtils.isEmpty(metricRanges))
      return Boolean.TRUE;
    if (metricRanges.contains(metricName))
      return Boolean.TRUE;
    for (String metric : metricRanges) {
      if (metricName.startsWith(metric))
        return Boolean.TRUE;
    }

    return Boolean.FALSE;
  }
}
