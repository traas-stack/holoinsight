/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.data;

import io.holoinsight.server.home.alert.model.compute.ComputeTask;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.model.data.DataResult;
import io.holoinsight.server.home.alert.model.data.PqlRule;
import io.holoinsight.server.home.alert.model.data.Rule;
import io.holoinsight.server.home.alert.model.trigger.Trigger;
import io.holoinsight.server.home.alert.service.data.load.PqlAlarmLoadData;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/2/22 4:51 下午
 */
@Service
public class AlarmDataSet {

  private static Logger LOGGER = LoggerFactory.getLogger(AlarmDataSet.class);

  private AlarmLoadData alarmLoadData;

  @Resource
  private LoadDataFactory loadDataFactory;

  @Resource
  private PqlAlarmLoadData pqlAlarmLoadData;

  public void loadData(ComputeTaskPackage computeTaskPackage) {

    if (computeTaskPackage == null || computeTaskPackage.getComputeTaskList() == null) {
      return;
    }
    for (ComputeTask computeTask : computeTaskPackage.getComputeTaskList()) {
      computeTask.getInspectConfigs().parallelStream().forEach(inspectConfig -> {
        // 处理pql告警逻辑
        if (inspectConfig.getIsPql()) {
          try {
            PqlRule pqlRule = inspectConfig.getPqlRule();
            if (pqlRule != null && !StringUtils.isEmpty(pqlRule.getPql())) {
              List<DataResult> result =
                  pqlAlarmLoadData.queryDataResult(computeTask, inspectConfig);
              pqlRule.setDataResult(result);
              inspectConfig.setPqlRule(pqlRule);
            }
          } catch (Exception exception) {
            LOGGER.error("Pql query Exception", exception);
          }
        }
        // 处理current&ai告警逻辑
        else {
          Rule rule = inspectConfig.getRule();
          if (rule == null || CollectionUtils.isEmpty(rule.getTriggers())) {
            return;
          }
          for (Trigger trigger : rule.getTriggers()) {
            try {
              // 接入统一数据源，查询数据信息
              alarmLoadData = loadDataFactory.getLoadDataService(trigger.getType().getType());
              List<DataResult> dataResults =
                  alarmLoadData.queryDataResult(computeTask, inspectConfig, trigger);
              trigger.setDataResult(dataResults);
            } catch (Exception exception) {
              LOGGER.error("AlarmLoadData Exception", exception);
            }
          }
        }

      });
    }
    // LOGGER.info("AlarmDataSet SUCCESS {} ", G.get().toJson(computeTaskPackage));
  }
}
