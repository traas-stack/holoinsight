/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

import java.util.List;


/**
 * @author wangsiyuan
 * @date 2022/3/11 18:45
 */
@Data
public class InspectConfig {

  private String traceId;

  private String uniqueId;

  private String tenant;

  private String workspace;

  private String ruleName;

  private String ruleType;

  private String alarmLevel;

  private String ruleDescribe;

  private Rule rule;

  // pql rule and data
  private PqlRule pqlRule;

  private Boolean isPql;

  private TimeFilter timeFilter;

  // alert status
  private Boolean status;

  private Boolean isMerge;

  private String mergeType;

  private Boolean recover;

  private String noticeType;

  private String envType;

  private String sourceType;

  private Long sourceId;

  private boolean logPatternEnable;

  private boolean logSampleEnable;

  private List<String> metrics;

  private AlertNotifyRecordDTO alertNotifyRecord;

  private boolean alertRecord;

  private AlertSilenceConfig alertSilenceConfig;
}
