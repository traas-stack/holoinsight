/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.converter;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.model.event.NotifyDataInfo;
import io.holoinsight.server.home.alert.model.event.WebhookInfo;
import io.holoinsight.server.home.dal.model.AlarmHistory;
import io.holoinsight.server.home.dal.model.AlarmRule;
import io.holoinsight.server.home.dal.model.AlarmWebhook;
import io.holoinsight.server.home.dal.model.AlertmanagerWebhook;
import io.holoinsight.server.home.facade.AlertRuleExtra;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.PqlRule;
import io.holoinsight.server.home.facade.Rule;
import io.holoinsight.server.home.facade.TimeFilter;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Trigger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author wangsiyuan
 * @date 2022/6/21 9:33 下午
 */
public class DoConvert {
  private static Logger LOGGER = LoggerFactory.getLogger(DoConvert.class);

  public static InspectConfig alarmRuleConverter(AlarmRule alarmRuleDO) {
    InspectConfig inspectConfig = new InspectConfig();
    try {
      inspectConfig.setTraceId(UUID.randomUUID().toString());
      BeanUtils.copyProperties(alarmRuleDO, inspectConfig);
      inspectConfig.setUniqueId(alarmRuleDO.getRuleType() + "_" + alarmRuleDO.getId().toString());
      if (alarmRuleDO.getRuleType().equals("pql")) {
        PqlRule pqlRule = new PqlRule();
        pqlRule.setPql(alarmRuleDO.getPql());
        pqlRule.setDataResult(new ArrayList<>());
        inspectConfig.setIsPql(true);
        inspectConfig.setPqlRule(pqlRule);
      } else {
        inspectConfig.setRule(G.get().fromJson(alarmRuleDO.getRule(), Rule.class));
        inspectConfig.setIsPql(false);
        inspectConfig.setMetrics(getMetricsFromRule(inspectConfig.getRule()));
      }
      inspectConfig.setTimeFilter(G.get().fromJson(alarmRuleDO.getTimeFilter(), TimeFilter.class));
      inspectConfig.setStatus(alarmRuleDO.getStatus() != 0);
      inspectConfig.setIsMerge(alarmRuleDO.getIsMerge() != 0);
      inspectConfig.setRecover(alarmRuleDO.getRecover() != 0);
      inspectConfig.setEnvType(alarmRuleDO.getEnvType());
      if (StringUtils.isNotBlank(alarmRuleDO.getExtra())) {
        AlertRuleExtra alertRuleExtra = J.fromJson(alarmRuleDO.getExtra(), AlertRuleExtra.class);
        inspectConfig.setAlertRecord(alertRuleExtra.isRecord);
        inspectConfig.setAlertSilenceConfig(alertRuleExtra.alertSilenceConfig);
      }
    } catch (Exception e) {
      LOGGER.error("fail to convert alarmRule {}", G.get().toJson(alarmRuleDO), e);
    }
    return inspectConfig;
  }

  private static List<String> getMetricsFromRule(Rule rule) {
    List<String> metrics = new ArrayList<>();
    if (rule == null || CollectionUtils.isEmpty(rule.getTriggers())) {
      return metrics;
    }
    for (Trigger trigger : rule.getTriggers()) {
      if (CollectionUtils.isEmpty(trigger.getDatasources())) {
        continue;
      }
      for (DataSource dataSource : trigger.getDatasources()) {
        if (StringUtils.isNotEmpty(dataSource.getMetric())) {
          metrics.add(dataSource.getMetric());
        }
      }
    }
    return metrics;
  }

  public static WebhookInfo alertWebhookDoConverter(AlarmWebhook alertWebhook) {
    WebhookInfo webhookInfo = new WebhookInfo();
    if (alertWebhook != null) {
      BeanUtils.copyProperties(alertWebhook, webhookInfo);
    }
    return webhookInfo;
  }

  public static AlarmHistory alertHistoryConverter(AlertNotify alertNotify) {
    AlarmHistory alarmHistory = new AlarmHistory();
    BeanUtils.copyProperties(alertNotify, alarmHistory);
    if (alertNotify.isPqlNotify()) {
      alarmHistory.setTriggerContent(J.toJson(Arrays.asList(alertNotify.getPqlRule().getPql())));
    } else {
      Set<String> triggerContent = new HashSet<>();
      List<NotifyDataInfo> notifyDataInfos = new ArrayList<>();
      alertNotify.getNotifyDataInfos().forEach((key, value) -> {
        notifyDataInfos.addAll(value);
      });
      notifyDataInfos.forEach(e -> triggerContent.add(e.getTriggerContent()));
      alarmHistory.setTriggerContent(G.get().toJson(triggerContent));
    }
    alarmHistory.setGmtCreate(new Date());
    alarmHistory.setAlarmTime(new Date(alertNotify.getAlarmTime()));

    return alarmHistory;
  }

}
