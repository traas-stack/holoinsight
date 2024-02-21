/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author wangsiyuan
 * @date 2022/4/1 10:40 上午
 */
@Data
@Table(name = "alarm_rule")
public class AlarmRule {

  /**
   * id
   */
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
   * 规则名称
   */
  @Column(name = "rule_name")
  private String ruleName;

  /**
   * 规则类型（AI、RULE）
   */
  @Column(name = "rule_type")
  private String ruleType;

  /**
   * 创建者
   */
  private String creator;

  /**
   * 修改者
   */
  private String modifier;

  /**
   * 告警级别
   */
  @Column(name = "alarm_level")
  private String alarmLevel;

  /**
   * 规则描述
   */
  @Column(name = "rule_describe")
  private String ruleDescribe;

  /**
   * 规则是否生效
   */
  private Byte status;

  /**
   * 合并是否开启
   */
  @Column(name = "is_merge")
  private Byte isMerge;

  /**
   * 合并方式
   */
  @Column(name = "merge_type")
  private String mergeType;

  /**
   * 恢复通知是否开启
   */
  private Byte recover;

  /**
   * 通知方式
   */
  @Column(name = "notice_type")
  private String noticeType;

  /**
   * 租户id
   */
  @Column(name = "tenant")
  private String tenant;

  /**
   * workspace
   */
  @Column(name = "workspace")
  private String workspace;

  /**
   * 告警规则
   */
  private String rule;

  /**
   * pql
   */
  @Column(name = "pql")
  private String pql;

  /**
   * 生效时间
   */
  @Column(name = "time_filter")
  private String timeFilter;

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

  /**
   * 告警模板ID
   */
  @Column(name = "alert_notification_template_id")
  private Long alertNotificationTemplateId;

  @Column(name = "alert_template_uuid")
  private String alertTemplateUuid;

}
