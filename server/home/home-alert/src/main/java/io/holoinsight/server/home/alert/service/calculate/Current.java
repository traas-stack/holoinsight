/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.model.data.DataResult;
import io.holoinsight.server.home.alert.model.emuns.FunctionEnum;
import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.alert.model.function.FunctionLogic;
import io.holoinsight.server.home.alert.model.trigger.CompareParam;
import io.holoinsight.server.home.alert.model.trigger.TriggerResult;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/11/25 9:54 AM
 */
@Service
public class Current implements FunctionLogic {
    @Override
    public FunctionEnum getFunc() {
        return FunctionEnum.Current;
    }

    @Override
    public TriggerResult invoke(DataResult dataResult, FunctionConfigParam functionConfigParam) {
        TriggerResult result = new TriggerResult();
        result.setHit(false);
        Map<Long, Double> points = dataResult.getPoints();
        result.setCurrentValue(points.get(functionConfigParam.getPeriod()));
        long duration = functionConfigParam.getDuration();
        for (long i = 0; i < duration; i++) {
            // 循环条件(增加枚举)
            long time = functionConfigParam.getPeriod() - i * 60000L;
            Double value = points.get(time);
            for (CompareParam cmp : functionConfigParam.getCmp()) {
                if (!TriggerLogicNew.compareValue(cmp, value)) {
                    return result;
                }
            }
            result.getValues().put(time, value);
        }
        result.setHit(true);
        return result;
    }
}
