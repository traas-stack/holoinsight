/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author jiwliu
 * @version : MetricEsService.java, v 0.1 2022年09月29日 16:58 xiangwanpeng Exp $
 */
public interface MetricStorage {

  List<String> listMetrics();

  MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions, List<String> groups) throws Exception;

  List<String> querySchema(String metric);

}
