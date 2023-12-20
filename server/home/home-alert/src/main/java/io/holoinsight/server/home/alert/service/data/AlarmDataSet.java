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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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

          RecordSucOrFailNotify.alertNotifyProcessSuc(ALERT_TASK_COMPUTE, "pql query",
              inspectConfig.getAlertNotifyRecord());
        } catch (Exception exception) {
          RecordSucOrFailNotify.alertNotifyProcessFail("pql query Exception: " + exception,
              ALERT_TASK_COMPUTE, "pql query", inspectConfig.getAlertNotifyRecord());
          LOGGER.error("Pql query Exception", exception);
        }
      } else {
        // 处理rule&ai告警逻辑
        Rule rule = inspectConfig.getRule();
        if (rule == null || CollectionUtils.isEmpty(rule.getTriggers())) {
          return;
        }
        boolean notifySuccess = true;
        for (Trigger trigger : rule.getTriggers()) {
          if (null == trigger || null == trigger.getType())
            continue;
          try {
            // 接入统一数据源，查询数据信息
            alarmLoadData = loadDataFactory.getLoadDataService(trigger.getType().getType());
            List<DataResult> dataResults =
                alarmLoadData.queryDataResult(computeTaskPackage, inspectConfig, trigger);
            trigger.setDataResult(dataResults);
          } catch (Exception exception) {
            LOGGER.error("AlarmLoadData Exception", exception);
            RecordSucOrFailNotify.alertNotifyProcessFail("alarm load data Exception: " + exception,
                ALERT_TASK_COMPUTE, "alarm load data", inspectConfig.getAlertNotifyRecord());
            notifySuccess = false;
          }
        }
        if (notifySuccess) {
          RecordSucOrFailNotify.alertNotifyProcessSuc(ALERT_TASK_COMPUTE, "alarm load data",
              inspectConfig.getAlertNotifyRecord());
        }
      }

    });

  }

}
