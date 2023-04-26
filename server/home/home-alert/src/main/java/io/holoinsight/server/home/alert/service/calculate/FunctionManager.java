/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;


import io.holoinsight.server.home.alert.model.function.FunctionLogic;
import io.holoinsight.server.home.facade.emuns.FunctionEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/3/17 9:54 下午
 */
@Service
public class FunctionManager implements InitializingBean {

  public static final Map<FunctionEnum, FunctionLogic> functionMap = new HashMap<>();

  @Resource
  private Current current;

  @Resource
  private PeriodAbs periodAbs;

  @Resource
  private PeriodValue periodValue;

  @Resource
  private PeriodRate periodRate;

  @Resource
  private Step step;

  @Resource
  private ValueUpAbnormalDetect valueUpAbnormalDetect;

  @Resource
  private ValueDownAbnormalDetect valueDownAbnormalDetect;

  @Resource
  private AnomalyUpAbnormalDetect anomalyUpAbnormalDetect;

  @Resource
  private AnomalyDownAbnormalDetect anomalyDownAbnormalDetect;

  public static void register(FunctionLogic function) {
    functionMap.put(function.getFunc(), function);
  }

  @Override
  public void afterPropertiesSet() {
    register(current);
    register(periodRate);
    register(periodValue);
    register(periodAbs);
    register(step);
    register(valueUpAbnormalDetect);
    register(valueDownAbnormalDetect);
    register(anomalyUpAbnormalDetect);
    register(anomalyDownAbnormalDetect);
  }
}
