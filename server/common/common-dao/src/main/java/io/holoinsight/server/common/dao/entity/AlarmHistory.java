/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: AlarmHistory.java, v 0.1 2022年04月08日 2:46 下午 jinsong.yjs Exp $
 */
@Data
@TableName("alarm_history")
public class AlarmHistory {
  /**
   * id
   */
  @TableId(type = IdType.AUTO)
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
  private String triggerContent;

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
  private String extra;

  /**
   * 环境类型
   */
  private String envType;

  /**
   * app
   */
  private String app;

  private boolean deleted;
}
