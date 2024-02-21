/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.core;

import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.plugin.config.CollectType;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.ExecuteRule;

import java.util.Map;


/**
 *
 * @author jsy1001de
 * @version 1.0: AbstractCentralIntegrationPlugin.java, v 0.1 2022年11月22日 上午11:35 jinsong.yjs Exp $
 */
public abstract class AbstractCentralIntegrationPlugin<T> extends AbstractIntegrationPlugin {


  public ExecuteRule getExecuteRule() {
    ExecuteRule executeRule = new ExecuteRule();
    executeRule.setType("fixedRate");
    executeRule.setFixedRate(60000);

    return executeRule;
  }

  public Map<String, Object> getExecutorSelector() {
    return GaeaConvertUtil.getCenterExecutorSelector();
  }

  public GaeaCollectRange getGaeaCollectRange() {
    return GaeaConvertUtil.convertCentralCollectRange(this.collectRange);
  }

  public CollectType getCollectType() {
    return CollectType.CENTER;
  }


  public void afterAction(IntegrationPluginDTO integrationPluginDTO) {

  }
}
