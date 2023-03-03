/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author jiwliu
 * @version : TraceService.java, v 0.1 2022年09月20日 16:46 xiangwanpeng Exp $
 */
public interface MetricService {

  List<String> listMetrics();

  MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions) throws IOException;

  List<String> querySchema(String metric);
}
