/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service;

import io.holoinsight.server.apm.common.model.query.Pagination;
import io.holoinsight.server.apm.common.model.query.QueryOrder;
import io.holoinsight.server.apm.common.model.query.StatisticData;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.engine.model.SpanDO;

import java.util.List;

public interface TraceService {

  TraceBrief queryBasicTraces(final String tenant, final String serviceName,
      final String serviceInstanceName, final String endpointName, final List<String> traceIds,
      final int minTraceDuration, int maxTraceDuration, final TraceState traceState,
      final QueryOrder queryOrder, final Pagination paging, final long start, final long end,
      final List<Tag> tags) throws Exception;

  Trace queryTrace(final String traceId) throws Exception;

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

  void insertSpans(final List<SpanDO> spans) throws Exception;

  /**
   * Query statistical trace data, which can be used to monitor the amount of trace data
   * 
   * @param startTime
   * @param endTime
   * @return
   * @throws Exception
   */
  List<StatisticData> statistic(long startTime, long endTime) throws Exception;
}
