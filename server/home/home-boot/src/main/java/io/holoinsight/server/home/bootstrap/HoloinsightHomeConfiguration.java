/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.bootstrap;

import io.holoinsight.server.common.service.CommonServiceAutoConfiguration;
import io.holoinsight.server.home.alert.plugin.AlertNotifyHandler;
import io.holoinsight.server.home.alert.plugin.DefaultAlertNotifyHandler;
import io.holoinsight.server.home.alert.plugin.DefaultGatewayService;
import io.holoinsight.server.home.alert.plugin.GatewayService;
import io.holoinsight.server.home.alert.service.event.AlertNotifyChainBuilder;
import io.holoinsight.server.home.alert.service.event.DefaultAlertNotifyChainBuilder;
import io.holoinsight.server.home.biz.plugin.DefaultMarketplaceProductHandler;
import io.holoinsight.server.home.biz.plugin.MarketplaceProductHandler;
import io.holoinsight.server.home.biz.service.EnvironmentService;
import io.holoinsight.server.home.biz.service.impl.DefaultEnvironmentServiceImpl;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import io.holoinsight.server.common.config.EnvironmentProperties;
import io.holoinsight.server.common.dao.CommonDaoConfiguration;
import io.holoinsight.server.common.springboot.ConditionalOnRole;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.biz.service.impl.DefaultTenantInitServiceImpl;

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
}
