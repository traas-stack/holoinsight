/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import com.google.common.base.Strings;
import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.model.query.*;
import io.holoinsight.server.apm.common.model.specification.OtlpMappings;
import io.holoinsight.server.apm.common.model.specification.otel.KeyValue;
import io.holoinsight.server.apm.common.model.specification.otel.*;
import io.holoinsight.server.apm.common.model.specification.sw.Span;
import io.holoinsight.server.apm.common.model.specification.sw.*;
import io.holoinsight.server.apm.common.utils.GsonUtils;
import io.holoinsight.server.apm.engine.elasticsearch.utils.EsGsonUtils;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.SpanStorage;
import io.opentelemetry.proto.trace.v1.Status;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.ParsedCardinality;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.*;

import static java.util.Objects.nonNull;

/**
 * @author jiwliu
 * @version : spanEsStorage.java, v 0.1 2022年09月19日 14:21 xiangwanpeng Exp $
 */
@Slf4j
public class SpanEsStorage extends RecordEsStorage<SpanDO> implements SpanStorage {

  private static final int SPAN_QUERY_MAX_SIZE = 2000;

  @Override
  public TraceBrief queryBasicTraces(final String tenant, String serviceName,
      String serviceInstanceName, String endpointName, List<String> traceIds, int minTraceDuration,
      int maxTraceDuration, TraceState traceState, QueryOrder queryOrder, Pagination paging,
      long start, long end, List<Tag> tags) throws IOException {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    searchSourceBuilder.query(boolQueryBuilder);

    if (StringUtils.isNotEmpty(tenant)) {
      boolQueryBuilder.must(new TermQueryBuilder(SpanDO.resource(SpanDO.TENANT), tenant));
    }
    if (start != 0 && end != 0) {
      boolQueryBuilder.must(new RangeQueryBuilder(SpanDO.START_TIME).gte(start).lte(end));
    }

    if (minTraceDuration != 0 || maxTraceDuration != 0) {
      RangeQueryBuilder rangeQueryBuilder = new RangeQueryBuilder(SpanDO.LATENCY);
      if (minTraceDuration != 0) {
        rangeQueryBuilder.gte(minTraceDuration);
      }
      if (maxTraceDuration != 0) {
        rangeQueryBuilder.lte(maxTraceDuration);
      }
      boolQueryBuilder.must(rangeQueryBuilder);
    }
    if (StringUtils.isNotEmpty(serviceName)) {
      boolQueryBuilder
          .must(new TermQueryBuilder(SpanDO.resource(SpanDO.SERVICE_NAME), serviceName));
    }
    if (StringUtils.isNotEmpty(serviceInstanceName)) {
      boolQueryBuilder.must(
          new TermQueryBuilder(SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME), serviceInstanceName));
    }
    if (!Strings.isNullOrEmpty(endpointName)) {
      boolQueryBuilder.must(new TermQueryBuilder(SpanDO.NAME, endpointName));
    }
    if (CollectionUtils.isNotEmpty(traceIds)) {
      boolQueryBuilder.must(new TermsQueryBuilder(SpanDO.TRACE_ID, traceIds));
    }
    switch (traceState) {
      case ERROR:
        boolQueryBuilder
            .must(new MatchQueryBuilder(SpanDO.TRACE_STATUS, StatusCode.ERROR.getCode()));
        break;
      case SUCCESS:
        boolQueryBuilder.must(new MatchQueryBuilder(SpanDO.TRACE_STATUS, StatusCode.OK.getCode()));
        break;
    }

    switch (queryOrder) {
      case BY_START_TIME:
        searchSourceBuilder.sort(SpanDO.START_TIME, SortOrder.DESC);
        break;
      case BY_DURATION:
        searchSourceBuilder.sort(SpanDO.LATENCY, SortOrder.DESC);
        break;
    }
    if (CollectionUtils.isNotEmpty(tags)) {
      BoolQueryBuilder tagMatchQuery = new BoolQueryBuilder();
      tags.forEach(tag -> tagMatchQuery
          .must(new TermQueryBuilder(OtlpMappings.sw2Otlp(tag.getKey()), tag.getValue())));
      boolQueryBuilder.must(tagMatchQuery);
    }

