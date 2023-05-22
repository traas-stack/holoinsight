/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.impl;

import io.holoinsight.server.apm.common.model.query.*;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.engine.postcal.MetricDefine;
import io.holoinsight.server.apm.server.service.MetricService;
import io.holoinsight.server.apm.web.MetricApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

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

  @Override
  public ResponseEntity<StatisticData> billing(QueryTraceRequest request) throws Exception {
    long start = 0;
    long end = 0;
    List<String> traceIds = Collections.EMPTY_LIST;

    if (CollectionUtils.isNotEmpty(request.getTraceIds())) {
      traceIds = request.getTraceIds();
    } else if (nonNull(request.getDuration())) {
      start = request.getDuration().getStart();
      end = request.getDuration().getEnd();
    } else {
      throw new IllegalArgumentException(
          "The condition must contains either queryDuration or traceId.");
    }

    int minDuration = request.getMinTraceDuration();
    int maxDuration = request.getMaxTraceDuration();
    String endpointName = request.getEndpointName();
    TraceState traceState = request.getTraceState();
    StatisticData statisticData = metricService.billing(request.getTenant(),
        request.getServiceName(), request.getServiceInstanceName(), endpointName, traceIds,
        minDuration, maxDuration, traceState, start, end, request.getTags());
    return ResponseEntity.ok(statisticData);
  }

  @Override
  public ResponseEntity<StatisticDataList> statistic(StatisticRequest request) throws Exception {
    return ResponseEntity.ok(
        metricService.statistic(request.getStart(), request.getEnd(), request.getGroups(), null));
  }

}
