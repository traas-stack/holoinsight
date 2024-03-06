/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.bootstrap;

import io.holoinsight.server.common.config.EnvironmentProperties;
import io.holoinsight.server.common.ctl.ProductCtlService;
import io.holoinsight.server.common.ctl.ProductCtlServiceImpl;
import io.holoinsight.server.common.dao.CommonDaoConfiguration;
import io.holoinsight.server.common.service.AccessRecordService;
import io.holoinsight.server.common.service.CommonServiceAutoConfiguration;
import io.holoinsight.server.common.springboot.ConditionalOnRole;
import io.holoinsight.server.home.alert.plugin.AlertNotifyHandler;
import io.holoinsight.server.home.alert.plugin.DefaultAlertNotifyHandler;
import io.holoinsight.server.home.alert.plugin.DefaultGatewayService;
import io.holoinsight.server.home.alert.plugin.GatewayService;
import io.holoinsight.server.home.alert.service.event.AlertNotifyChainBuilder;
import io.holoinsight.server.home.alert.service.event.DefaultAlertNotifyChainBuilder;
import io.holoinsight.server.home.biz.plugin.DefaultMarketplaceProductHandler;
import io.holoinsight.server.home.biz.plugin.MarketplaceProductHandler;
import io.holoinsight.server.home.biz.plugin.MetricInfoCheckService;
import io.holoinsight.server.home.biz.plugin.MetricInfoCheckServiceImpl;
import io.holoinsight.server.home.biz.service.AlertRuleService;
import io.holoinsight.server.home.biz.service.EnvironmentService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.biz.service.UserinfoVerificationService;
import io.holoinsight.server.home.biz.service.impl.AlarmHistoryServiceImpl;
import io.holoinsight.server.home.biz.service.impl.AlertRuleServiceImpl;
import io.holoinsight.server.home.biz.service.impl.DefaultEnvironmentServiceImpl;
import io.holoinsight.server.home.biz.service.impl.DefaultTenantInitServiceImpl;
import io.holoinsight.server.home.biz.service.impl.UserinfoVerificationServiceImpl;
import io.holoinsight.server.home.common.service.RequestContextAdapter;
import io.holoinsight.server.home.common.service.RequestContextAdapterImpl;
import io.holoinsight.server.home.facade.utils.ApiSecurityService;
import io.holoinsight.server.home.web.controller.TraceAgentFacadeImpl;
import io.holoinsight.server.home.web.security.ApiSecurityServiceImpl;
import io.holoinsight.server.home.web.security.ParameterSecurityService;
import io.holoinsight.server.home.web.security.ParameterSecurityServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author masaimu
 * @version 2022-12-08 10:39:00
 */
@ComponentScan(basePackages = {"io.holoinsight.server.home"})
@EnableTransactionManagement
@EnableScheduling
@EntityScan(basePackages = "io.holoinsight.server.home.dal.model")
@MapperScan("io.holoinsight.server.home.dal.mapper")
@ConditionalOnRole("home")
@EnableConfigurationProperties({EnvironmentProperties.class})
@Import({CommonDaoConfiguration.class, CommonServiceAutoConfiguration.class})
public class HoloinsightHomeConfiguration {
  @Bean
  public TenantInitService tenantInitService() {
    return new DefaultTenantInitServiceImpl();
  }

  @Bean
  public MarketplaceProductHandler marketplaceProductHandler() {
    return new DefaultMarketplaceProductHandler();
  }

  @Bean
  public EnvironmentService environmentService() {
    return new DefaultEnvironmentServiceImpl();
  }

  @Bean
  public AlertNotifyHandler alertNotifyHandler() {
    return new DefaultAlertNotifyHandler();
  }

  @Bean
  public GatewayService gatewayService() {
    return new DefaultGatewayService();
  }

  @Bean
  public AlertNotifyChainBuilder alertNotifyChainBuilder() {
    return new DefaultAlertNotifyChainBuilder();
  }

  @Bean
  public UserinfoVerificationService userinfoVerificationService() {
    return new UserinfoVerificationServiceImpl();
  }

  @Bean
  public RequestContextAdapter requestContextAdapter() {
    return new RequestContextAdapterImpl();
  }

  @Bean
  public TraceAgentFacadeImpl traceAgentFacadeImpl() {
    return new TraceAgentFacadeImpl();
  }

  @Bean
  public MetricInfoCheckService metricInfoCheckService() {
    return new MetricInfoCheckServiceImpl();
  }

  @Bean
  public ProductCtlService productCtlService() {
    return new ProductCtlServiceImpl();
  }

  @Bean
  public AlertRuleService alertRuleService() {
    return new AlertRuleServiceImpl();
  }

  @Bean
  public ParameterSecurityService alertSecurityService() {
    return new ParameterSecurityServiceImpl();
  }

  @Bean
  public AccessRecordService accessRecordService() {
    return new AccessRecordService();
  }

  @Bean
  public ApiSecurityService apiSecurityService() {
    return new ApiSecurityServiceImpl();
  }
}