    int limit = paging.getPageSize();
    int from = paging.getPageSize() * ((paging.getPageNum() == 0 ? 1 : paging.getPageNum()) - 1);
    searchSourceBuilder.from(from).size(limit);

    SearchRequest searchRequest =
        new SearchRequest(new String[] {SpanDO.INDEX_NAME}, searchSourceBuilder);

    SearchResponse searchResponse = esClient().search(searchRequest, RequestOptions.DEFAULT);
    final TraceBrief traceBrief = new TraceBrief();
    for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
      String hitJson = hit.getSourceAsString();
      SpanDO spanEsDO = EsGsonUtils.esGson().fromJson(hitJson, SpanDO.class);
      BasicTrace basicTrace = new BasicTrace();
      basicTrace.setStart(spanEsDO.getStartTime());
      basicTrace.getServiceNames()
          .add(spanEsDO.getTags().get(SpanDO.resource(SpanDO.SERVICE_NAME)));
      basicTrace.getServiceInstanceNames()
          .add(spanEsDO.getTags().get(SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME)));
      basicTrace.getEndpointNames().add(spanEsDO.getName());
      basicTrace.setDuration(spanEsDO.getLatency());
      basicTrace.setError(spanEsDO.getTraceStatus() == StatusCode.ERROR.getCode());
      basicTrace.getTraceIds().add(spanEsDO.getTraceId());
      traceBrief.getTraces().add(basicTrace);
    }
    return traceBrief;
  }

  @Override
  public Trace queryTrace(String traceId) throws IOException {
    Trace trace = new Trace();
    QueryBuilder queryBuilder = new TermQueryBuilder(SpanDO.TRACE_ID, traceId);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.query(queryBuilder);
    searchSourceBuilder.size(SPAN_QUERY_MAX_SIZE);
    SearchRequest searchRequest =
        new SearchRequest(new String[] {SpanDO.INDEX_NAME}, searchSourceBuilder);
    SearchResponse searchResponse = esClient().search(searchRequest, RequestOptions.DEFAULT);
    List<SpanDO> spanRecords = new ArrayList<>();
    for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
      String hitJson = hit.getSourceAsString();
      SpanDO spanEsDO = EsGsonUtils.esGson().fromJson(hitJson, SpanDO.class);
      spanRecords.add(spanEsDO);
    }

    if (!spanRecords.isEmpty()) {
      for (SpanDO spanEsDO : spanRecords) {
        if (nonNull(spanEsDO)) {
          trace.getSpans().add(buildSpan(spanEsDO));
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
    sortedSpans.forEach(span -> {
      if (StringUtils.isEmpty(span.getParentSpanId())
          && CollectionUtils.isNotEmpty(span.getRefs())) {
        span.setParentSpanId(span.getRefs().get(0).getParentSpanId());
      }
    });
    trace.getSpans().addAll(sortedSpans);
    return trace;
  }

  @Override
  public List<StatisticData> statisticTrace(long startTime, long endTime) throws IOException {
    List<StatisticData> result = new ArrayList<>();

    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.rangeQuery(SpanDO.START_TIME).gte(startTime).lte(endTime));

    TermsAggregationBuilder aggregationBuilder =
        AggregationBuilders.terms(SpanDO.attributes(Const.APPID))
            .field(SpanDO
                .attributes(Const.APPID))
            .subAggregation(
                AggregationBuilders.terms(SpanDO.attributes(Const.ENVID))
                    .field(SpanDO.attributes(Const.ENVID))
                    .subAggregation(AggregationBuilders.cardinality("service_count")
                        .field(SpanDO.resource(SpanDO.SERVICE_NAME)))
                    .subAggregation(
                        AggregationBuilders.cardinality("trace_count").field(SpanDO.TRACE_ID))
                    .subAggregation(AggregationBuilders
                        .filter(SpanDO.TRACE_STATUS,
                            QueryBuilders.termQuery(SpanDO.TRACE_STATUS,
                                Status.StatusCode.STATUS_CODE_ERROR_VALUE))
                        .subAggregation(
                            AggregationBuilders.cardinality("error_count").field(SpanDO.TRACE_ID)))
                    .subAggregation(AggregationBuilders
                        .filter(SpanDO.attributes(Const.ISENTRY),
                            QueryBuilders.boolQuery()
                                .must(QueryBuilders.termQuery(SpanDO.attributes(Const.ISENTRY),
                                    "true"))
                                .mustNot(QueryBuilders.termQuery(SpanDO.NAME, "GET:/")))
                        .subAggregation(
                            AggregationBuilders.cardinality("entry_count").field(SpanDO.NAME)))
                    .subAggregation(AggregationBuilders.avg("avg_latency").field(SpanDO.LATENCY))
                    .executionHint("map").collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST)
                    .size(1000));

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(aggregationBuilder);

    SearchRequest searchRequest = new SearchRequest(SpanDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = esClient().search(searchRequest, RequestOptions.DEFAULT);

    Terms terms = response.getAggregations().get(SpanDO.attributes(Const.APPID));
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String appId = bucket.getKey().toString();
      Terms envTerms = bucket.getAggregations().get(SpanDO.attributes(Const.ENVID));
      for (Terms.Bucket envBucket : envTerms.getBuckets()) {
        String envId = envBucket.getKey().toString();

        ParsedCardinality serviceTerm = envBucket.getAggregations().get("service_count");
        long serviceCount = serviceTerm.getValue();

        ParsedCardinality traceTerm = envBucket.getAggregations().get("trace_count");
        long traceCount = traceTerm.getValue();

        Filter errFilter = envBucket.getAggregations().get(SpanDO.TRACE_STATUS);
        ParsedCardinality errorTerm = errFilter.getAggregations().get("error_count");
        int errorCount = (int) errorTerm.getValue();

        Filter entryFilter = envBucket.getAggregations().get(SpanDO.attributes(Const.ISENTRY));
        ParsedCardinality entryTerm = entryFilter.getAggregations().get("entry_count");
        int entryCount = (int) entryTerm.getValue();

        Avg avgLatency = envBucket.getAggregations().get("avg_latency");
        double latency = Double.valueOf(avgLatency.getValue());

        StatisticData statisticData = new StatisticData();
        statisticData.setAppId(appId);
        statisticData.setEnvId(envId);
        statisticData.setServiceCount(serviceCount);
        statisticData.setTraceCount(traceCount);
        statisticData.setEntryCount(entryCount);
        statisticData.setAvgLatency(latency);
        statisticData.setSuccessRate(((double) (traceCount - errorCount) / traceCount) * 100);

        result.add(statisticData);
      }
    }
    return result;
  }


  private Span buildSpan(SpanDO spanEsDO) {
    Span span = new Span();
    span.setTraceId(spanEsDO.getTraceId());
    span.setSpanId(spanEsDO.getSpanId());
    span.setParentSpanId(spanEsDO.getParentSpanId());
    span.setStartTime(spanEsDO.getStartTime());
    span.setEndTime(spanEsDO.getEndTime());
    span.setError(spanEsDO.getTraceStatus() == StatusCode.ERROR.getCode());
    span.setLayer(spanEsDO.getTags().get(SpanDO.attributes(SpanDO.SPANLAYER)));
    String kind = spanEsDO.getKind();
    if (StringUtils.equals(kind, SpanKind.SERVER.name())
        || StringUtils.equals(kind, SpanKind.CONSUMER.name())) {
      span.setType("Entry");
    } else if (StringUtils.equals(kind, SpanKind.CLIENT.name())
        || StringUtils.equals(kind, SpanKind.PRODUCER.name())) {
      span.setType("Exit");
    } else {
      span.setType("Local");
    }

    span.setPeer(spanEsDO.getTags().get(SpanDO.attributes(SpanDO.NET_PEER_NAME)));

    span.setEndpointName(spanEsDO.getName());

    span.setServiceCode(spanEsDO.getTags().get(SpanDO.resource(SpanDO.SERVICE_NAME)));
    span.setServiceInstanceName(
        spanEsDO.getTags().get(SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME)));

    // TODO: 2022/9/20
    // span.setComponent(getComponentLibraryCatalogService().getComponentName(spanObject.getComponentId()));
    String linksJson = spanEsDO.getLinks();
    List<Link> links = GsonUtils.toList(linksJson, Link.class);
    if (CollectionUtils.isNotEmpty(links)) {
      links.forEach(link -> {
        Ref ref = new Ref();
        ref.setTraceId(link.getTraceId());
        Map<String, String> linkAttributeMap = new HashMap<>();
        List<KeyValue> linkAttributes = link.getAttributes();
        if (CollectionUtils.isNotEmpty(linkAttributes)) {
          linkAttributes.forEach(linkAttribute -> linkAttributeMap.put(linkAttribute.getKey(),
              String.valueOf(linkAttribute.getValue())));
        }
        switch (linkAttributeMap.get(SpanDO.REF_TYPE)) {
          case "CrossThread":
            ref.setType(RefType.CROSS_THREAD);
            break;
          case "CrossProcess":
            ref.setType(RefType.CROSS_PROCESS);
            break;
        }
        ref.setParentSpanId(link.getSpanId());
        span.getRefs().add(ref);
      });
    }

    spanEsDO.getTags().forEach((tagk, tagv) -> {
      io.holoinsight.server.apm.common.model.specification.sw.KeyValue keyValue =
          new io.holoinsight.server.apm.common.model.specification.sw.KeyValue(tagk, tagv);
      span.getTags().add(keyValue);
    });
    Collections.sort(span.getTags(), (o1, o2) -> StringUtils.compare(o1.getKey(), o2.getKey()));

    String eventsJson = spanEsDO.getEvents();
    List<Event> events = GsonUtils.toList(eventsJson, Event.class);
    if (CollectionUtils.isNotEmpty(events)) {
      events.forEach(event -> {
        LogEntity logEntity = new LogEntity();
        logEntity.setTime(event.getTimeUnixNano() / 1000000);
        event.getAttributes().forEach(kv -> {
          io.holoinsight.server.apm.common.model.specification.sw.KeyValue keyValue =
              new io.holoinsight.server.apm.common.model.specification.sw.KeyValue(kv.getKey(),
                  String.valueOf(kv.getValue()));
          logEntity.getData().add(keyValue);
        });
        span.getLogs().add(logEntity);
      });
    }
    return span;
  }

  private List<Span> findRoot(List<Span> spans) {
    List<Span> rootSpans = new ArrayList<>();
    spans.forEach(span -> {
      String parentSpanId = span.getParentSpanId();

      boolean hasParent = false;
      for (Span subSpan : spans) {
        if (parentSpanId.equals(subSpan.getSpanId())
            || CollectionUtils.isNotEmpty(span.getRefs())) {
          hasParent = true;
          // if find parent, quick exit
          break;
        }
      }

      if (!hasParent) {
        span.setRoot(true);
        rootSpans.add(span);
      }
    });
    rootSpans.sort(Comparator.comparing(Span::getStartTime));
    return rootSpans;
  }

  private void findChildren(List<Span> spans, Span parentSpan, List<Span> childrenSpan) {
    spans.forEach(span -> {
      if (span.getParentSpanId().equals(parentSpan.getSpanId())) {
        childrenSpan.add(span);
        findChildren(spans, span, childrenSpan);
      }
    });
  }
}
