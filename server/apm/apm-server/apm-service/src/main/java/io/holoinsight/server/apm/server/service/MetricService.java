/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.common.model.query.StatisticData;
import io.holoinsight.server.apm.common.model.query.StatisticDataList;
import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.engine.postcal.MetricDefine;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.util.List;
import java.util.Map;

public interface MetricService {

  List<String> listMetrics();

  List<MetricDefine> listMetricDefines();

  MetricDefine getMetricDefine(String metric);

  MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions) throws Exception;

  List<String> querySchema(String metric);

  /**
   * billing specified resource ranges by certain conditions
   *
   * @param tenant
   * @param serviceName
   * @param serviceInstanceName
   * @param endpointName
   * @param traceIds
   * @param minTraceDuration
   * @param maxTraceDuration
   * @param traceState
   * @param start
   * @param end
   * @param tags
   * @return
   * @throws Exception
   */
  StatisticData billing(String tenant, String serviceName, String serviceInstanceName,
      String endpointName, List<String> traceIds, int minTraceDuration, int maxTraceDuration,
      TraceState traceState, long start, long end, List<Tag> tags) throws Exception;

  /**
   * Query statistical trace data, which can be used to monitor the amount of trace data
   *
   * @param startTime
   * @param endTime
   * @return
   * @throws Exception
   */
  StatisticDataList statistic(long startTime, long endTime, List<String> groups,
      List<AggregationBuilder> aggregations) throws Exception;
}
