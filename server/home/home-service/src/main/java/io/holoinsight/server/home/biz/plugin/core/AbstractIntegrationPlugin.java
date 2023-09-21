/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.plugin.config.CollectType;
import io.holoinsight.server.home.biz.plugin.model.Plugin;
import io.holoinsight.server.home.biz.plugin.model.PluginType;
import io.holoinsight.server.home.biz.service.IntegrationProductService;
import io.holoinsight.server.home.biz.service.MetaService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.integration.GaeaTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: AbstractIntegrationPlugin.java, v 0.1 2022年11月17日 下午4:02 jinsong.yjs Exp $
 */
@Service
public abstract class AbstractIntegrationPlugin<T> extends Plugin {

  @Autowired
  public IntegrationProductService integrationProductService;

  @Autowired
  public MetaService metaService;

  @Autowired
  public TenantInitService tenantInitService;

  public String tenant;

  /**
   * 采集范围
   */
  public CloudMonitorRange collectRange;

  /**
   * 可指定的指标名
   */
  public String metricName;

  /**
   * 配置唯一键
   */
  public String gaeaTableName;

  /**
   * 采集插件
   */
  public String collectPlugin;

  /**
   * 创建采集配置
   * 
   * @return
   */
  public GaeaTask generateCollectConfig() {
    return buildTask();
  }

  public abstract GaeaTask buildTask();

  public abstract List<T> genPluginList(IntegrationPluginDTO integrationPluginDTO);

  public abstract void afterAction(IntegrationPluginDTO integrationPluginDTO);

  public abstract Map<String, Object> getExecutorSelector();

  public abstract GaeaCollectRange getGaeaCollectRange();

  public abstract CollectType getCollectType();

  @Override
  public PluginType getPluginType() {
    return PluginType.datasource;
  }
}
