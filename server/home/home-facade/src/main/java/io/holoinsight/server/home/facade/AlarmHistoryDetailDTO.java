/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import io.holoinsight.server.home.facade.trigger.AlertHistoryDetailExtra;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/6/9 21:32
 */
@Data
public class AlarmHistoryDetailDTO {
  /**
   * id
   */
  private Long id;

  private Date alarmTime;

  private String uniqueId;

  private Long historyId;

  private String tenant;

  /**
   * workspace
   */
  private String workspace;

  private String alarmContent;

  private String datasource;

  private String sourceType;

  private Long sourceId;

  private String envType;

  private AlertHistoryDetailExtra extra;

  private List<String> app;
}
