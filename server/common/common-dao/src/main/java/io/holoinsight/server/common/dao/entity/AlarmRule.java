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
 * @author wangsiyuan
 * @date 2022/4/1 10:40 上午
 */
@Data
@TableName("alarm_rule")
public class AlarmRule {

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
   * 规则名称
   */
  private String ruleName;

  /**
   * 规则类型（AI、RULE）
   */
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
  private String alarmLevel;

  /**
   * 规则描述
   */
  private String ruleDescribe;

  /**
   * 规则是否生效
   */
  private Byte status;

  /**
   * 合并是否开启
   */
  private Byte isMerge;

  /**
   * 合并方式
   */
  private String mergeType;

  /**
   * 恢复通知是否开启
   */
  private Byte recover;

  /**
   * 通知方式
   */
  private String noticeType;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * workspace
   */
  private String workspace;

  /**
   * 告警规则
   */
  private String rule;

  /**
   * pql
   */
  private String pql;

  /**
   * 生效时间
   */
  private String timeFilter;

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
   * 告警模板ID
   */
  private Long alertNotificationTemplateId;

  private String alertTemplateUuid;

}
