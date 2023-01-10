/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.biz.access;

import io.holoinsight.server.home.biz.access.model.AccessLimitedException;
import io.holoinsight.server.home.biz.access.model.MonitorAccessConfig;
import io.holoinsight.server.home.biz.access.model.MonitorTokenData;
import io.holoinsight.server.home.biz.access.model.RateLimitedException;
import com.google.common.util.concurrent.RateLimiter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
     * @param tokenData
     */
    public void acquire(MonitorTokenData tokenData, String metric) {
        final String accessId = tokenData.accessId;
        // 获取配置
        final MonitorAccessConfig accessConfig = accessConfigService.getAccessConfigDOMap().get(accessId);

        // 判访问控制
        if (accessConfig.isAccessAll()) {
            if (!accessConfig.getAccessRange().contains(metric)) {
                throw new AccessLimitedException(
                    "no access to " + metric + " [" + tokenData + "]");
            }
        }

        // AccessID维度是否黑名单
        if (accessConfig.getMetricQps() == 0) {
            throw new RateLimitedException("accessId in blacklist, " + tokenData);
        }


        // AccessID维度是否限流
        if (accessConfig.getMetricQps() > 0) {
            final long rate = Math.max(5, accessConfig.getMetricQps());
            RateLimiter rateLimiter = LIMITERS.computeIfAbsent(accessId, s -> RateLimiter.create(rate));

            // not equal
            if (Math.abs(rateLimiter.getRate() - rate) > 1) {
                rateLimiter = RateLimiter.create(rate);
                LIMITERS.put(accessId, rateLimiter);
            }

            final boolean succ = rateLimiter.tryAcquire();

            if (!succ) {
                throw new RateLimitedException("accessId rate limited, " + tokenData);
            }
        }

    }
}