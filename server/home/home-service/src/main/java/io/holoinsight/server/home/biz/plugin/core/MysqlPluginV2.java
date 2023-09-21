/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.plugin.core;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.MD5Hash;
import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.plugin.config.CollectType;
import io.holoinsight.server.home.biz.plugin.config.MysqlConfig;
import io.holoinsight.server.home.biz.plugin.model.PluginModel;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import io.holoinsight.server.registry.model.integration.GaeaTask;
import io.holoinsight.server.registry.model.integration.mysql.MysqlConf;
import io.holoinsight.server.registry.model.integration.mysql.MysqlTaskV2;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: MysqlPluginV2.java, Date: 2023-08-08 Time: 15:28
 */
@Component
@PluginModel(name = "io.holoinsight.plugin.MysqlPluginV2", version = "2")
public class MysqlPluginV2 extends AbstractIntegrationPlugin<MysqlPluginV2> {

  public MysqlTaskV2 mysqlTaskV2;
  public CollectType collectType;

  @Override
  public GaeaTask buildTask() {
    return mysqlTaskV2;
  }

  @Override
  public List<MysqlPluginV2> genPluginList(IntegrationPluginDTO integrationPluginDTO) {
    List<MysqlPluginV2> mysqlPluginV2s = new ArrayList<>();

    MysqlConfig mysqlConfig =
        J.fromJson(integrationPluginDTO.json, new TypeToken<MysqlConfig>() {}.getType());
    if (null == mysqlConfig || CollectionUtils.isEmpty(mysqlConfig.getConfs())) {
      return mysqlPluginV2s;
    }
    String tableName = String.format("%s_%s_%s_server", integrationPluginDTO.getTenant(),
        StringUtils.isNotBlank(integrationPluginDTO.getWorkspace())
            ? integrationPluginDTO.getWorkspace()
            : "_",
        integrationPluginDTO.getProduct());

    int i = 0;

    List<Map<String, Object>> metaList = new ArrayList<>();
    for (MysqlConf conf : mysqlConfig.getConfs()) {
      MysqlPluginV2 mysqlPluginV2 = new MysqlPluginV2();
      {
        MysqlTaskV2 mysqlTask = new MysqlTaskV2();
        mysqlTask.setPassword(conf.getPassword());
        mysqlTask.setPort(conf.getPort());
        mysqlTask.setUsername(conf.getUser());
        mysqlTask.setExecuteRule(mysqlConfig.getExtraConfig().getExecuteRule());
        mysqlTask.setTransform(mysqlConfig.getExtraConfig().getTransform());
        mysqlTask.setRefMetas(mysqlConfig.getExtraConfig().getRefMetas());

        mysqlPluginV2.collectType = mysqlConfig.getExtraConfig().getCollectType();
        mysqlPluginV2.mysqlTaskV2 = mysqlTask;
        mysqlPluginV2.gaeaTableName = integrationPluginDTO.name + "_" + i++;
        mysqlPluginV2.collectPlugin = "telegraf_mysql";
        mysqlPluginV2.collectRange = GaeaConvertUtil.convertIpsCollectRange(tableName,
            Collections.singletonList(conf.getHost()));
      }

      mysqlPluginV2s.add(mysqlPluginV2);

      Map<String, Object> metaMap = new HashMap<>();
      metaMap.put("siteTenant", integrationPluginDTO.getTenant());
      metaMap.put("ip", conf.getHost());
      metaMap.put("port", conf.getPort());
      metaMap.put("user", conf.getUser());
      metaMap.put("password", conf.getPassword());
      metaList.add(metaMap);
    }

    metaService.syncCompare(tableName, metaList, this::getOutList);
    return mysqlPluginV2s;
  }

  @Override
  public Map<String, Object> getExecutorSelector() {
    if (collectType == CollectType.CENTER) {
      return GaeaConvertUtil.getCenterExecutorSelector();
    }
    return GaeaConvertUtil.getLocalExecutorSelector();
  }

  @Override
  public GaeaCollectRange getGaeaCollectRange() {
    return GaeaConvertUtil.convertCollectRange(this.collectRange);
  }

  private List<Map<String, Object>> getOutList(List<Map<String, Object>> params) {
    List<Map<String, Object>> outList = new ArrayList<>();
    if (CollectionUtils.isEmpty(params))
      return outList;

    params.forEach(param -> {
      Map<String, Object> map = new HashMap<>(param);
      map.put("_uk", MD5Hash.getMD5(J.toJson(map)));
      map.put("_modified", System.currentTimeMillis());
      map.put("_type", "mysql_server");
      outList.add(map);
    });

    return outList;
  }

  public CollectType getCollectType() {
    return collectType;
  }

  public void afterAction(IntegrationPluginDTO integrationPluginDTO) {

  }
}
