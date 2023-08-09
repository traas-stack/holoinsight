/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.data;

import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.service.data.load.PqlAlarmLoadData;
import io.holoinsight.server.home.alert.service.event.RecordSucOrFailNotify;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.PqlRule;
import io.holoinsight.server.home.facade.Rule;
import io.holoinsight.server.home.facade.trigger.Trigger;
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

  private static final String ALERT_TASK_COMPUTE = "AlertTaskCompute";

  public void loadData(ComputeTaskPackage computeTaskPackage) {

    if (computeTaskPackage == null
        || CollectionUtils.isEmpty(computeTaskPackage.getInspectConfigs())) {
      return;
    }

    computeTaskPackage.getInspectConfigs().parallelStream().forEach(inspectConfig -> {
      // 处理pql告警逻辑
      if (inspectConfig.getIsPql()) {
        try {
          PqlRule pqlRule = inspectConfig.getPqlRule();
          if (pqlRule != null && !StringUtils.isEmpty(pqlRule.getPql())) {
            List<DataResult> result =
                pqlAlarmLoadData.queryDataResult(computeTaskPackage, inspectConfig);
            pqlRule.setDataResult(result);
            inspectConfig.setPqlRule(pqlRule);
          }
          RecordSucOrFailNotify.alertNotifyProcessSuc(ALERT_TASK_COMPUTE, "Pql query",
              inspectConfig.getAlertNotifyRecord());
        } catch (Exception exception) {
          RecordSucOrFailNotify.alertNotifyProcess("Pql query Exception: " + exception,
              ALERT_TASK_COMPUTE, "Pql query", inspectConfig.getAlertNotifyRecord());
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
                alarmLoadData.queryDataResult(computeTaskPackage, inspectConfig, trigger);
            trigger.setDataResult(dataResults);
          } catch (Exception exception) {
            LOGGER.error("AlarmLoadData Exception", exception);
            RecordSucOrFailNotify.alertNotifyProcess("alarm load data Exception: " + exception,
                ALERT_TASK_COMPUTE, "alarm load data", inspectConfig.getAlertNotifyRecord());
          }
        }
        RecordSucOrFailNotify.alertNotifyProcessSuc(ALERT_TASK_COMPUTE, "alarm load data",
            inspectConfig.getAlertNotifyRecord());
      }

    });

  }

}
