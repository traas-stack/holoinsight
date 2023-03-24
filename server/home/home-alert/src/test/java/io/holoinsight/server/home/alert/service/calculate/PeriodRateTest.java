/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.emuns.CompareOperationEnum;
import io.holoinsight.server.home.facade.trigger.CompareParam;
import io.holoinsight.server.home.facade.trigger.TriggerResult;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.holoinsight.server.home.facade.emuns.PeriodType.MINUTE;
import static io.holoinsight.server.home.facade.emuns.PeriodType.QUARTER_HOUR;

/**
 * @author masaimu
 * @version 2023-03-23 16:33:00
 */
public class PeriodRateTest {

  @Test
  public void testHit() {
    DataResult dataResult = new DataResult();
    Map<Long, Double> points = new LinkedHashMap<>();
    points.put(1678079340000L - QUARTER_HOUR.intervalMillis(), 11d);
    points.put(1678079400000L - QUARTER_HOUR.intervalMillis(), 13d);
    points.put(1678079460000L - QUARTER_HOUR.intervalMillis(), 14d);
    points.put(1678079520000L - QUARTER_HOUR.intervalMillis(), 14d);
    points.put(1678079580000L - QUARTER_HOUR.intervalMillis(), 10d);

    points.put(1678079340000L, 21d);
    points.put(1678079400000L, 23d);
    points.put(1678079460000L, 24d);
    points.put(1678079520000L, 24d);
    points.put(1678079580000L, 15d);
    dataResult.setPoints(points);

    CompareParam compareParam = new CompareParam();
    compareParam.setCmpValue(50d);
    compareParam.setCmp(CompareOperationEnum.GTE);

    FunctionConfigParam functionConfigParam = new FunctionConfigParam();
    functionConfigParam.setPeriod(1678079580000L);
    functionConfigParam.setDuration(5);
    functionConfigParam.setCmp(Arrays.asList(compareParam));
    functionConfigParam.setPeriodType(QUARTER_HOUR);

    PeriodRate periodRate = new PeriodRate();
    TriggerResult triggerResult = periodRate.invoke(dataResult, functionConfigParam);
    Assert.assertTrue(triggerResult.isHit());
  }

  @Test
  public void testMiss() {
    DataResult dataResult = new DataResult();
    Map<Long, Double> points = new LinkedHashMap<>();
    points.put(1678079340000L - QUARTER_HOUR.intervalMillis(), 11d);
    points.put(1678079400000L - QUARTER_HOUR.intervalMillis(), 13d);
    points.put(1678079460000L - QUARTER_HOUR.intervalMillis(), 14d);
    points.put(1678079520000L - QUARTER_HOUR.intervalMillis(), 14d);
    points.put(1678079580000L - QUARTER_HOUR.intervalMillis(), 10d);

    points.put(1678079340000L, 21d);
    points.put(1678079400000L, 23d);
    points.put(1678079460000L, 24d);
    points.put(1678079520000L, 24d);
    points.put(1678079580000L, 15d);
    dataResult.setPoints(points);

    CompareParam compareParam = new CompareParam();
    compareParam.setCmpValue(20d);
    compareParam.setCmp(CompareOperationEnum.LT);

    FunctionConfigParam functionConfigParam = new FunctionConfigParam();
    functionConfigParam.setPeriod(1678079580000L);
    functionConfigParam.setDuration(5);
    functionConfigParam.setCmp(Arrays.asList(compareParam));
    functionConfigParam.setPeriodType(QUARTER_HOUR);

    PeriodRate periodRate = new PeriodRate();
    TriggerResult triggerResult = periodRate.invoke(dataResult, functionConfigParam);
    Assert.assertFalse(triggerResult.isHit());
  }

  @Test
  public void testZeroFill() {
    DataResult dataResult = new DataResult();
    Map<Long, Double> points = new LinkedHashMap<>();
    points.put(1678079340000L - QUARTER_HOUR.intervalMillis(), 11d);
    points.put(1678079400000L - QUARTER_HOUR.intervalMillis(), 13d);
    points.put(1678079460000L - QUARTER_HOUR.intervalMillis(), 14d);
    points.put(1678079580000L - QUARTER_HOUR.intervalMillis(), 10d);

    points.put(1678079340000L, 21d);
    points.put(1678079400000L, 23d);
    points.put(1678079460000L, 24d);
    points.put(1678079520000L, 24d);
    points.put(1678079580000L, 15d);
    dataResult.setPoints(points);

    CompareParam compareParam = new CompareParam();
    compareParam.setCmpValue(50d);
    compareParam.setCmp(CompareOperationEnum.GTE);

    FunctionConfigParam functionConfigParam = new FunctionConfigParam();
    functionConfigParam.setPeriod(1678079580000L);
    functionConfigParam.setDuration(3);
    functionConfigParam.setCmp(Arrays.asList(compareParam));
    functionConfigParam.setPeriodType(QUARTER_HOUR);
    functionConfigParam.setZeroFill(true);

    PeriodRate periodRate = new PeriodRate();
    TriggerResult triggerResult = periodRate.invoke(dataResult, functionConfigParam);
    Assert.assertTrue(triggerResult.isHit());

    functionConfigParam.setZeroFill(false);
    triggerResult = periodRate.invoke(dataResult, functionConfigParam);
    Assert.assertFalse(triggerResult.isHit());

    functionConfigParam.setDuration(1);
    triggerResult = periodRate.invoke(dataResult, functionConfigParam);
    Assert.assertTrue(triggerResult.isHit());
  }
}
