/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web;

import io.holoinsight.server.apm.core.ModelCenter;
import io.holoinsight.server.apm.core.ttl.ModelTtlManager;
import io.holoinsight.server.apm.receiver.trace.TraceOtelServiceImpl;
import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.common.springboot.ConditionalOnRole;
import io.holoinsight.server.common.threadpool.ThreadPoolConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 * created at 2022/11/28
 *
 * @author jiwliu
 */
@Configuration
@ConditionalOnRole("apm")
@ConditionalOnFeature("trace")
@ComponentScan(basePackages = {"io.holoinsight.server.apm"})
@EnableScheduling
@Import(ThreadPoolConfiguration.class)
public class ApmConfiguration {

  /**
   *
   * when storage and gateway are deployed together, report directly through local calls instead of
   * through grpc-server
   *
   * @return
   */
  @Bean
  @ConditionalOnProperty(value = "holoinsight.storage.grpcserver.enabled", havingValue = "true",
      matchIfMissing = true)
  public ApmGrpcServer storageGrpcServer() {
    return new ApmGrpcServer();
  }

  @Bean
  public TraceOtelServiceImpl storage_traceOtelServiceImpl() {
    return new TraceOtelServiceImpl();
  }

  @Bean
  public ModelCenter modelCenter() {
    return new ModelCenter();
  }

  @Bean
  public ModelTtlManager modelTtlManager() {
    return new ModelTtlManager();
  }


}
