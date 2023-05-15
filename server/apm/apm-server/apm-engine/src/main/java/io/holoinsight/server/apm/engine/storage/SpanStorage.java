/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.storage;

import io.holoinsight.server.apm.common.model.query.Pagination;
import io.holoinsight.server.apm.common.model.query.QueryOrder;
import io.holoinsight.server.apm.common.model.query.StatisticData;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.engine.model.SpanDO;

import java.util.List;

/**
 * @author jiwliu
 * @version : SpanEsService.java, v 0.1 2022年09月29日 16:56 xiangwanpeng Exp $
 */
public interface SpanStorage extends WritableStorage<SpanDO>, ReadableStorage {

  TraceBrief queryBasicTraces(final String tenant, final String serviceName,
      final String serviceInstanceName, final String endpointName, final List<String> traceIds,
      final int minTraceDuration, int maxTraceDuration, final TraceState traceState,
      final QueryOrder queryOrder, final Pagination paging, final long start, final long end,
      final List<Tag> tags) throws Exception;

  Trace queryTrace(final String traceId) throws Exception;

  StatisticData billing(final String tenant, String serviceName, String serviceInstanceName,
      String endpointName, List<String> traceIds, int minTraceDuration, int maxTraceDuration,
      TraceState traceState, long start, long end, List<Tag> tags) throws Exception;

  List<StatisticData> statistic(long startTime, long endTime) throws Exception;
}
