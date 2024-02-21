/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.alert.model.event;

import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.AlertRuleExtra;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.NotificationTemplate;
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

  /**
   * user info and notification channel
   */
  private Map<String/* notify type */, List<String>> userNotifyMap;

  private List<WebhookInfo> dingdingUrls;

  private List<WebhookInfo> webhookInfos;

  private String traceId;

  private String tenant;

  private String workspace;

  private String uniqueId;

  private String ruleId;

  private String ruleName;

  private Long alarmTime;

  private String alarmLevel;

  private String aggregationNum;

  private Map<Trigger, List<NotifyDataInfo>> notifyDataInfos;

  private InspectConfig ruleConfig;

  private String envType;

  private AlertRuleExtra alertRuleExtra;

  private NotificationTemplate notificationTemplate;

  public Long alarmHistoryId;

  public Long alarmHistoryDetailId;

  public String sourceType;

  private Long duration;

  private String alertServer;

  private String alertIp;

  private String pid;

  // log analysis content
  private List<String> logAnalysis;

  private List<String> logSample;

  private AlertNotifyRecordDTO alertNotifyRecord;

  private boolean notifyRecover;

}
