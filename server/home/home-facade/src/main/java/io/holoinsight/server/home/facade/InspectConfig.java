/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;


/**
 * @author wangsiyuan
 * @date 2022/3/11 6:45 下午
 */
@Data
public class InspectConfig {

  private String traceId;

  private String uniqueId;

  private String tenant;

  private String workspace;

  private String ruleName;

  private String ruleType;

  private String alarmLevel; // 枚举

  private String ruleDescribe;

  private Rule rule;

  private PqlRule pqlRule; // pql规则+数据

  private Boolean isPql;

  private TimeFilter timeFilter; // 生效时间

  private Boolean status; // 开启报警

  private Boolean isMerge; // 开启合并

  private String mergeType; // 合并方式

  private Boolean recover; // 恢复通知

  private String noticeType; // 枚举，通知方式

  private String envType; // 环境类型

  private String sourceType; // 来源类型
}
