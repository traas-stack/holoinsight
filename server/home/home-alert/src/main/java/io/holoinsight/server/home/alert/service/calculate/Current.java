/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerDataResult;
import io.holoinsight.server.common.dao.emuns.FunctionEnum;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.CompareParam;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerResult;
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
  public TriggerResult invoke(TriggerDataResult triggerDataResult,
      FunctionConfigParam functionConfigParam) {
    return doInvoke(triggerDataResult, functionConfigParam);
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
