/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.impl;

import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.common.model.query.QueryMetricRequest;
import io.holoinsight.server.apm.engine.postcal.MetricDefine;
import io.holoinsight.server.apm.server.service.MetricService;
import io.holoinsight.server.apm.web.MetricApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

@Slf4j
public class MetricApiController implements MetricApi {

  @Autowired
  private MetricService metricService;

  @Override
  public ResponseEntity<List<String>> listMetrics() throws IOException {
    List<String> metrics = metricService.listMetrics();
    return ResponseEntity.ok(metrics);
  }

  @Override
  public ResponseEntity<List<MetricDefine>> listMetricDefines() throws IOException {
    return ResponseEntity.ok(metricService.listMetricDefines());
  }

  @Override
  public ResponseEntity<MetricValues> queryMetricData(QueryMetricRequest request) throws Exception {
    MetricValues metricValues = metricService.queryMetric(request.getTenant(), request.getMetric(),
        request.getDuration(), request.getConditions());
    return ResponseEntity.ok(metricValues);
  }

  @Override
  public ResponseEntity<MetricDefine> queryMetricDefine(QueryMetricRequest metric) {
    return ResponseEntity.ok(metricService.getMetricDefine(metric.getMetric()));
  }

  @Override
  public ResponseEntity<List<String>> queryMetricSchema(QueryMetricRequest request)
      throws IOException {
    return ResponseEntity.ok(metricService.querySchema(request.getMetric()));
  }
}
