/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerDataResult;
import io.holoinsight.server.common.dao.emuns.CompareOperationEnum;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.CompareParam;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.holoinsight.server.common.dao.emuns.PeriodType.HOUR;
import static io.holoinsight.server.common.dao.emuns.PeriodType.WEEK;

/**
 * @author masaimu
 * @version 2023-03-23 16:59:00
 */
public class PeriodAbsTest {

  @Test
  public void testHit() {
    TriggerDataResult triggerDataResult = new TriggerDataResult();
    Map<Long, Double> points = new LinkedHashMap<>();
    points.put(1678079340000L, 11d);
    points.put(1678079400000L, 13d);
    points.put(1678079460000L, 14d);
    points.put(1678079520000L, 14d);
    points.put(1678079580000L, 10d);

    points.put(1678079340000L - HOUR.intervalMillis(), 110d);
    points.put(1678079400000L - HOUR.intervalMillis(), 130d);
    points.put(1678079460000L - HOUR.intervalMillis(), 140d);
    points.put(1678079520000L - HOUR.intervalMillis(), 140d);
    points.put(1678079580000L - HOUR.intervalMillis(), 100d);
    triggerDataResult.setPoints(points);

    CompareParam compareParam = new CompareParam();
    compareParam.setCmpValue(50d);
    compareParam.setCmp(CompareOperationEnum.GT);

    FunctionConfigParam functionConfigParam = new FunctionConfigParam();
    functionConfigParam.setPeriod(1678079580000L);
    functionConfigParam.setDuration(3);
    functionConfigParam.setCmp(Arrays.asList(compareParam));
    functionConfigParam.setPeriodType(HOUR);

    PeriodAbs periodAbs = new PeriodAbs();
    TriggerResult triggerResult = periodAbs.invoke(triggerDataResult, functionConfigParam);
    Assert.assertTrue(triggerResult.isHit());
  }

  @Test
  public void testMiss() {
    TriggerDataResult triggerDataResult = new TriggerDataResult();
    Map<Long, Double> points = new LinkedHashMap<>();
    points.put(1678079340000L, 11d);
    points.put(1678079400000L, 13d);
    points.put(1678079460000L, 14d);
    points.put(1678079520000L, 14d);
    points.put(1678079580000L, 10d);

    points.put(1678079340000L - WEEK.intervalMillis(), 110d);
    points.put(1678079400000L - WEEK.intervalMillis(), 130d);
    points.put(1678079460000L - WEEK.intervalMillis(), 140d);
    points.put(1678079520000L - WEEK.intervalMillis(), 140d);
    points.put(1678079580000L - WEEK.intervalMillis(), 150d);
    triggerDataResult.setPoints(points);

    CompareParam compareParam = new CompareParam();
    compareParam.setCmpValue(100d);
    compareParam.setCmp(CompareOperationEnum.GT);

    FunctionConfigParam functionConfigParam = new FunctionConfigParam();
    functionConfigParam.setPeriod(1678079580000L);
    functionConfigParam.setDuration(5);
    functionConfigParam.setCmp(Arrays.asList(compareParam));
    functionConfigParam.setPeriodType(WEEK);

    PeriodAbs periodAbs = new PeriodAbs();
    TriggerResult triggerResult = periodAbs.invoke(triggerDataResult, functionConfigParam);
    Assert.assertFalse(triggerResult.isHit());
  }

  @Test
  public void testZeroFill() {
    TriggerDataResult triggerDataResult = new TriggerDataResult();
    Map<Long, Double> points = new LinkedHashMap<>();
    points.put(1678079340000L, 11d);
    points.put(1678079400000L, 13d);
    points.put(1678079460000L, 14d);
    points.put(1678079580000L, 10d);

    points.put(1678079340000L - HOUR.intervalMillis(), 110d);
    points.put(1678079400000L - HOUR.intervalMillis(), 130d);
    points.put(1678079460000L - HOUR.intervalMillis(), 140d);
    points.put(1678079520000L - HOUR.intervalMillis(), 140d);
    points.put(1678079580000L - HOUR.intervalMillis(), 100d);
    triggerDataResult.setPoints(points);

    CompareParam compareParam = new CompareParam();
    compareParam.setCmpValue(50d);
    compareParam.setCmp(CompareOperationEnum.GT);

    FunctionConfigParam functionConfigParam = new FunctionConfigParam();
    functionConfigParam.setPeriod(1678079580000L);
    functionConfigParam.setDuration(3);
    functionConfigParam.setCmp(Arrays.asList(compareParam));
    functionConfigParam.setPeriodType(HOUR);
    functionConfigParam.setZeroFill(true);

    PeriodAbs periodAbs = new PeriodAbs();
    TriggerResult triggerResult = periodAbs.invoke(triggerDataResult, functionConfigParam);
    Assert.assertTrue(triggerResult.isHit());

    functionConfigParam.setZeroFill(false);
    triggerResult = periodAbs.invoke(triggerDataResult, functionConfigParam);
    Assert.assertFalse(triggerResult.isHit());
  }

}
