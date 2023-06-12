/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import com.google.common.base.Strings;
import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.model.query.BasicTrace;
import io.holoinsight.server.apm.common.model.query.Pagination;
import io.holoinsight.server.apm.common.model.query.QueryOrder;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.specification.OtlpMappings;
import io.holoinsight.server.apm.common.model.specification.otel.Event;
import io.holoinsight.server.apm.common.model.specification.otel.KeyValue;
import io.holoinsight.server.apm.common.model.specification.otel.Link;
import io.holoinsight.server.apm.common.model.specification.otel.SpanKind;
import io.holoinsight.server.apm.common.model.specification.otel.StatusCode;
import io.holoinsight.server.apm.common.model.specification.sw.LogEntity;
import io.holoinsight.server.apm.common.model.specification.sw.Ref;
import io.holoinsight.server.apm.common.model.specification.sw.RefType;
import io.holoinsight.server.apm.common.model.specification.sw.Span;
import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.common.utils.GsonUtils;
import io.holoinsight.server.apm.engine.elasticsearch.utils.EsGsonUtils;
import io.holoinsight.server.apm.engine.model.RecordDO;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.SpanStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.*;

import static java.util.Objects.nonNull;

@Slf4j
public class SpanEsStorage extends RecordEsStorage<SpanDO> implements SpanStorage {

  private static final int SPAN_QUERY_MAX_SIZE = 2000;

  @Override
  public String timeSeriesField() {
    return RecordDO.TIMESTAMP;
  }

  @Override
  public TraceBrief queryBasicTraces(final String tenant, String serviceName,
      String serviceInstanceName, String endpointName, List<String> traceIds, int minTraceDuration,
      int maxTraceDuration, TraceState traceState, QueryOrder queryOrder, Pagination paging,
      long start, long end, List<Tag> tags) throws IOException {
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    searchSourceBuilder.query(buildQuery(tenant, serviceName, serviceInstanceName, endpointName,
        traceIds, minTraceDuration, maxTraceDuration, traceState, start, end, tags,
        this.timeSeriesField()));

    switch (queryOrder) {
      case BY_START_TIME:
        searchSourceBuilder.sort(SpanDO.START_TIME, SortOrder.DESC);
        break;
      case BY_DURATION:
        searchSourceBuilder.sort(SpanDO.LATENCY, SortOrder.DESC);
        break;
    }
    int limit = paging.getPageSize();
    int from = paging.getPageSize() * ((paging.getPageNum() == 0 ? 1 : paging.getPageNum()) - 1);
    searchSourceBuilder.from(from).size(limit);

    SearchRequest searchRequest =
        new SearchRequest(new String[] {SpanDO.INDEX_NAME}, searchSourceBuilder);

    SearchResponse searchResponse = client().search(searchRequest, RequestOptions.DEFAULT);
    final TraceBrief traceBrief = new TraceBrief();
    for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
      String hitJson = hit.getSourceAsString();
      SpanDO spanEsDO = EsGsonUtils.esGson().fromJson(hitJson, SpanDO.class);
      BasicTrace basicTrace = new BasicTrace();
      basicTrace.setStart(spanEsDO.getStartTime());
      basicTrace.getServiceNames()
          .add(spanEsDO.getTags().get(SpanDO.resource(SpanDO.SERVICE_NAME)));
      basicTrace.getServiceInstanceNames().add(spanEsDO.getTags()
          .getOrDefault(SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME), Const.UNKNOWN));
      basicTrace.getEndpointNames().add(spanEsDO.getName());
      basicTrace.setDuration(spanEsDO.getLatency());
      basicTrace.setError(spanEsDO.getTraceStatus() == StatusCode.ERROR.getCode());
      basicTrace.getTraceIds().add(spanEsDO.getTraceId());
      traceBrief.getTraces().add(basicTrace);
    }
    return traceBrief;
  }

  @Override
  public Trace queryTrace(String tenant, long start, long end, String traceId) throws IOException {
    Trace trace = new Trace();

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    searchSourceBuilder.query(buildQuery(tenant, null, null, null,
        Collections.singletonList(traceId), 0, 0, null, start, end, null, this.timeSeriesField()));

    searchSourceBuilder.size(SPAN_QUERY_MAX_SIZE);
    SearchRequest searchRequest =
        new SearchRequest(new String[] {SpanDO.INDEX_NAME}, searchSourceBuilder);
    SearchResponse searchResponse = client().search(searchRequest, RequestOptions.DEFAULT);
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

  public static BoolQueryBuilder buildQuery(final String tenant, String serviceName,
      String serviceInstanceName, String endpointName, List<String> traceIds, int minTraceDuration,
      int maxTraceDuration, TraceState traceState, long start, long end, List<Tag> tags,
      String timeField) {
    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();

    if (StringUtils.isNotEmpty(tenant)) {
      boolQueryBuilder.must(new TermQueryBuilder(SpanDO.resource(SpanDO.TENANT), tenant));
    }
    if (start != 0 && end != 0) {
      boolQueryBuilder.must(new RangeQueryBuilder(timeField).gte(start).lte(end));
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

    if (traceState != null) {
      switch (traceState) {
        case ERROR:
          boolQueryBuilder
              .must(new TermQueryBuilder(SpanDO.TRACE_STATUS, StatusCode.ERROR.getCode()));
          break;
        case SUCCESS:
          boolQueryBuilder.must(new TermQueryBuilder(SpanDO.TRACE_STATUS, StatusCode.OK.getCode()));
          break;
      }
    }


    if (CollectionUtils.isNotEmpty(tags)) {
      BoolQueryBuilder tagMatchQuery = new BoolQueryBuilder();
      tags.forEach(tag -> tagMatchQuery.must(new TermQueryBuilder(
          OtlpMappings.toOtlp(SpanDO.INDEX_NAME, tag.getKey()), tag.getValue())));
      boolQueryBuilder.must(tagMatchQuery);
    }
    return boolQueryBuilder;
  }
}
