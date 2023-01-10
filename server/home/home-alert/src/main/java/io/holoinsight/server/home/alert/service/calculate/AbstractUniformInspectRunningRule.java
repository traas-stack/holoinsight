/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.compute.ComputeContext;
import io.holoinsight.server.home.alert.model.compute.ComputeInfo;
import io.holoinsight.server.home.alert.model.data.DataResult;
import io.holoinsight.server.home.alert.model.data.InspectConfig;
import io.holoinsight.server.home.alert.model.data.Rule;
import io.holoinsight.server.home.alert.model.emuns.BoolOperationEnum;
import io.holoinsight.server.home.alert.model.event.EventInfo;
import io.holoinsight.server.home.alert.model.function.FunctionConfigAIParam;
import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.alert.model.function.FunctionLogic;
import io.holoinsight.server.home.alert.model.trigger.Trigger;
import io.holoinsight.server.home.alert.model.trigger.TriggerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author wangsiyuan
 * @date 2022/3/21 7:25 下午
 */
@Component
public class AbstractUniformInspectRunningRule {

  private static final Logger logger =
      LoggerFactory.getLogger(AbstractUniformInspectRunningRule.class);

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

  public EventInfo runRule(InspectConfig inspectConfig, long period) {

    Map<Trigger, List<TriggerResult>> triggerMap = new HashMap<>();// 告警
    for (Trigger trigger : inspectConfig.getRule().getTriggers()) {
      // 后续考虑增加tags比较
      List<DataResult> dataResultList = trigger.getDataResult();
      if (CollectionUtils.isEmpty(dataResultList)) {
        continue;
      }
      ComputeInfo computeInfo = ComputeInfo.getComputeInfo(inspectConfig, period);
      List<TriggerResult> triggerResults = new Vector<>();
      dataResultList.parallelStream().forEach(dataResult -> {
        // 单个tags判断，增加异步处理，收集所有tags结果
        TriggerResult ruleResult = apply(dataResult, computeInfo, trigger);
        if (ruleResult.isHit()) {
          triggerResults.add(ruleResult);
        }
      });
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
  public static TriggerResult apply(DataResult dataResult, ComputeInfo computeInfo,
      Trigger trigger) {
    FunctionLogic inspectFunction = FunctionManager.functionMap.get(trigger.getType());
    // 增加智能告警算法执行
    if (inspectFunction == null) {
      return TriggerResult.create();
    }
    FunctionConfigParam functionConfigParam = buildFunctionConfigParam(computeInfo, trigger);
    TriggerResult ruleResult = inspectFunction.invoke(dataResult, functionConfigParam);
    ruleResult.setMetric(dataResult.getMetric());
    ruleResult.setTags(dataResult.getTags());
    ruleResult.setCompareParam(functionConfigParam.getCmp());
    ruleResult.setTriggerContent(trigger.getTriggerContent());
    return ruleResult;
  }

  private static FunctionConfigParam buildFunctionConfigParam(ComputeInfo computeInfo,
      Trigger trigger) {
    FunctionConfigParam functionConfigParam = new FunctionConfigParam();
    if ("AI".equals(trigger.getType().getType())) {
      // 参数组装
      FunctionConfigAIParam functionConfigAIParam = new FunctionConfigAIParam();
      functionConfigAIParam.setCmp(trigger.getCompareParam());
      functionConfigAIParam.setPeriod(computeInfo.getPeriod());
      functionConfigAIParam.setDuration(trigger.getStepNum());
      functionConfigAIParam.setStepTime(trigger.getDownsample());
      functionConfigAIParam.setTraceId(computeInfo.getTraceId());
      functionConfigAIParam.setTenant(computeInfo.getTenant());
      functionConfigAIParam.setTrigger(trigger);
      functionConfigParam = functionConfigAIParam;
    } else {
      functionConfigParam.setCmp(trigger.getCompareParam());
      functionConfigParam.setPeriod(computeInfo.getPeriod());
      functionConfigParam.setDuration(trigger.getStepNum());
      functionConfigParam.setStepTime(trigger.getDownsample());
      functionConfigParam.setTraceId(computeInfo.getTraceId());
    }
    return functionConfigParam;
  }

}
