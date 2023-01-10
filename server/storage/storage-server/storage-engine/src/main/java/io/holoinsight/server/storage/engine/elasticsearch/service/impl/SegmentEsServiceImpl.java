/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.constants.Const;
import io.holoinsight.server.storage.common.model.query.BasicTrace;
import io.holoinsight.server.storage.common.model.query.Pagination;
import io.holoinsight.server.storage.common.model.query.QueryOrder;
import io.holoinsight.server.storage.common.model.query.TraceBrief;
import io.holoinsight.server.storage.common.model.specification.sw.KeyValue;
import io.holoinsight.server.storage.common.model.specification.sw.LogEntity;
import io.holoinsight.server.storage.common.model.specification.sw.Ref;
import io.holoinsight.server.storage.common.model.specification.sw.RefType;
import io.holoinsight.server.storage.common.model.specification.sw.Span;
import io.holoinsight.server.storage.common.model.specification.sw.Tag;
import io.holoinsight.server.storage.common.model.specification.sw.Trace;
import io.holoinsight.server.storage.common.model.specification.sw.TraceState;

import io.holoinsight.server.storage.common.utils.BooleanUtils;
import io.holoinsight.server.storage.engine.elasticsearch.model.SegmentEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.service.RecordEsService;
import io.holoinsight.server.storage.engine.elasticsearch.service.SegmentEsService;
import io.holoinsight.server.storage.engine.elasticsearch.utils.EsGsonUtils;
import io.holoinsight.server.storage.grpc.trace.SegmentObject;

import com.google.common.base.Strings;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author jiwliu
 * @version : SegmentEsServiceImpl.java, v 0.1 2022年09月19日 14:21 wanpeng.xwp Exp $
 */
@ConditionalOnFeature("trace")
@Service
public class SegmentEsServiceImpl extends RecordEsService<SegmentEsDO> implements SegmentEsService {

  private static final int SEGMENT_QUERY_MAX_SIZE = 200;

  @Autowired
  private RestHighLevelClient esClient;

