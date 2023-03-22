/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

import java.util.Date;

/**
 * @author wangsiyuan
 * @date 2022/6/9 9:32 下午
 */
@Data
public class AlarmHistoryDetailDTO {
  /**
   * id
   */
  private Long id;

  /**
   * 告警时间
   */
  private Date alarmTime;

  /**
   * 告警id
   */
  private String uniqueId;

  /**
   * 告警历史id
   */
  private Long historyId;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * workspace
   */
  private String workspace;

  /**
   * 触发方式简述
   */
  private String alarmContent;

  /**
   * 数据源信息
   */
  private String datasource;

  /**
   * 来源类型
   */
  private String sourceType;

  /**
   * 来源id
   */
  private Long sourceId;

  /**
   * 环境类型
   */
  private String envType;
}
