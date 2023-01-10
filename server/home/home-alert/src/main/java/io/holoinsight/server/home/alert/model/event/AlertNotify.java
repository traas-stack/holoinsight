/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.event;

import io.holoinsight.server.home.alert.model.data.InspectConfig;
import io.holoinsight.server.home.alert.model.data.PqlRule;
import io.holoinsight.server.home.alert.model.trigger.Trigger;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/3/29 7:28 下午
 */
@Data
public class AlertNotify {

  private String traceId;

  private String tenant;

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

  private List<String> dingdingUrl; // 钉钉群

  private List<WebhookInfo> webhookInfos; // 告警相关webhook消息

  private NotifyConfig notifyConfig; // 通知增加的信息

  private InspectConfig ruleConfig; // 告警规则配置

  private PqlRule pqlRule; // pql告警规则+结果

  private Boolean isPql; // 是否属于pql告警通知

  private String alarmTraceId; // 告警唯一id

  /**
   * 对于小程序云站点，没有用户信息，需要裸记录订阅信息
   */
  // 短信渠道
  private List<String> smsPhones;
  // 电话渠道
  private List<String> dyvmsPhones;
  // 钉钉渠道
  private List<String> ddWebhooks;
  // 邮件渠道
  private List<String> emailAddresses;
  // 环境类型
  private String envType;

  public static AlertNotify eventInfoConver(EventInfo eventInfo, InspectConfig inspectConfig) {
    AlertNotify alertNotify = new AlertNotify();
    BeanUtils.copyProperties(inspectConfig, alertNotify);
    BeanUtils.copyProperties(eventInfo, alertNotify);
    if (!eventInfo.getIsRecover()) {

      if (inspectConfig.getIsPql()) {
        alertNotify.setIsPql(true);
      } else {
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
        alertNotify.setIsPql(false);
      }
      alertNotify.setEnvType(eventInfo.getEnvType());
      // 对于平台消费侧，可能需要知道完整的告警规则
      alertNotify.setRuleConfig(inspectConfig);
    }
    return alertNotify;
  }

  public Boolean isPqlNotify() {
    return isPql != null && isPql;
  }

}
