/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.impl;

import io.holoinsight.server.apm.common.model.query.Pagination;
import io.holoinsight.server.apm.common.model.query.QueryOrder;
import io.holoinsight.server.apm.common.model.query.QueryTraceRequest;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.query.TraceTree;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.server.service.TraceService;
import io.holoinsight.server.apm.web.TraceApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
public class TraceApiController implements TraceApi {

  @Autowired
  private TraceService traceService;

  @Override
  public ResponseEntity<TraceBrief> queryBasicTraces(QueryTraceRequest request) throws Exception {
    long start = 0;
    long end = 0;
    List<String> traceIds = Collections.EMPTY_LIST;

    if (CollectionUtils.isNotEmpty(request.getTraceIds())) {
      traceIds = request.getTraceIds();
    }
    if (nonNull(request.getDuration())) {
      start = request.getDuration().getStart();
      end = request.getDuration().getEnd();
    }
    if (CollectionUtils.isEmpty(traceIds) && start == 0 && end == 0) {
      throw new IllegalArgumentException(
          "The condition must contains either queryDuration or traceIds.");
    }

    int minDuration = request.getMinTraceDuration();
    int maxDuration = request.getMaxTraceDuration();
    String endpointName = request.getEndpointName();
    TraceState traceState = request.getTraceState();
    QueryOrder queryOrder = request.getQueryOrder();
    Pagination pagination = request.getPaging();
    TraceBrief traceBrief =
        traceService.queryBasicTraces(request.getTenant(), request.getServiceName(),
            request.getServiceInstanceName(), endpointName, traceIds, minDuration, maxDuration,
            traceState, queryOrder, pagination, start, end, request.getTags());
    return ResponseEntity.ok(traceBrief);
  }

  @Override
  public ResponseEntity<Trace> queryTrace(QueryTraceRequest request) throws Exception {
    if (CollectionUtils.isEmpty(request.getTraceIds())) {
      throw new IllegalArgumentException("The condition must contains traceIds.");
    }
    List<String> traceIds = request.getTraceIds();
    long start = 0;
    long end = 0;
    if (nonNull(request.getDuration())) {
      start = request.getDuration().getStart();
      end = request.getDuration().getEnd();
    }
    Trace trace = traceService.queryTrace(request.getTenant(), start, end, traceIds.get(0),
        request.getTags());
    return ResponseEntity.ok(trace);
  }

  @Override
  public ResponseEntity<List<TraceTree>> queryTraceTree(QueryTraceRequest request)
      throws Exception {
    if (CollectionUtils.isEmpty(request.getTraceIds())) {
      throw new IllegalArgumentException("The condition must contains traceIds.");
    }
    List<String> traceIds = request.getTraceIds();
    long start = 0;
    long end = 0;
    if (nonNull(request.getDuration())) {
      start = request.getDuration().getStart();
      end = request.getDuration().getEnd();
    }
    List<TraceTree> trace = traceService.queryTraceTree(request.getTenant(), start, end,
        traceIds.get(0), request.getTags());
    return ResponseEntity.ok(trace);
  }
}
