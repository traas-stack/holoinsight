/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service.flyway;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import io.holoinsight.server.common.dao.mapper.FlywaySchemaHistoryDOMapper;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.CollectionUtils;

/**
 * @author jiwliu
 * @date 2023/3/1
 */

@Slf4j
public class FlywayService implements BeanPostProcessor {

  public static final String REPAIR = "repair";
  public static final String MIGRATE = "migrate";
  public static final String SKIP_ALL = "skipAll";
  public static final String SKIP_LATEST = "skipLatest";
  public static final String LIST = "list";
  private final Flyway flyway;

  @Autowired
  private FlywaySchemaHistoryDOMapper flywaySchemaHistoryDOMapper;

  public FlywayService(FlywayProperties properties, DataSource dataSource) {
    FluentConfiguration config = Flyway.configure().dataSource(dataSource);
    fillConfig(config, properties);
    this.flyway = config.load();
  }

  @PostConstruct
  public void migrate() {
    try {
      flyway.migrate();
    } catch (Exception e) {
      log.error("[flywaydb] call migrate error", e);
    }
  }

  private void fillConfig(FluentConfiguration config, FlywayProperties properties) {
    List<String> locations = properties.getLocations();
    if (!CollectionUtils.isEmpty(locations)) {
      config.locations(locations.toArray(new String[0]));
    }
    config.baselineOnMigrate(properties.isBaselineOnMigrate());
    config.baselineVersion(properties.getBaselineVersion());
    config.placeholderReplacement(properties.isPlaceholderReplacement());
    config.cleanDisabled(properties.isCleanDisabled());
  }

  public Object doAction(String action) {
    if (REPAIR.equalsIgnoreCase(action)) {
      return flyway.repair();
    } else if (MIGRATE.equalsIgnoreCase(action)) {
      return flyway.migrate();
    } else if (SKIP_ALL.equalsIgnoreCase(action)) {
      return flywaySchemaHistoryDOMapper.skipAll();
    } else if (SKIP_LATEST.equalsIgnoreCase(action)) {
      return flywaySchemaHistoryDOMapper.skipLatest();
    } else if (LIST.equalsIgnoreCase(action)) {
      return flywaySchemaHistoryDOMapper.selectAll();
    } else {
      throw new UnsupportedOperationException(action);
    }
  }

}
