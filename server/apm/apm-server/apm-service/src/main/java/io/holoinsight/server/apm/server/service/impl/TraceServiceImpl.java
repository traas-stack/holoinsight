/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.common.model.query.Pagination;
import io.holoinsight.server.apm.common.model.query.QueryOrder;
import io.holoinsight.server.apm.common.model.query.StatisticData;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.SpanStorage;
import io.holoinsight.server.apm.server.service.TraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

@Slf4j
public class TraceServiceImpl implements TraceService {

  @Autowired
  protected SpanStorage spanStorage;


  @Override
  public TraceBrief queryBasicTraces(String tenant, String serviceName, String serviceInstanceName,
      String endpointName, List<String> traceIds, int minTraceDuration, int maxTraceDuration,
      TraceState traceState, QueryOrder queryOrder, Pagination paging, long start, long end,
      List<Tag> tags) throws Exception {
    return spanStorage.queryBasicTraces(tenant, serviceName, serviceInstanceName, endpointName,
        traceIds, minTraceDuration, maxTraceDuration, traceState, queryOrder, paging, start, end,
        tags);
  }

  @Override
  public Trace queryTrace(String traceId) throws Exception {
    return spanStorage.queryTrace(traceId);
  }

  @Override
  public void insertSpans(List<SpanDO> spans) throws Exception {
    spanStorage.insert(spans);
  }

  @Override
  public StatisticData billing(String tenant, String serviceName, String serviceInstanceName,
      String endpointName, List<String> traceIds, int minTraceDuration, int maxTraceDuration,
      TraceState traceState, long start, long end, List<Tag> tags) throws Exception {
    return spanStorage.billing(tenant, serviceName, serviceInstanceName, endpointName, traceIds,
        minTraceDuration, maxTraceDuration, traceState, start, end, tags);
  }

  @Override
  public List<StatisticData> statistic(long startTime, long endTime) throws Exception {
    return spanStorage.statistic(startTime, endTime);
  }

}
