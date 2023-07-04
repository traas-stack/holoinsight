/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import io.holoinsight.server.common.AddressUtil;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.PqlRule;
import io.holoinsight.server.home.facade.TemplateValue;
import io.holoinsight.server.home.facade.emuns.AlertLevel;
import io.holoinsight.server.home.facade.trigger.Trigger;
import io.holoinsight.server.home.facade.trigger.TriggerResult;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/3/29 7:28 下午
 */
@Data
public class AlertNotify {
  private static Logger LOGGER = LoggerFactory.getLogger(AlertNotify.class);

  private String traceId;

  private String tenant;

  private String workspace;

  private String uniqueId;

  private String ruleName;

  private Long alarmTime;

  private String alarmLevel;

  // data info
  private Map<Trigger, List<NotifyDataInfo>> notifyDataInfos;

  // alert msg
  private List<String> msgList;

  private int aggregationNum;

  private Boolean isRecover;

  private List<SubscriptionInfo> subscriptionInfos;

  private List<UserInfo> userInfos;

  private Map<String/* notify type */, List<String>> userNotifyMap;

  private List<WebhookInfo> dingdingUrl;

  private List<WebhookInfo> webhookInfos = new ArrayList<>();

  private NotifyConfig notifyConfig;

  // alert rule config
  private InspectConfig ruleConfig;

  private PqlRule pqlRule;

  private Boolean isPql;

  private String alarmTraceId;

  public Long alarmHistoryId;

  public Long alarmHistoryDetailId;

  private Long duration;

  private String alertServer;

  private String envType;

  private String sourceType;

  // log analysis content
  private List<String> logAnalysis;

  private List<String> logSample;

  public static AlertNotify eventInfoConvert(EventInfo eventInfo, InspectConfig inspectConfig) {
    AlertNotify alertNotify = new AlertNotify();
    BeanUtils.copyProperties(inspectConfig, alertNotify);
    BeanUtils.copyProperties(eventInfo, alertNotify);
    if (!eventInfo.getIsRecover()) {
      alertNotify.setIsPql(inspectConfig.getIsPql() != null && inspectConfig.getIsPql());
      Map<Trigger, List<NotifyDataInfo>> notifyDataInfoMap = new HashMap<>();
      eventInfo.getAlarmTriggerResults().forEach((trigger, resultList) -> {
        List<NotifyDataInfo> notifyDataInfos = new ArrayList<>();
        resultList.forEach(result -> {
          NotifyDataInfo notifyDataInfo = new NotifyDataInfo();
          notifyDataInfo.setMetric(result.getMetric());
          notifyDataInfo.setTags(result.getTags());
          notifyDataInfo.setCurrentValue(result.getCurrentValue());
          notifyDataInfo.setTriggerContent(result.getTriggerContent());
          notifyDataInfos.add(notifyDataInfo);
        });
        notifyDataInfoMap.put(trigger, notifyDataInfos);
      });
      alertNotify.setNotifyDataInfos(notifyDataInfoMap);
      alertNotify.setAggregationNum(notifyDataInfoMap.size());
      alertNotify.setEnvType(eventInfo.getEnvType());
      // 对于平台消费侧，可能需要知道完整的告警规则
      alertNotify.setRuleConfig(inspectConfig);
      tryFixAlertLevel(alertNotify, eventInfo.getAlarmTriggerResults());
      alertNotify.setAlertServer(AddressUtil.getHostAddress());
    }
    return alertNotify;
  }

  private static void tryFixAlertLevel(AlertNotify alertNotify,
      Map<Trigger, List<TriggerResult>> alarmTriggerResults) {
    if (CollectionUtils.isEmpty(alarmTriggerResults)) {
      return;
    }
    for (Map.Entry<Trigger, List<TriggerResult>> entry : alarmTriggerResults.entrySet()) {
      for (TriggerResult triggerResult : entry.getValue()) {
        if (StringUtils.isNotEmpty(triggerResult.getTriggerLevel())) {
          alertNotify.setAlarmLevel(triggerResult.getTriggerLevel());
          return;
        }
      }
    }
  }

  public Boolean isPqlNotify() {
    return isPql != null && isPql;
  }

  public static List<TemplateValue> convertAlertNotify(AlertNotify alertNotify) {
    List<TemplateValue> templateValues = new ArrayList<>();
    Date day = new Date(alertNotify.getAlarmTime());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    TemplateValue templateValue = new TemplateValue();
    templateValue.setRuleName(alertNotify.getRuleName());
    templateValue.setAlarmTime(sdf.format(day));
    templateValue.setAlarmLevel(AlertLevel.getByCode(alertNotify.getAlarmLevel()));
    templateValues.add(templateValue);
    return templateValues;
  }
}
