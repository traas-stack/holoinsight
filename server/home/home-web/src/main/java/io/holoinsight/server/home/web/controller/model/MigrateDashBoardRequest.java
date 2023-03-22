/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller.model;

import lombok.Data;

/**
 * @author zanghaibo
 * @time 2022-11-18 1:50 下午
 */

@Data
public class MigrateDashBoardRequest {

  /**
   * dashboard id
   */
  private Long dashboardId;

  /**
   * target tenant
   */
  private String targetTenant;
  private String targetWorkspace;

  /**
   * source tenant
   */
  private String sourceTenant;
}
