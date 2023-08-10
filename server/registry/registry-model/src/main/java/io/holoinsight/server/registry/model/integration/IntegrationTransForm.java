/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.registry.model.integration;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: TransForm.java, Date: 2023-05-12 Time: 13:52
 */
@Data
public class IntegrationTransForm {

  private String metricPrefix;
  private String metricFormat;

  private List<String> metricWhiteList;
  private Map<String, Map<String, String>> metricConfigs;
}
