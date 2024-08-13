/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity.dto.alarm.trigger;

import io.holoinsight.server.common.dao.emuns.FunctionEnum;
import io.holoinsight.server.common.dao.emuns.PeriodType;
import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/14 11:47
 */
@Data
public class Trigger {

  private List<DataSource> datasources;

  private String query = "a";

  private String aggregator;

  private Long downsample;

  private int stepNum;

  private int triggerStepNum;

  // trigger summary
  private String triggerContent;

  // detected date
  private List<TriggerDataResult> dataResult;

  private List<CompareParam> compareParam;

  // detection mode
  private FunctionEnum type;

  private String triggerTitle;

  // compare config list
  private List<CompareConfig> compareConfigs;

  // ai alert config
  private RuleConfig ruleConfig;

  // zero fill
  private boolean zeroFill;
  // period type
  private PeriodType periodType;

}
