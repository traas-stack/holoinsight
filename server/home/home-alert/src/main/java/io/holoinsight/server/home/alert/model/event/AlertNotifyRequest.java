/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.alert.model.event;

import io.holoinsight.server.home.facade.AlertRuleExtra;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.trigger.Trigger;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-01-12 21:40:00
 */
@Data
public class AlertNotifyRequest {

  private Map<String/* notify type */, List<String>> userNotifyMap; // 用户信息和通知渠道

  private List<WebhookInfo> dingdingUrls; // 钉钉群

  private List<WebhookInfo> webhookInfos; // 告警相关webhook消息

  private String traceId;

  private String tenant;

  private String workspace;

  private String uniqueId; // 告警规则+id

  private String ruleId; // 告警id

  private String ruleName; // 告警名称

  private Long alarmTime; // 告警时间

  private String alarmLevel; // 告警级别

  private String aggregationNum; // 聚合告警数

  private Map<Trigger, List<NotifyDataInfo>> notifyDataInfos; // 告警计算结果

  private InspectConfig ruleConfig; // 告警规则配置

  private String envType; // 环境类型

  private AlertRuleExtra alertRuleExtra; // 告警附加信息

  public Long alarmHistoryId; // 告警历史 id

  public Long alarmHistoryDetailId; // 告警历史明细 id

  public String sourceType; // 来源类型

  private Long duration; // 持续时间

  private String alertServer; // 告警机器

}
