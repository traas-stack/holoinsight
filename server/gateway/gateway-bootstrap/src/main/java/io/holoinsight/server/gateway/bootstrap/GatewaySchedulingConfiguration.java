/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.bootstrap;

import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.common.threadpool.ThreadPoolConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

/**
 * <p>
 * created at 2022/3/4
 *
 * @author sw1136562366
 */
@EnableScheduling
@Configuration
@Import(ThreadPoolConfiguration.class)
public class GatewaySchedulingConfiguration {
  /**
   * Spring 会使用这个 scheduler 去执行 @Scheduled 注解的任务
   *
   * @param commonThreadPools
   * @return
   */
  @Bean
  public TaskScheduler taskScheduler(CommonThreadPools commonThreadPools) {
    return new ConcurrentTaskScheduler(commonThreadPools.getScheduler());
  }
}
