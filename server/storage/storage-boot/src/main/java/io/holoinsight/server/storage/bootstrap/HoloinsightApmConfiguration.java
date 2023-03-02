/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.bootstrap;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.common.springboot.ConditionalOnRole;
import io.holoinsight.server.storage.server.service.impl.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnRole("storage")
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
}
