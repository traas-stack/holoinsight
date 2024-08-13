/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.bootstrap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import io.holoinsight.server.apm.core.ApmConfig;
import io.holoinsight.server.apm.core.installer.ModelInstallManager;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.CommonBuilder;
import io.holoinsight.server.apm.engine.elasticsearch.storage.impl.SpanMetricEsStorage;
import io.holoinsight.server.apm.engine.storage.SpanStorageHookManager;
import io.holoinsight.server.apm.receiver.analysis.RelationAnalysis;
import io.holoinsight.server.apm.receiver.analysis.ServiceErrorAnalysis;
import io.holoinsight.server.apm.receiver.analysis.SlowSqlAnalysis;
import io.holoinsight.server.apm.receiver.common.PublicAttr;
import io.holoinsight.server.apm.receiver.trace.SpanHandler;
import io.holoinsight.server.apm.server.service.impl.EndpointRelationServiceImpl;
import io.holoinsight.server.apm.server.service.impl.EndpointServiceImpl;
import io.holoinsight.server.apm.server.service.impl.EventServiceImpl;
import io.holoinsight.server.apm.server.service.impl.MetricServiceImpl;
import io.holoinsight.server.apm.server.service.impl.NetworkAddressMappingServiceImpl;
import io.holoinsight.server.apm.server.service.impl.ServiceErrorServiceImpl;
import io.holoinsight.server.apm.server.service.impl.ServiceInstanceRelationServiceImpl;
import io.holoinsight.server.apm.server.service.impl.ServiceInstanceServiceImpl;
import io.holoinsight.server.apm.server.service.impl.ServiceOverviewServiceImpl;
import io.holoinsight.server.apm.server.service.impl.ServiceRelationServiceImpl;
import io.holoinsight.server.apm.server.service.impl.SlowSqlServiceImpl;
import io.holoinsight.server.apm.server.service.impl.TopologyServiceImpl;
import io.holoinsight.server.apm.server.service.impl.TraceServiceImpl;
import io.holoinsight.server.apm.server.service.impl.VirtualComponentServiceImpl;
import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.common.springboot.ConditionalOnRole;

@ConditionalOnRole("apm")
@ConditionalOnFeature("trace")
@Configuration
public class HoloinsightApmConfiguration {

  @Bean("endpointRelationService")
  public EndpointRelationServiceImpl endpointRelationService() {
    return new EndpointRelationServiceImpl();
  }

  @Bean("endpointService")
  public EndpointServiceImpl endpointService() {
    return new EndpointServiceImpl();
  }

  @Bean("metricService")
  public MetricServiceImpl metricService() {
    return new MetricServiceImpl();
  }

  @Bean("networkAddressMappingService")
  public NetworkAddressMappingServiceImpl networkAddressMappingService() {
    return new NetworkAddressMappingServiceImpl();
  }

  @Bean("serviceErrorService")
  public ServiceErrorServiceImpl serviceErrorService() {
    return new ServiceErrorServiceImpl();
  }

  @Bean("serviceInstanceRelationService")
  public ServiceInstanceRelationServiceImpl serviceInstanceRelationService() {
    return new ServiceInstanceRelationServiceImpl();
  }

  @Bean("serviceInstanceService")
  public ServiceInstanceServiceImpl serviceInstanceService() {
    return new ServiceInstanceServiceImpl();
  }

  @Bean("serviceOverviewService")
  public ServiceOverviewServiceImpl serviceOverviewService() {
    return new ServiceOverviewServiceImpl();
  }

  @Bean("serviceRelationService")
  public ServiceRelationServiceImpl serviceRelationService() {
    return new ServiceRelationServiceImpl();
  }

  @Bean("slowSqlService")
  public SlowSqlServiceImpl slowSqlService() {
    return new SlowSqlServiceImpl();
  }

  @Bean("topologyService")
  public TopologyServiceImpl topologyService() {
    return new TopologyServiceImpl();
  }

  @Bean("traceService")
  public TraceServiceImpl traceService() {
    return new TraceServiceImpl();
  }

  @Bean("virtualComponentService")
  public VirtualComponentServiceImpl virtualComponentService() {
    return new VirtualComponentServiceImpl();
  }

  @Bean("modelInstallManager")
  public ModelInstallManager modelInstallManager() {
    return new ModelInstallManager();
  }

  @Bean("commonBuilder")
  public CommonBuilder commonBuilder() {
    return new CommonBuilder();
  }

  @Bean("publicAttr")
  public PublicAttr publicAttr() {
    return new PublicAttr();
  }

  @Bean
  public ApmConfig apmConfig() {
    return new ApmConfig();
  }

  @Bean
  public SpanStorageHookManager spanStorageHookManager() {
    return new SpanStorageHookManager();
  }

  @Bean("spanHandler")
  @Lazy
  public SpanHandler spanHandler() throws InterruptedException {
    // the startup of the trace data reporting service needs to wait for the necessary
    // initialization conditions, such as the creation of the index template
    Thread.sleep(10000);
    return new SpanHandler();
  }

  @Bean("relationAnalysis")
  public RelationAnalysis relationAnalysis() {
    return new RelationAnalysis();
  }

  @Bean("errorAnalysis")
  public ServiceErrorAnalysis errorAnalysis() {
    return new ServiceErrorAnalysis();
  }

  @Bean("slowSqlAnalysis")
  public SlowSqlAnalysis slowSqlAnalysis() {
    return new SlowSqlAnalysis();
  }

  @Bean("eventService")
  public EventServiceImpl eventService() {
    return new EventServiceImpl();
  }

  @Bean("spanMetricEsStorage")
  @Primary
  public SpanMetricEsStorage spanMetricEsStorage() {
    return new SpanMetricEsStorage();
  }
}
