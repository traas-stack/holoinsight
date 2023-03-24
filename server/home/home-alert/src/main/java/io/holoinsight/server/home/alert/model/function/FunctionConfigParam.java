/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.function;

import io.holoinsight.server.home.facade.emuns.PeriodType;
import io.holoinsight.server.home.facade.trigger.CompareParam;
import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/17 9:51 下午
 */
@Data
public class FunctionConfigParam {

  /**
   * detection time point
   */
  private Long period;

  /**
   * data period
   */
  private Long stepTime;

  /**
   * alert duration
   */
  private int duration;

  private List<CompareParam> cmp;

  // trace id
  private String traceId;

  private String triggerLevel;

  // zero fill to time series
  private boolean zeroFill;
  // period type is used to compare past and present values or rate
  private PeriodType periodType;
  // trigger summary
  private String triggerContent;

}
