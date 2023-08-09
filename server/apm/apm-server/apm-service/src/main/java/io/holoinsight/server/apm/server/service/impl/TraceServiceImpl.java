/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.server.service.impl;

import io.holoinsight.server.apm.common.model.query.Pagination;
import io.holoinsight.server.apm.common.model.query.QueryOrder;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.query.TraceTree;
import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.SpanStorage;
import io.holoinsight.server.apm.server.service.TraceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

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
  public Trace queryTrace(String tenant, long start, long end, String traceId, List<Tag> tags)
      throws Exception {
    return spanStorage.queryTrace(tenant, start, end, traceId, tags);
  }

  @Override
  public List<TraceTree> queryTraceTree(String tenant, long start, long end, String traceId,
      List<Tag> tags) throws Exception {
    return spanStorage.queryTraceTree(tenant, start, end, traceId, tags);
  }

  @Override
  public void insertSpans(List<SpanDO> spans) throws Exception {
    spanStorage.insert(spans);
  }
}
