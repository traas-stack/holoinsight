/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.extension.ceresdbx;

import io.holoinsight.server.extension.MetricMeterService;
import io.holoinsight.server.extension.model.Table;
import io.holoinsight.server.extension.model.WriteMetricsParam;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: MetricMeterServiceImpl.java, Date: 2024-01-25 Time: 20:04
 */

@Slf4j
public class MetricMeterServiceImpl implements MetricMeterService {
  @Override
  public void meter(WriteMetricsParam writeMetricsParam) {

  }

  @Override
  public void meter(String tenant, Table table) {

  }

  @Override
  public void meter(Map<String, String> tags, String tenant, String metricName) {

  }

  @Override
  public Map<String, String> keyGen(String tenant, String name, Map<String, String> tags) {
    return null;
  }
}
