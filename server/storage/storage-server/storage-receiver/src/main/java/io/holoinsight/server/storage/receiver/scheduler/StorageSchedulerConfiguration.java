/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.receiver.scheduler;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>created at 2022/11/30
 *
 * @author jiwliu
 */
@Configuration
@ConditionalOnFeature("trace")
public class StorageSchedulerConfiguration {
    @Bean
    public CacheUpdateScheduler cacheUpdateScheduler() {
        return new CacheUpdateScheduler();
    }
}
