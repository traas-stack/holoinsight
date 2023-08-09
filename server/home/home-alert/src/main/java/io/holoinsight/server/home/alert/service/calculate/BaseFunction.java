/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.alert.model.function.FunctionLogic;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import io.holoinsight.server.home.facade.trigger.CompareParam;
import io.holoinsight.server.home.facade.trigger.TriggerResult;

import java.util.Map;

/**
 * @author masaimu
 * @version 2023-03-21 15:32:00
 */
public abstract class BaseFunction implements FunctionLogic {

  protected Double rate(Double current, Double lastValue) {
    if (Double.compare(current, 0d) == 0) {
      return 0.0;
    }
    if (Double.compare(lastValue, 0d) == 0) {
      return current > 0 ? Double.POSITIVE_INFINITY : Double.NEGATIVE_INFINITY;
    }
    return (current - lastValue) / lastValue;
  }

  protected Double fillZero(boolean zeroFill, Double value) {
    if (value == null && zeroFill) {
      value = 0d;
    }
    return value;
  }

  protected Double fillOne(boolean zeroFill, Double value) {
    if (value == null && zeroFill) {
      value = 1d;
    }
    return value;
  }

  protected TriggerResult doInvoke(DataResult dataResult, FunctionConfigParam functionConfigParam) {
    TriggerResult result = new TriggerResult();
    result.setHit(false);

    long delta = getDelta(functionConfigParam.getPeriodType());
    Map<Long, Double> points = dataResult.getPoints();
    Double currentValue = points.get(functionConfigParam.getPeriod());
    if (currentValue == null && functionConfigParam.isZeroFill()) {
      currentValue = 0d;
    }
    result.setCurrentValue(currentValue);
    long duration = functionConfigParam.getDuration();
    for (long i = 0; i < duration; i++) {
      long time = functionConfigParam.getPeriod() - i * 60000L;
      Double current = fillZero(functionConfigParam.isZeroFill(), points.get(time));
      Double past = fillZero(functionConfigParam.isZeroFill(), points.get(time - delta));
      Double comparedValue = getComparedValue(current, past);
      // comparedValue is null means rule has missed at this data point, the trigger condition can
      // never be reached.
      if (comparedValue == null) {
        return result;
      }
      for (CompareParam cmp : functionConfigParam.getCmp()) {
        double value = getValue(cmp);
        boolean isHit = TriggerLogicNew.compareValue(cmp.getCmp(), value, comparedValue);
        // comparedValue cannot hit means rule has missed at this data point
        if (!isHit) {
          return result;
        }
      }
      result.getValues().put(time, current);
    }
    result.setHit(true);
    return result;
  }

  private long getDelta(PeriodType periodType) {
    if (periodType == null) {
      return 0;
    } else {
      return periodType.intervalMillis();
    }
  }

  protected abstract double getValue(CompareParam cmp);

  protected abstract Double getComparedValue(Double current, Double past);
}
