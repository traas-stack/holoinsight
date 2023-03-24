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

/**
 * @author masaimu
 * @version 2023-03-17 13:20:00
 */
public class CurrentTest {

  @Test
  public void testZeroFill() {
    DataResult dataResult = new DataResult();
    Map<Long, Double> points = new LinkedHashMap<>();
    points.put(1678079340000L, 0d);
    points.put(1678079400000L, 0d);
    points.put(1678079460000L, 0d);
    points.put(1678079580000L, 0d);
    dataResult.setPoints(points);

    CompareParam compareParam = new CompareParam();
    compareParam.setCmpValue(0d);
    compareParam.setCmp(CompareOperationEnum.LTE);

    FunctionConfigParam functionConfigParam = new FunctionConfigParam();
    functionConfigParam.setZeroFill(false);
    functionConfigParam.setPeriod(1678079580000L);
    functionConfigParam.setDuration(3);
    functionConfigParam.setCmp(Arrays.asList(compareParam));

    Current current = new Current();
    TriggerResult triggerResult = current.invoke(dataResult, functionConfigParam);
    Assert.assertFalse(triggerResult.isHit());

    functionConfigParam.setZeroFill(true);
    triggerResult = current.invoke(dataResult, functionConfigParam);
    Assert.assertTrue(triggerResult.isHit());
  }

  @Test
  public void testHit() {
    DataResult dataResult = new DataResult();
    Map<Long, Double> points = new LinkedHashMap<>();
    points.put(1678079340000L, 11d);
    points.put(1678079400000L, 13d);
    points.put(1678079460000L, 14d);
    points.put(1678079520000L, 14d);
    points.put(1678079580000L, 10d);
    dataResult.setPoints(points);

    CompareParam compareParam = new CompareParam();
    compareParam.setCmpValue(10d);
    compareParam.setCmp(CompareOperationEnum.GTE);

    FunctionConfigParam functionConfigParam = new FunctionConfigParam();
    functionConfigParam.setPeriod(1678079580000L);
    functionConfigParam.setDuration(3);
    functionConfigParam.setCmp(Arrays.asList(compareParam));

    Current current = new Current();
    TriggerResult triggerResult = current.invoke(dataResult, functionConfigParam);
    Assert.assertTrue(triggerResult.isHit());
  }

  @Test
  public void testMiss() {
    DataResult dataResult = new DataResult();
    Map<Long, Double> points = new LinkedHashMap<>();
    points.put(1678079340000L, 11d);
    points.put(1678079400000L, 13d);
    points.put(1678079460000L, 14d);
    points.put(1678079520000L, 1d);
    points.put(1678079580000L, 10d);
    dataResult.setPoints(points);

    CompareParam compareParam = new CompareParam();
    compareParam.setCmpValue(10d);
    compareParam.setCmp(CompareOperationEnum.LT);

    FunctionConfigParam functionConfigParam = new FunctionConfigParam();
    functionConfigParam.setPeriod(1678079580000L);
    functionConfigParam.setDuration(1);
    functionConfigParam.setCmp(Arrays.asList(compareParam));

    Current current = new Current();
    TriggerResult triggerResult = current.invoke(dataResult, functionConfigParam);
    Assert.assertFalse(triggerResult.isHit());
  }
}
