/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/4/12 9:38 下午
 */
@Data
public class AlarmRuleDTO {

  /**
   * id
   */
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
   * 触发方式简述
   */
  private List<String> alarmContent;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * 告警规则
   */
  private Map<String, Object> rule;

  /**
   * 生效时间
   */
  private Map<String, Object> timeFilter;

  /**
   * 屏蔽id
   */
  private Long blockId;

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
  private Map<String, Object> extra;

  /**
   * 环境类型
   */
  private String envType;
}
