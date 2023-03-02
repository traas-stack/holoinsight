/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.web.impl;

import io.holoinsight.server.storage.web.TraceApi;
import io.holoinsight.server.storage.common.model.query.Pagination;
import io.holoinsight.server.storage.common.model.query.QueryOrder;
import io.holoinsight.server.storage.common.model.query.QueryTraceRequest;
import io.holoinsight.server.storage.common.model.query.TraceBrief;
import io.holoinsight.server.storage.common.model.specification.sw.Trace;
import io.holoinsight.server.storage.common.model.specification.sw.TraceState;
import io.holoinsight.server.storage.server.service.TraceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author jiwliu
 * @version : TraceApiController.java, v 0.1 2022年09月20日 16:11 xiangwanpeng Exp $
 */
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
    Trace trace = traceService.queryTrace(request.getTraceIds().get(0));
    return ResponseEntity.ok(trace);
  }
}
