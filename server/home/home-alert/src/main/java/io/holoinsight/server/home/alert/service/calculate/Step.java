/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

/**
 * @author wangsiyuan
 * @date 2022/11/25 9:55 AM
 */

import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.alert.model.function.FunctionLogic;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.emuns.FunctionEnum;
import io.holoinsight.server.home.facade.trigger.CompareParam;
import io.holoinsight.server.home.facade.trigger.TriggerResult;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/11/25 9:56 AM
 */
@Service
public class Step implements FunctionLogic {
  public FunctionEnum getFunc() {
    return FunctionEnum.Step;
  }

  public TriggerResult invoke(DataResult dataResult, FunctionConfigParam functionConfigParam) {
    TriggerResult fr = new TriggerResult();
    fr.setHit(false);
    int triggerNum = 0;

    // 循环周期 (是否手动计算周期再循环)
    for (Map.Entry<Long, Double> m : dataResult.getPoints().entrySet()) {
      // 循环条件
      boolean isTrigger = true;
      for (CompareParam cmp : functionConfigParam.getCmp()) {
        if (!TriggerLogicNew.compareValue(cmp.getCmp(), cmp.getCmpValue(), m.getValue())) {
          isTrigger = false;
        }
      }
      if (isTrigger) {
        // 记录所有周期的值
        fr.getValues().put(m.getKey(), m.getValue());
        triggerNum++;
      }
    }
    if (triggerNum >= functionConfigParam.getDuration()) {
      fr.setCurrentValue(dataResult.getPoints().get(functionConfigParam.getPeriod()));
      fr.setHit(true);
      fr.setCompareParam(functionConfigParam.getCmp());
      fr.setTags(dataResult.getTags());
    }
    return fr;
  }
}
