/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import javax.persistence.Id;
import javax.persistence.Table;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import javax.persistence.Column;
import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: AlarmHistory.java, v 0.1 2022年04月08日 2:46 下午 jinsong.yjs Exp $
 */
@Data
@Table(name = "alarm_history")
public class AlarmHistory {
  /**
   * id
   */
  @Id
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 创建时间
   */
  @Column(name = "gmt_create")
  private Date gmtCreate;

  /**
   * 修改时间
   */
  @Column(name = "gmt_modified")
  private Date gmtModified;

  /**
   * 告警时间
   */
  @Column(name = "alarm_time")
  private Date alarmTime;

  /**
   * 恢复时间
   */
  @Column(name = "recover_time")
  private Date recoverTime;

  /**
   * 持续时间
   */
  private Long duration;

  /**
   * 告警id
   */
  @Column(name = "unique_id")
  private String uniqueId;

  /**
   * 规则名称
   */
  @Column(name = "rule_name")
  private String ruleName;

  /**
   * 告警级别
   */
  @Column(name = "alarm_level")
  private String alarmLevel;

  /**
   * 触发详情
   */
  @Column(name = "trigger_content")
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
  @Column(name = "source_type")
  private String sourceType;

  /**
   * 来源id
   */
  @Column(name = "source_id")
  private Long sourceId;


  /**
   * 额外信息
   */
  private String extra;

  /**
   * 环境类型
   */
  @Column(name = "env_type")
  private String envType;
}
