/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute.algorithm;

import io.holoinsight.server.home.facade.trigger.RuleConfig;
import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/10/11 9:51 下午
 */
@Data
public class ValueAlgorithmRequest {

  private AlgorithmConfig algorithmConfig;

  private DatasourceConfig datasourceConfig;

  private Long detectTime;

  private ExtendConfig extendConfig;

  private String taskId;

  private RuleConfig ruleConfig;
}
