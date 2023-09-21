/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.common.model.query.StatisticData;
import io.holoinsight.server.apm.common.model.query.StatisticDataList;
import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import org.elasticsearch.search.aggregations.AggregationBuilder;

import java.util.List;
import java.util.Map;

public interface MetricStorage extends ReadableStorage {

  MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions) throws Exception;

  List<String> querySchema(String metric);

  StatisticData billing(final String tenant, String serviceName, String serviceInstanceName,
      String endpointName, List<String> traceIds, int minTraceDuration, int maxTraceDuration,
      TraceState traceState, long start, long end, List<Tag> tags) throws Exception;

  StatisticDataList statistic(long startTime, long endTime, List<String> groups,
      List<AggregationBuilder> aggregations) throws Exception;

}