  @Override
  public TraceBrief queryBasicTraces(final String tenant, String serviceName,
      String serviceInstanceName, String endpointName, List<String> traceIds, int minTraceDuration,
      int maxTraceDuration, TraceState traceState, QueryOrder queryOrder, Pagination paging,
      long start, long end, List<Tag> tags) throws IOException {

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    searchSourceBuilder.query(boolQueryBuilder);

    if (StringUtils.isNotEmpty(tenant)) {
      boolQueryBuilder.must(new TermQueryBuilder(SegmentEsDO.TENANT, tenant));
    }
    if (start != 0 && end != 0) {
      boolQueryBuilder.must(new RangeQueryBuilder(SegmentEsDO.START_TIME).gte(start).lte(end));
    }

    if (minTraceDuration != 0 || maxTraceDuration != 0) {
      RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(SegmentEsDO.LATENCY);
      if (minTraceDuration != 0) {
        rangeQueryBuilder.gte(minTraceDuration);
      }
      if (maxTraceDuration != 0) {
        rangeQueryBuilder.lte(maxTraceDuration);
      }
      boolQueryBuilder.must(rangeQueryBuilder);
    }
    if (StringUtils.isNotEmpty(serviceName)) {
      boolQueryBuilder.must(new TermQueryBuilder(SegmentEsDO.SERVICE_NAME, serviceName));
    }
    if (StringUtils.isNotEmpty(serviceInstanceName)) {
      boolQueryBuilder
          .must(new TermQueryBuilder(SegmentEsDO.SERVICE_INSTANCE_NAME, serviceInstanceName));
    }
    if (!Strings.isNullOrEmpty(endpointName)) {
      boolQueryBuilder.must(new TermQueryBuilder(SegmentEsDO.ENDPOINT_NAME, endpointName));
    }
    if (CollectionUtils.isNotEmpty(traceIds)) {
      boolQueryBuilder.must(new TermsQueryBuilder(SegmentEsDO.TRACE_ID, traceIds));
    }
    switch (traceState) {
      case ERROR:
        boolQueryBuilder.must(new MatchQueryBuilder(SegmentEsDO.IS_ERROR, BooleanUtils.TRUE));
        break;
      case SUCCESS:
        boolQueryBuilder.must(new MatchQueryBuilder(SegmentEsDO.IS_ERROR, BooleanUtils.FALSE));
        break;
    }

    switch (queryOrder) {
      case BY_START_TIME:
        searchSourceBuilder.sort(SegmentEsDO.START_TIME, SortOrder.DESC);
        break;
      case BY_DURATION:
        searchSourceBuilder.sort(SegmentEsDO.LATENCY, SortOrder.DESC);
        break;
    }
    if (CollectionUtils.isNotEmpty(tags)) {
      BoolQueryBuilder tagMatchQuery = new BoolQueryBuilder();
      tags.forEach(
          tag -> tagMatchQuery.must(new TermQueryBuilder(SegmentEsDO.TAGS, tag.toString())));
      boolQueryBuilder.must(tagMatchQuery);
    }

    int limit = paging.getPageSize();
    int from = paging.getPageSize() * ((paging.getPageNum() == 0 ? 1 : paging.getPageNum()) - 1);
    searchSourceBuilder.from(from).size(limit);

    SearchRequest searchRequest =
        new SearchRequest(new String[] {SegmentEsDO.INDEX_NAME}, searchSourceBuilder);

    SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
    final TraceBrief traceBrief = new TraceBrief();
    for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
      String hitJson = hit.getSourceAsString();
      SegmentEsDO segmentEsDO = EsGsonUtils.esGson().fromJson(hitJson, SegmentEsDO.class);
      BasicTrace basicTrace = new BasicTrace();

      basicTrace.setSegmentId(segmentEsDO.getSegmentId());
      basicTrace.setStart(segmentEsDO.getStartTime());
      basicTrace.getEndpointNames().add(segmentEsDO.getEndpointName());
      basicTrace.setDuration(segmentEsDO.getLatency());
      basicTrace.setError(BooleanUtils.valueToBoolean(segmentEsDO.getIsError()));
      basicTrace.getTraceIds().add(segmentEsDO.getTraceId());
      traceBrief.getTraces().add(basicTrace);
    }

