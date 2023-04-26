/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;


import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.compute.algorithm.AlgorithmConfig;
import io.holoinsight.server.home.alert.model.compute.algorithm.DatasourceConfig;
import io.holoinsight.server.home.alert.model.compute.algorithm.ExtendConfig;
import io.holoinsight.server.home.alert.model.compute.algorithm.ValueAlgorithmRequest;
import io.holoinsight.server.home.alert.model.compute.algorithm.ValueAlgorithmResponse;
import io.holoinsight.server.home.alert.model.function.FunctionConfigAIParam;
import io.holoinsight.server.home.alert.model.function.FunctionConfigParam;
import io.holoinsight.server.home.alert.model.function.FunctionLogic;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.emuns.FunctionEnum;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import io.holoinsight.server.home.facade.trigger.RuleConfig;
import io.holoinsight.server.home.facade.trigger.TriggerAIResult;
import io.holoinsight.server.home.facade.trigger.TriggerResult;
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
public class ValueUpAbnormalDetect implements FunctionLogic {
  private static final Logger LOGGER = LoggerFactory.getLogger(ValueUpAbnormalDetect.class);

  @Value("${holoinsight.alert.algorithm.url}")
  private String url;

  @Override
  public FunctionEnum getFunc() {
    return FunctionEnum.ValueUp;
  }

  @Override
  public TriggerResult invoke(DataResult dataResult, FunctionConfigParam functionConfigParam) {
    FunctionConfigAIParam functionConfigAIParam = (FunctionConfigAIParam) functionConfigParam;
    TriggerAIResult triggerAIResult = new TriggerAIResult();
    ValueAlgorithmRequest valueAlgorithmRequest = new ValueAlgorithmRequest();
    AlgorithmConfig algorithmConfig = new AlgorithmConfig();
    algorithmConfig.setAlgorithmType("value");
    algorithmConfig.setDetectType("up");
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
        .setExtendConfig(ExtendConfig.triggerConverter(dataResult, functionConfigAIParam));
    RuleConfig ruleConfig = functionConfigAIParam.getTrigger().getRuleConfig();
    if (ruleConfig == null) {
      ruleConfig = RuleConfig.defaultUpConfig(dataResult.getMetric());
    }
    valueAlgorithmRequest.setRuleConfig(ruleConfig);
    // Set the name of the algorithm interface
    String algoUrl = url + "/serving";
    // Call algorithm interface
    String abnormalResult = AlgorithmHttp.invokeAlgorithm(algoUrl,
        G.get().toJson(valueAlgorithmRequest), functionConfigParam.getTraceId());
    ValueAlgorithmResponse valueAlgorithmResponse =
        G.get().fromJson(abnormalResult, ValueAlgorithmResponse.class);
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
