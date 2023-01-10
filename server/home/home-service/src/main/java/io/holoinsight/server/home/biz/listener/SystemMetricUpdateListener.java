/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.listener;

import io.holoinsight.server.home.biz.service.GaeaCollectConfigService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.MetaTableDTO;
import io.holoinsight.server.home.dal.model.dto.MetaTableDTO.TableStatus;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import io.holoinsight.server.registry.model.Elect;
import io.holoinsight.server.registry.model.Elect.RefMeta;
import io.holoinsight.server.registry.model.ExecuteRule;
import io.holoinsight.server.registry.model.From;
import io.holoinsight.server.registry.model.GroupBy;
import io.holoinsight.server.registry.model.Output;
import io.holoinsight.server.registry.model.Output.Gateway;
import io.holoinsight.server.registry.model.Select;
import io.holoinsight.server.registry.model.SqlTask;
import io.holoinsight.server.registry.model.Where;
import io.holoinsight.server.registry.model.Window;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: SystemMetricUpdateListener.java, v 0.1 2022年04月07日 4:10 下午 jinsong.yjs Exp $
 */
@Component
public class SystemMetricUpdateListener {

  @Autowired
  private GaeaCollectConfigService gaeaCollectConfigService;

  @PostConstruct
  void register() {
    EventBusHolder.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onEvent(MetaTableDTO metaTableDTO) {
    // 1. 转换模型
    // 2. 落库
    // 3. 通知registry

    List<String> metricList = metaTableDTO.config.metricList;
    if (CollectionUtils.isEmpty(metricList))
      return;
    Map<String, SqlTask> sqlTasks = new HashMap<>();

    metricList.forEach(metric -> {
      sqlTasks.put("system_" + metric + "_" + metaTableDTO.tenant,
          buildSqlTask(metric, metaTableDTO.config.rateSec));
    });

    if (CollectionUtils.isEmpty(sqlTasks))
      return;

    // 更新
    List<Long> upsert = upsert(sqlTasks, metaTableDTO);

    // 通知registry
    notify(upsert);
  }

  private List<Long> upsert(Map<String, SqlTask> sqlTasks, MetaTableDTO metaTableDTO) {

    Map<String, Object> executorSelector = convertExecutorSelector();

    List<Long> upsertList = new ArrayList<>();
    for (Map.Entry<String, SqlTask> entry : sqlTasks.entrySet()) {
      GaeaCollectConfigDTO gaeaCollectConfigDTO = new GaeaCollectConfigDTO();
      gaeaCollectConfigDTO.tenant = metaTableDTO.tenant;
      gaeaCollectConfigDTO.deleted = false;
      gaeaCollectConfigDTO.json = entry.getValue();
      gaeaCollectConfigDTO.tableName = entry.getKey();
      gaeaCollectConfigDTO.collectRange = convertCollectRange(metaTableDTO.name);
      gaeaCollectConfigDTO.executorSelector = executorSelector;
      gaeaCollectConfigDTO.version = 1L;

      // 下线配置直接置为 deleted=1
      if (metaTableDTO.getStatus() == TableStatus.OFFLINE) {
        Long aLong = gaeaCollectConfigService.updateDeleted(gaeaCollectConfigDTO.tableName);
        if (null != aLong)
          upsertList.add(aLong);
        continue;
      }
      GaeaCollectConfigDTO upsert = gaeaCollectConfigService.upsert(gaeaCollectConfigDTO);
      if (null != upsert) {
        upsertList.add(upsert.id);
      }
    }

    return upsertList;
  }

  /**
   * type = cpu, mem, disk, load, traffic, processperf
   * 
   * @param type
   * @param rateSec 周期
   * @return
   */
  private SqlTask buildSqlTask(String type, Integer rateSec) {
    Map<String, String> metaStrs = new HashMap<>();
    metaStrs.put("ip", "ip");
    metaStrs.put("hostname", "hostname");

    Select select = new Select();
    From from = buildFrom(type);
    Where where = new Where();
    GroupBy groupBy = buildGroupBy(metaStrs);
    Window window = new Window();
    Output output = buildOutput(type);
    ExecuteRule executeRule = buildExecuteRule(rateSec);

    SqlTask sqlTask = new SqlTask();
    {
      sqlTask.setSelect(select);
      sqlTask.setFrom(from);
      sqlTask.setWhere(where);
      sqlTask.setGroupBy(groupBy);
      sqlTask.setWindow(window);
      sqlTask.setOutput(output);
      sqlTask.setExecuteRule(executeRule);
    }

    return sqlTask;
  }

  private From buildFrom(String type) {
    From from = new From();
    from.setType(type);
    return from;
  }

  private GroupBy buildGroupBy(Map<String, String> metaStrs) {

    GroupBy groupBy = new GroupBy();
    groupBy.setGroups(new ArrayList<>());
    metaStrs.forEach((k, v) -> {

      Elect elect = new Elect();
      elect.setType("refMeta");
      elect.setRefMeta(new RefMeta());
      elect.getRefMeta().setName(k);

      GroupBy.Group group = new GroupBy.Group();
      {
        group.setName(v);
        group.setElect(elect);
      }
      groupBy.getGroups().add(group);
    });

    return groupBy;
  }

  private Output buildOutput(String tableName) {
    Output output = new Output();
    if (!StringUtils.isEmpty(tableName)) {
      output.setType("gateway");
      output.setGateway(new Gateway());
      output.getGateway().setMetricName("system_%s");
    }
    return output;
  }

  private ExecuteRule buildExecuteRule(Integer rateSec) {
    ExecuteRule executeRule = new ExecuteRule();
    executeRule.setType("fixedRate");
    executeRule.setFixedRate(rateSec * 1000);
    return executeRule;
  }

  private GaeaCollectRange convertCollectRange(String table) {
    GaeaCollectRange gaeaCollectRange = new GaeaCollectRange();
    gaeaCollectRange.setType("cloudmonitor");
    CloudMonitorRange cloudMonitorRange = new CloudMonitorRange();
    cloudMonitorRange.setTable(table);

    Map<String, List<String>> map = new HashMap<>();
    map.put("_status", Collections.singletonList("ONLINE"));
    map.put("_type", Collections.singletonList("VM"));
    cloudMonitorRange.setCondition(Collections.singletonList(map));
    gaeaCollectRange.setCloudmonitor(cloudMonitorRange);
    return gaeaCollectRange;
  }

  private Map<String, Object> convertExecutorSelector() {

    Map<String, Object> executorSelector = new HashMap<>();
    executorSelector.put("type", "sidecar");
    executorSelector.put("sidecar", new HashMap<>());

    return executorSelector;
  }

  private void notify(List<Long> upsertList) {

    // grpc 通知id更新
  }
}
