/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.compute.ComputeContext;
import io.holoinsight.server.home.alert.model.compute.ComputeInfo;
import io.holoinsight.server.home.alert.model.event.EventInfo;
import io.holoinsight.server.home.alert.model.function.FunctionConfigAIParam;
import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.alert.model.function.FunctionLogic;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.Rule;
import io.holoinsight.server.home.facade.emuns.BoolOperationEnum;
import io.holoinsight.server.home.facade.trigger.CompareConfig;
import io.holoinsight.server.home.facade.trigger.Trigger;
import io.holoinsight.server.home.facade.trigger.TriggerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author wangsiyuan
 * @date 2022/3/21 7:25 下午
 */
@Component
public class AbstractUniformInspectRunningRule {

  private static final Logger logger =
      LoggerFactory.getLogger(AbstractUniformInspectRunningRule.class);

  ThreadPoolExecutor ruleRunner = new ThreadPoolExecutor(20, 100, 10, TimeUnit.SECONDS,
      new ArrayBlockingQueue<>(1000), r -> new Thread(r, "RuleRunner"));

  public EventInfo eval(ComputeContext context) {
    long period = context.getTimestamp();
    InspectConfig inspectConfig = context.getInspectConfig();
    String traceId = inspectConfig.getTraceId();

    EventInfo events = null;
    try {
      if (inspectConfig.getIsPql()) {
        events = runPqlRule(inspectConfig, period);
      } else {
        events = runRule(inspectConfig, period);
      }
    } catch (Throwable ex) {
      logger.error("fail to eval inspectConfig {}, traceId: {} ", G.get().toJson(inspectConfig),
          traceId, ex);
    }
    return events;
  }

  public EventInfo runPqlRule(InspectConfig inspectConfig, long period) {
    if (inspectConfig.getIsPql()
        && !CollectionUtils.isEmpty(inspectConfig.getPqlRule().getDataResult())) {
      EventInfo eventInfo = new EventInfo();
      eventInfo.setIsPql(true);
      eventInfo.setPqlRule(inspectConfig.getPqlRule());
      eventInfo.setAlarmTime(period);
      eventInfo.setEnvType(inspectConfig.getEnvType());
      eventInfo.setIsRecover(false);
      eventInfo.setUniqueId(inspectConfig.getUniqueId());
      return eventInfo;
    }
    // 恢复时这里返回null
    return null;
  }

  public EventInfo runRule(InspectConfig inspectConfig, long period) throws InterruptedException {

    Map<Trigger, List<TriggerResult>> triggerMap = new HashMap<>();// 告警
    for (Trigger trigger : inspectConfig.getRule().getTriggers()) {
      // 后续考虑增加tags比较
      List<DataResult> dataResultList = trigger.getDataResult();
      if (CollectionUtils.isEmpty(dataResultList)) {
        continue;
      }
      int parallelSize = dataResultList.size();
      ComputeInfo computeInfo = ComputeInfo.getComputeInfo(inspectConfig, period);
      List<TriggerResult> triggerResults = new CopyOnWriteArrayList<>();
      CountDownLatch latch = new CountDownLatch(parallelSize);
      for (DataResult dataResult : dataResultList) {
        ruleRunner.execute(() -> {
          try {
            List<TriggerResult> ruleResults = apply(dataResult, computeInfo, trigger);
            for (TriggerResult ruleResult : ruleResults) {
              if (ruleResult.isHit()) {
                triggerResults.add(ruleResult);
              }
            }
          } finally {
            latch.countDown();
          }
        });
      }
      latch.await();
      if (triggerResults.size() != 0) {
        triggerMap.put(trigger, triggerResults);
      }
    }
    Rule rule = inspectConfig.getRule();
    if ((BoolOperationEnum.AND.equals(rule.getBoolOperation())
        && rule.getTriggers().size() == triggerMap.size())
        || (BoolOperationEnum.OR.equals(rule.getBoolOperation()) && !triggerMap.isEmpty())) {
      EventInfo eventInfo = new EventInfo();
      eventInfo.setAlarmTriggerResults(triggerMap);
      eventInfo.setUniqueId(inspectConfig.getUniqueId());
      eventInfo.setBoolOperation(rule.getBoolOperation());
      eventInfo.setAlarmTime(period);
      eventInfo.setIsRecover(false);
      eventInfo.setEnvType(inspectConfig.getEnvType());
      return eventInfo;
    }
    return null;
  }

