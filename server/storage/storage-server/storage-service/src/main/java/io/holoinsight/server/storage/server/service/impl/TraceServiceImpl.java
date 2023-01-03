/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.Pagination;
import io.holoinsight.server.storage.common.model.query.QueryOrder;
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
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author jiwliu
 * @version : TraceServiceImpl.java, v 0.1 2022年09月20日 16:48 wanpeng.xwp Exp $
 */
@Service
@ConditionalOnFeature("trace")
public class TraceServiceImpl implements TraceService {

    @Autowired
    private SegmentEsService segmentEsService;

    @Autowired
    private SpanEsService spanEsService;

    @Override
    public TraceBrief queryBasicTraces(String tenant, String serviceName, String serviceInstanceName,
                                       String endpointName, List<String> traceIds, int minTraceDuration, int maxTraceDuration,
                                       TraceState traceState, QueryOrder queryOrder, Pagination paging,
                                       long start, long end, List<Tag> tags) throws IOException {
        return spanEsService.queryBasicTraces(tenant, serviceName, serviceInstanceName, endpointName, traceIds, minTraceDuration,
                maxTraceDuration, traceState, queryOrder, paging, start, end, tags);
    }

    @Override
    public Trace queryTrace(String traceId) throws IOException {
        return spanEsService.queryTrace(traceId);
    }

    @Override
    public void insert(List<SegmentEsDO> segments) throws IOException {
        segmentEsService.batchInsert(segments);
    }

    @Override
    public void insertSpans(List<SpanEsDO> spans) throws IOException {
        spanEsService.batchInsert(spans);
    }
}