    return traceBrief;
  }

  @Override
  public Trace queryTrace(String traceId) throws IOException {
    Trace trace = new Trace();
    QueryBuilder queryBuilder = new TermQueryBuilder(SegmentEsDO.TRACE_ID, traceId);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(queryBuilder);
    searchSourceBuilder.size(SEGMENT_QUERY_MAX_SIZE);
    SearchRequest searchRequest =
        new SearchRequest(new String[] {SegmentEsDO.INDEX_NAME}, searchSourceBuilder);
    SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);
    List<SegmentEsDO> segmentRecords = new ArrayList<>();
    for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
      String hitJson = hit.getSourceAsString();
      SegmentEsDO segmentEsDO = EsGsonUtils.esGson().fromJson(hitJson, SegmentEsDO.class);
      segmentRecords.add(segmentEsDO);
    }

    if (!segmentRecords.isEmpty()) {
      for (SegmentEsDO segment : segmentRecords) {
        if (nonNull(segment)) {
          SegmentObject segmentObject = SegmentObject.parseFrom(segment.getDataBinary());
          trace.getSpans().addAll(buildSpanList(segmentObject));
        }
      }
    }

    List<Span> sortedSpans = new LinkedList<>();
    if (CollectionUtils.isNotEmpty(trace.getSpans())) {
      List<Span> rootSpans = findRoot(trace.getSpans());
      if (CollectionUtils.isNotEmpty(rootSpans)) {
        rootSpans.forEach(span -> {
          List<Span> childrenSpan = new ArrayList<>();
          childrenSpan.add(span);
          findChildren(trace.getSpans(), span, childrenSpan);
          sortedSpans.addAll(childrenSpan);
        });
      }
    }
    trace.getSpans().clear();
    trace.getSpans().addAll(sortedSpans);
    return trace;
  }

  private List<Span> buildSpanList(SegmentObject segmentObject) {
    List<Span> spans = new ArrayList<>();

    segmentObject.getSpansList().forEach(spanObject -> {
      Span span = new Span();
      span.setTraceId(segmentObject.getTraceId());
      span.setSegmentId(segmentObject.getTraceSegmentId());
      span.setStartTime(spanObject.getStartTime());
      span.setEndTime(spanObject.getEndTime());
      span.setError(spanObject.getIsError());
      span.setLayer(spanObject.getSpanLayer().name());
      span.setType(spanObject.getSpanType().name());

      String segmentSpanId =
          segmentObject.getTraceSegmentId() + Const.SEGMENT_SPAN_SPLIT + spanObject.getSpanId();
      span.setSpanId(segmentSpanId);
      span.setSegmentSpanId(segmentSpanId);

      String segmentParentSpanId = segmentObject.getTraceSegmentId() + Const.SEGMENT_SPAN_SPLIT
          + spanObject.getParentSpanId();
      span.setParentSpanId(segmentParentSpanId);
      span.setSegmentParentSpanId(segmentParentSpanId);

      span.setPeer(spanObject.getPeer());

      span.setEndpointName(spanObject.getOperationName());

      span.setServiceCode(segmentObject.getService());
      span.setServiceInstanceName(segmentObject.getServiceInstance());

      // TODO: 2022/9/20
      // span.setComponent(getComponentLibraryCatalogService().getComponentName(spanObject.getComponentId()));

      spanObject.getRefsList().forEach(reference -> {
        Ref ref = new Ref();
        ref.setTraceId(reference.getTraceId());
        ref.setParentSegmentId(reference.getParentTraceSegmentId());

        switch (reference.getRefType()) {
          case CrossThread:
            ref.setType(RefType.CROSS_THREAD);
            break;
          case CrossProcess:
            ref.setType(RefType.CROSS_PROCESS);
            break;
        }
        ref.setParentSpanId(String.valueOf(reference.getParentSpanId()));

        span.setSegmentParentSpanId(
            ref.getParentSegmentId() + Const.SEGMENT_SPAN_SPLIT + ref.getParentSpanId());

        span.getRefs().add(ref);
      });

      spanObject.getTagsList().forEach(tag -> {
        KeyValue keyValue = new KeyValue();
        keyValue.setKey(tag.getKey());
        keyValue.setValue(tag.getValue());
        span.getTags().add(keyValue);
      });

      spanObject.getLogsList().forEach(log -> {
        LogEntity logEntity = new LogEntity();
        logEntity.setTime(log.getTime());

        log.getDataList().forEach(data -> {
          KeyValue keyValue = new KeyValue();
          keyValue.setKey(data.getKey());
          keyValue.setValue(data.getValue());
          logEntity.getData().add(keyValue);
        });

        span.getLogs().add(logEntity);
      });

      spans.add(span);
    });

    return spans;
  }

  private List<Span> findRoot(List<Span> spans) {
    List<Span> rootSpans = new ArrayList<>();
    spans.forEach(span -> {
      String segmentParentSpanId = span.getSegmentParentSpanId();

      boolean hasParent = false;
      for (Span subSpan : spans) {
        if (segmentParentSpanId.equals(subSpan.getSegmentSpanId())) {
          hasParent = true;
          // if find parent, quick exit
          break;
        }
      }

      if (!hasParent && CollectionUtils.isEmpty(span.getRefs())) {
        span.setRoot(true);
        rootSpans.add(span);
      }
    });
    rootSpans.sort(Comparator.comparing(Span::getStartTime));
    return rootSpans;
  }

  private void findChildren(List<Span> spans, Span parentSpan, List<Span> childrenSpan) {
    spans.forEach(span -> {
      if (span.getSegmentParentSpanId().equals(parentSpan.getSegmentSpanId())) {
        childrenSpan.add(span);
        findChildren(spans, span, childrenSpan);
      }
    });
  }
}
