/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/14 5:32 下午
 */
@Data
public class AlarmHistoryDTO {

  private Long id;

  /**
   * 创建时间
   */
  private Date gmtCreate;

  /**
   * 修改时间
   */
  private Date gmtModified;

  /**
   * 告警时间
   */
  private Date alarmTime;

  /**
   * 恢复时间
   */
  private Date recoverTime;

  /**
   * 持续时间
   */
  private Long duration;

  /**
   * 告警id
   */
  private String uniqueId;

  private List<String> uniqueIds;

  /**
   * 规则名称
   */
  private String ruleName;

  /**
   * 告警级别
   */
  private String alarmLevel;

  /**
   * 触发详情
   */
  private List<String> triggerContent;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * workspace
   */
  private String workspace;

  /**
   * 来源类型
   */
  private String sourceType;

  /**
   * 来源id
   */
  private Long sourceId;

  /**
   * 额外信息
   */
  private AlertHistoryExtra extra;

  /**
   * 环境类型
   */
  private String envType;

  private List<String> app;

  private boolean deleted;
}
