/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import java.util.List;
import java.util.Map;

/**
 * alert rule extra data
 * 
 * @author masaimu
 * @version 2023-02-21 15:57:00
 */
public class AlertRuleExtra {
  public NotificationConfig notificationConfig;
  public String sourceLink;
  public List<String> alertTags;
  public Map<String, String> tagAlias;
  public boolean isRecord;
  public String md5;
  public AlertSilenceConfig alertSilenceConfig;

  public NotificationTemplate getDingTalkTemplate() {
    if (notificationConfig == null || notificationConfig.dingtalkTemplate == null) {
      return null;
    }
    return notificationConfig.dingtalkTemplate;
  }
}
