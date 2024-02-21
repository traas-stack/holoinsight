/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.emuns.FunctionEnum;
import io.holoinsight.server.home.facade.trigger.CompareParam;
import io.holoinsight.server.home.facade.trigger.TriggerResult;
import org.springframework.stereotype.Service;

/**
 * @author masaimu
 * @version 2023-03-21 14:37:00
 */
@Service
public class PeriodValue extends BaseFunction {
  @Override
  public FunctionEnum getFunc() {
    return FunctionEnum.PeriodValue;
  }

  @Override
  public TriggerResult invoke(DataResult dataResult, FunctionConfigParam functionConfigParam) {
    return doInvoke(dataResult, functionConfigParam);
  }

  @Override
  protected double getValue(CompareParam cmp) {
    return cmp.getCmpValue() == null ? 0d : cmp.getCmpValue();
  }

  @Override
  protected Double getComparedValue(Double current, Double past) {
    if (current == null || past == null) {
      return null;
    }
    return current - past;
  }
}
