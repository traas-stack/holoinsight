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
 * @author wangsiyuan
 * @date 2022/11/25 9:54 AM
 */
@Service
public class Current extends BaseFunction {
  @Override
  public FunctionEnum getFunc() {
    return FunctionEnum.Current;
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
    return current;
  }
}
