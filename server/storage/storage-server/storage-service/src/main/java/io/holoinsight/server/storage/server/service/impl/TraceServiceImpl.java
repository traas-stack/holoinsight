/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.Pagination;
import io.holoinsight.server.storage.common.model.query.QueryOrder;
import io.holoinsight.server.storage.common.model.query.StatisticData;
import io.holoinsight.server.storage.common.model.query.TraceBrief;
import io.holoinsight.server.storage.common.model.specification.sw.Tag;
import io.holoinsight.server.storage.common.model.specification.sw.Trace;
import io.holoinsight.server.storage.common.model.specification.sw.TraceState;
import io.holoinsight.server.storage.engine.elasticsearch.model.SegmentEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.model.SpanEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.service.SegmentEsService;
import io.holoinsight.server.storage.engine.elasticsearch.service.SpanEsService;
import io.holoinsight.server.storage.server.service.TraceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author jiwliu
 * @version : TraceServiceImpl.java, v 0.1 2022年09月20日 16:48 xiangwanpeng Exp $
 */
@Service
@ConditionalOnFeature("trace")
public class TraceServiceImpl implements TraceService {

  @Autowired
  private SegmentEsService segmentEsService;

  @Resource
  @Qualifier("spanEsServiceImpl")
  private SpanEsService spanEsService;

  @Resource
  @Qualifier("spanTatrisServiceImpl")
  private SpanEsService spanTatrisService;

  @Override
  public TraceBrief queryBasicTraces(String tenant, String serviceName, String serviceInstanceName,
      String endpointName, List<String> traceIds, int minTraceDuration, int maxTraceDuration,
      TraceState traceState, QueryOrder queryOrder, Pagination paging, long start, long end,
      List<Tag> tags) throws Exception {
    return spanTatrisService.queryBasicTraces(tenant, serviceName, serviceInstanceName,
        endpointName, traceIds, minTraceDuration, maxTraceDuration, traceState, queryOrder, paging,
        start, end, tags);
  }

  @Override
  public Trace queryTrace(String traceId) throws Exception {
    return spanTatrisService.queryTrace(traceId);
  }

  @Override
  public void insert(List<SegmentEsDO> segments) throws Exception {
    segmentEsService.batchInsert(segments);
  }

  @Override
  public void insertSpans(List<SpanEsDO> spans) throws Exception {
    spanTatrisService.batchInsert(spans);
  }

  @Override
  public List<StatisticData> statisticTrace(long startTime, long endTime) throws Exception {
    return spanTatrisService.statisticTrace(startTime, endTime);
  }
}
