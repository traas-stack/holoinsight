/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service.flyway;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author jiwliu
 * @date 2023/3/1
 */

@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
@EnableConfigurationProperties(FlywayProperties.class)
@ConditionalOnProperty(prefix = "holoinsight.flyway", name = "enabled", havingValue = "true")
public class FlywaydbConfiguration {

  @Bean
  public FlywayService flywaydb(DataSource dataSource, FlywayProperties flywayProperties) {
    return new FlywayService(flywayProperties, dataSource);
  }

  @Bean
  public FlywaydbController flywaydbController() {
    return new FlywaydbController();
  }
}
