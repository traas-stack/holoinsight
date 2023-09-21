/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.plugin.config.CollectType;
import io.holoinsight.server.home.biz.plugin.config.MetaLabel;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.Elect;
import io.holoinsight.server.registry.model.ExecuteRule;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: AbstractLocalIntegrationPlugin.java, v 0.1 2022年11月22日 上午11:35 jinsong.yjs Exp $
 */
public abstract class AbstractLocalIntegrationPlugin<T> extends AbstractIntegrationPlugin {

  public Map<String, Elect.RefMeta> getRefMeta() {
    Map<String, Elect.RefMeta> refMetas = new HashMap<>();
    refMetas.put("app", new Elect.RefMeta("app"));
    return refMetas;
  }

  public ExecuteRule getExecuteRule() {
    ExecuteRule executeRule = new ExecuteRule();
    executeRule.setType("fixedRate");
    executeRule.setFixedRate(60000);

    return executeRule;
  }

  public Map<String, Object> getExecutorSelector() {
    return GaeaConvertUtil.getLocalExecutorSelector();
  }

  public GaeaCollectRange getGaeaCollectRange() {
    return GaeaConvertUtil.convertCollectRange(this.collectRange);
  }

  public CloudMonitorRange getGaeaCollectRange(IntegrationPluginDTO integrationPluginDTO,
      List<String> appList, MetaLabel metaLabel) {
    return tenantInitService.getCollectMonitorRange(
        tenantInitService.getTenantServerTable(integrationPluginDTO.getTenant()),
        integrationPluginDTO.getTenant(), integrationPluginDTO.getWorkspace(), appList, metaLabel);
  }

  public CollectType getCollectType() {
    return CollectType.LOCAL;
  }

  public void afterAction(IntegrationPluginDTO integrationPluginDTO) {

  }
}
