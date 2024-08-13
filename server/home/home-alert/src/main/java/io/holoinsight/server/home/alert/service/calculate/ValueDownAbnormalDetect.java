/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.emuns.FunctionEnum;
import io.holoinsight.server.common.dao.emuns.PeriodType;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.RuleConfig;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerAIResult;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerDataResult;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.TriggerResult;
import io.holoinsight.server.home.alert.model.compute.algorithm.AlgorithmConfig;
import io.holoinsight.server.home.alert.model.compute.algorithm.DatasourceConfig;
import io.holoinsight.server.home.alert.model.compute.algorithm.ExtendConfig;
import io.holoinsight.server.home.alert.model.compute.algorithm.ValueAlgorithmRequest;
import io.holoinsight.server.home.alert.model.compute.algorithm.ValueAlgorithmResponse;
import io.holoinsight.server.home.alert.model.function.FunctionConfigAIParam;
import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.alert.model.function.FunctionLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * @author wangsiyuan
 * @date 2022/10/11 9:12 下午
 */
@Service
public class ValueDownAbnormalDetect implements FunctionLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(ValueDownAbnormalDetect.class);

  @Value("${holoinsight.alert.algorithm.url}")
  private String url;

  @Override
  public FunctionEnum getFunc() {
    return FunctionEnum.ValueDown;
  }

  @Override
  public TriggerResult invoke(TriggerDataResult triggerDataResult,
      FunctionConfigParam functionConfigParam) {
    FunctionConfigAIParam functionConfigAIParam = (FunctionConfigAIParam) functionConfigParam;
    TriggerAIResult triggerAIResult = new TriggerAIResult();
    ValueAlgorithmRequest valueAlgorithmRequest = new ValueAlgorithmRequest();
    AlgorithmConfig algorithmConfig = new AlgorithmConfig();
    algorithmConfig.setAlgorithmType("value");
    algorithmConfig.setDetectType("down");
    algorithmConfig.setDefaultDuration(2);
    algorithmConfig.setStrictPolicy("auto");

    DatasourceConfig datasourceConfig = new DatasourceConfig();
    datasourceConfig.setFields(Collections
        .singletonList(functionConfigAIParam.getTrigger().getDatasources().get(0).getMetric()));
    datasourceConfig.setSource("saas");

    valueAlgorithmRequest.setTaskId(functionConfigAIParam.getTraceId());
    valueAlgorithmRequest.setDatasourceConfig(datasourceConfig);
    valueAlgorithmRequest
        .setDetectTime(functionConfigAIParam.getPeriod() + PeriodType.MINUTE.intervalMillis());
    valueAlgorithmRequest.setAlgorithmConfig(algorithmConfig);
    valueAlgorithmRequest
        .setExtendConfig(ExtendConfig.triggerConverter(triggerDataResult, functionConfigAIParam));
    RuleConfig ruleConfig = functionConfigAIParam.getTrigger().getRuleConfig();
    if (ruleConfig == null) {
      ruleConfig = RuleConfig.defaultDownConfig(triggerDataResult.getMetric());
    }
    valueAlgorithmRequest.setRuleConfig(ruleConfig);
    // Set the name of the algorithm interface
    String algoUrl = url + "/serving";
    // Call algorithm interface
    String abnormalResult = AlgorithmHttp.invokeAlgorithm(algoUrl, J.toJson(valueAlgorithmRequest),
        functionConfigParam.getTraceId());
    ValueAlgorithmResponse valueAlgorithmResponse =
        J.fromJson(abnormalResult, ValueAlgorithmResponse.class);
    if (valueAlgorithmResponse != null && valueAlgorithmResponse.getIsException()) {
      triggerAIResult.setHit(true);
      triggerAIResult
          .setCurrentValue(valueAlgorithmResponse.getAbnormalFeatures().getCurrentValue());
      triggerAIResult
          .setAnomalyDuration(valueAlgorithmResponse.getAbnormalFeatures().getAnomalyDuration());
      triggerAIResult.setBaseLine(valueAlgorithmResponse.getAbnormalFeatures().getBaseLine());
      triggerAIResult.setChangeRate(valueAlgorithmResponse.getAbnormalFeatures().getChangeRate());
      triggerAIResult.setMsg(valueAlgorithmResponse.getMsg());
    }
    return triggerAIResult;
  }
}
