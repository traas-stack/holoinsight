/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service;

import io.holoinsight.server.storage.common.model.query.Pagination;
import io.holoinsight.server.storage.common.model.query.QueryOrder;
import io.holoinsight.server.storage.common.model.query.StatisticData;
import io.holoinsight.server.storage.common.model.query.TraceBrief;
import io.holoinsight.server.storage.common.model.specification.sw.Tag;
import io.holoinsight.server.storage.common.model.specification.sw.Trace;
import io.holoinsight.server.storage.common.model.specification.sw.TraceState;
import io.holoinsight.server.storage.engine.elasticsearch.model.SegmentEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.model.SpanEsDO;

import java.io.IOException;
import java.util.List;

/**
 * @author jiwliu
 * @version : TraceService.java, v 0.1 2022年09月20日 16:46 xiangwanpeng Exp $
 */
public interface TraceService {

  TraceBrief queryBasicTraces(final String tenant, final String serviceName,
      final String serviceInstanceName, final String endpointName, final List<String> traceIds,
      final int minTraceDuration, int maxTraceDuration, final TraceState traceState,
      final QueryOrder queryOrder, final Pagination paging, final long start, final long end,
      final List<Tag> tags) throws Exception;

  Trace queryTrace(final String traceId) throws Exception;

  void insert(final List<SegmentEsDO> segments) throws Exception;

  void insertSpans(final List<SpanEsDO> spans) throws Exception;

  List<StatisticData> statisticTrace(long startTime, long endTime) throws Exception;
}
