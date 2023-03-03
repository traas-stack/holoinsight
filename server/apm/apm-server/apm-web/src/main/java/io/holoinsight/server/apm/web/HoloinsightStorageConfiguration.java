/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.common.springboot.ConditionalOnRole;
import io.holoinsight.server.common.threadpool.ThreadPoolConfiguration;
import io.holoinsight.server.apm.core.ModelCenter;
import io.holoinsight.server.apm.core.installer.ModelInstallManager;
import io.holoinsight.server.apm.core.ttl.ModelTtlManager;
import io.holoinsight.server.apm.receiver.trace.TraceOtelServiceImpl;
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
@ConditionalOnRole("apm")
@ComponentScan(basePackages = {"io.holoinsight.server.apm"})
@EnableScheduling
@Import(ThreadPoolConfiguration.class)
public class HoloinsightStorageConfiguration {

  @Configuration
  @ConditionalOnFeature("trace")
  public static class ApmTraceConfiguration {

    /**
     * 当 storage 和 gateway 一起部署时, storage 的 grpc-server 就没用了, 直接走本地调用
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

    @Bean
    public ModelInstallManager modelInstallManager() {
      return new ModelInstallManager();
    }


  }
}