  /**
   * 执行规则
   *
   * @param dataResult 数据结果
   * @param computeInfo 计算信息
   * @param trigger 触发
   * @return {@link TriggerResult}
   */
  public static List<TriggerResult> apply(DataResult dataResult, ComputeInfo computeInfo,
      Trigger trigger) {
    FunctionLogic inspectFunction = FunctionManager.functionMap.get(trigger.getType());
    // 增加智能告警算法执行
    if (inspectFunction == null) {
      return Collections.singletonList(TriggerResult.create());
    }
    List<TriggerResult> triggerResults = new ArrayList<>();
    List<FunctionConfigParam> functionConfigParams = buildFunctionConfigParam(computeInfo, trigger);
    for (FunctionConfigParam functionConfigParam : functionConfigParams) {
      TriggerResult ruleResult = inspectFunction.invoke(dataResult, functionConfigParam);
      ruleResult.setMetric(dataResult.getMetric());
      ruleResult.setTags(dataResult.getTags());
      ruleResult.setCompareParam(functionConfigParam.getCmp());
      ruleResult.setTriggerContent(functionConfigParam.getTriggerContent());
      ruleResult.setTriggerLevel(functionConfigParam.getTriggerLevel());
      triggerResults.add(ruleResult);
      if (ruleResult.isHit()) {
        break;
      }
    }

    return triggerResults;
  }

  private static List<FunctionConfigParam> buildFunctionConfigParam(ComputeInfo computeInfo,
      Trigger trigger) {
    List<FunctionConfigParam> list = new ArrayList<>();
    if ("AI".equals(trigger.getType().getType())) {
      // 参数组装
      FunctionConfigAIParam functionConfigAIParam = new FunctionConfigAIParam();
      functionConfigAIParam.setCmp(trigger.getCompareParam());
      functionConfigAIParam.setPeriod(computeInfo.getPeriod());
      functionConfigAIParam.setDuration(trigger.getStepNum());
      functionConfigAIParam.setStepTime(trigger.getDownsample());
      functionConfigAIParam.setTraceId(computeInfo.getTraceId());
      functionConfigAIParam.setTenant(computeInfo.getTenant());
      functionConfigAIParam.setTriggerContent(trigger.getTriggerContent());
      functionConfigAIParam.setTrigger(trigger);
      list.add(functionConfigAIParam);
    } else {
      if (!CollectionUtils.isEmpty(trigger.getCompareConfigs())) {
        for (CompareConfig compareConfig : trigger.getCompareConfigs()) {
          FunctionConfigParam functionConfigParam = new FunctionConfigParam();
          functionConfigParam.setCmp(compareConfig.getCompareParam());
          functionConfigParam.setTriggerLevel(compareConfig.getTriggerLevel());
          functionConfigParam.setPeriod(computeInfo.getPeriod());
          functionConfigParam.setDuration(trigger.getStepNum());
          functionConfigParam.setStepTime(trigger.getDownsample());
          functionConfigParam.setTraceId(computeInfo.getTraceId());
          functionConfigParam.setZeroFill(trigger.isZeroFill());
          functionConfigParam.setPeriodType(trigger.getPeriodType());
          functionConfigParam.setTriggerContent(compareConfig.getTriggerContent());
          list.add(functionConfigParam);
        }
      } else {
        FunctionConfigParam functionConfigParam = new FunctionConfigParam();
        functionConfigParam.setCmp(trigger.getCompareParam());
        functionConfigParam.setPeriod(computeInfo.getPeriod());
        functionConfigParam.setDuration(trigger.getStepNum());
        functionConfigParam.setStepTime(trigger.getDownsample());
        functionConfigParam.setTraceId(computeInfo.getTraceId());
        functionConfigParam.setZeroFill(trigger.isZeroFill());
        functionConfigParam.setPeriodType(trigger.getPeriodType());
        functionConfigParam.setTriggerContent(trigger.getTriggerContent());
        list.add(functionConfigParam);
      }

    }
    return list;
  }

}
