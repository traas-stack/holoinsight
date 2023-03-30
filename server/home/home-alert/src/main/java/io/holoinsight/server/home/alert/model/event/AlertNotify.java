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

  private String uniqueId; // 告警id

  private String ruleName; // 告警名称

  private Long alarmTime; // 告警时间

  private String alarmLevel; // 告警级别

  private Map<Trigger, List<NotifyDataInfo>> notifyDataInfos; // 数据信息

  private List<String> msgList; // 告警消息

  private int aggregationNum; // 聚合告警数

  private Boolean isRecover; // 恢复通知

  private List<SubscriptionInfo> subscriptionInfos; // 订阅信息

  private List<UserInfo> userInfos; // 用户信息

  private Map<String/* notify type */, List<String>> userNotifyMap; // 用户信息和通知渠道

  private List<WebhookInfo> dingdingUrl; // 钉钉群

  private List<WebhookInfo> webhookInfos; // 告警相关webhook消息

  private NotifyConfig notifyConfig; // 通知增加的信息

  private InspectConfig ruleConfig; // 告警规则配置

  private PqlRule pqlRule; // pql告警规则+结果

  private Boolean isPql; // 是否属于pql告警通知

  private String alarmTraceId; // 告警唯一id

  public Long alarmHistoryId; // 告警历史 id

  public Long alarmHistoryDetailId; // 告警历史明细 id

  private Long duration; // 持续时间

  private String alertServer;

  private String envType; // 环境类型

  private String sourceType; // 来源类型

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
