/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.flyway;

import javax.sql.DataSource;

import io.holoinsight.server.common.dao.mapper.FlywaySchemaHistoryMapper;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Flyway database migrations.
 * 
 * @author jiwliu
 * @date 2023/3/1
 */
@AutoConfigureAfter({DataSourceAutoConfiguration.class, JdbcTemplateAutoConfiguration.class})
@EnableConfigurationProperties(FlywayProperties.class)
@ConditionalOnProperty(prefix = "holoinsight.flyway", name = "enabled", havingValue = "true")
public class FlywayAutoConfiguration {

  @Bean
  public FlywayService flywayService(FlywaySchemaHistoryMapper flywaySchemaHistoryMapper,
      DataSource dataSource, FlywayProperties flywayProperties) {
    return new FlywayService(flywaySchemaHistoryMapper, flywayProperties, dataSource);
  }

  @Bean
  public FlywaydbController flywaydbController() {
    return new FlywaydbController();
  }
}
