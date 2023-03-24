/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.emuns.FunctionEnum;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/14 11:47
 */
@Data
public class Trigger {

  private List<DataSource> datasources;

  private String query;

  private String aggregator;

  private Long downsample;

  private int stepNum;

  private int triggerStepNum;

  // trigger summary
  private String triggerContent;

  // detected date
  private List<DataResult> dataResult;

  private List<CompareParam> compareParam;

  // detection mode
  private FunctionEnum type;

  private String triggerTitle;

  // compare config list
  private List<CompareConfig> compareConfigs;

  private String triggerId;

  // ai alert config
  private RuleConfig ruleConfig;

  // zero fill
  private boolean zeroFill;
  // period type
  private PeriodType periodType;

}
