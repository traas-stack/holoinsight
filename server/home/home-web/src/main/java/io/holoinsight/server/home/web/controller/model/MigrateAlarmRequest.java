/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

/**
 * @author zanghaibo
 * @time 2022-11-18 1:49 下午
 */

@Data
public class MigrateAlarmRequest {

  private Long alarmRuleId;

  private String sourceTenant;

  private String targetTenant;
  private String targetWorkspace;
}
