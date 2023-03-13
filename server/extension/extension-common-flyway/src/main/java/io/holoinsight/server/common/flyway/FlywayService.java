/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.flyway;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.holoinsight.server.common.dao.entity.FlywaySchemaHistory;
import io.holoinsight.server.common.dao.mapper.FlywaySchemaHistoryMapper;
import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.CollectionUtils;

/**
 * The execution of FlywayService needs to create beans first after datasource initialization and
 * execute the initialization method to realize the management of sql scripts, so it is realized by
 * implementing BeanPostProcessor;
 * 
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

  private FlywaySchemaHistoryMapper flywaySchemaHistoryMapper;

  public FlywayService(FlywaySchemaHistoryMapper flywaySchemaHistoryMapper,
      FlywayProperties properties, DataSource dataSource) {
    this.flywaySchemaHistoryMapper = flywaySchemaHistoryMapper;
    FluentConfiguration config = Flyway.configure().dataSource(dataSource);
    fillConfig(config, properties);
    this.flyway = config.load();
  }

  @PostConstruct
  public void migrate() {
    flyway.migrate();
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

  /**
   * Manage sql version execution information migrate : Re-trigger the execution of the SQL file;
   * repair : Try to repair the history table and the latest migrate; list : List the execution
   * history of the SQL version; skipLatest : Skip the latest failed SQL version; skipAll : Skip all
   * SQL versions that fail to execute.
   * 
   * @param action
   * @return
   */
  public Object doAction(String action) {
    if (REPAIR.equalsIgnoreCase(action)) {
      return flyway.repair();
    } else if (MIGRATE.equalsIgnoreCase(action)) {
      return flyway.migrate();
    } else if (SKIP_LATEST.equalsIgnoreCase(action)) {
      FlywaySchemaHistory flywaySchemaHistory = new FlywaySchemaHistory();
      flywaySchemaHistory.setSuccess(true);
      UpdateWrapper<FlywaySchemaHistory> updateWrapper = new UpdateWrapper<>();
      updateWrapper.orderByDesc("version");
      updateWrapper.last("limit 1");
      return flywaySchemaHistoryMapper.update(flywaySchemaHistory, updateWrapper);
    } else if (SKIP_ALL.equalsIgnoreCase(action)) {
      FlywaySchemaHistory flywaySchemaHistory = new FlywaySchemaHistory();
      flywaySchemaHistory.setSuccess(true);
      UpdateWrapper<FlywaySchemaHistory> updateWrapper = new UpdateWrapper<>();
      return flywaySchemaHistoryMapper.update(flywaySchemaHistory, updateWrapper);
    } else if (LIST.equalsIgnoreCase(action)) {
      return flywaySchemaHistoryMapper.selectList(new QueryWrapper<>());
    } else {
      throw new UnsupportedOperationException(action);
    }
  }

}
