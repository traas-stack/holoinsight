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
import io.holoinsight.server.apm.common.model.query.TraceTree;
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
import io.holoinsight.server.apm.engine.elasticsearch.utils.ApmGsonUtils;
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
      SpanDO spanEsDO = ApmGsonUtils.apmGson().fromJson(hitJson, SpanDO.class);
      BasicTrace basicTrace = new BasicTrace();
      basicTrace.setStart(spanEsDO.getStartTime());
      basicTrace.getServiceNames()
          .add(spanEsDO.getTags().get(SpanDO.resource(SpanDO.SERVICE_NAME)).toString());
      basicTrace.getServiceInstanceNames().add(spanEsDO.getTags()
          .getOrDefault(SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME), Const.UNKNOWN).toString());
      basicTrace.getEndpointNames().add(spanEsDO.getName());
      basicTrace.setDuration(spanEsDO.getLatency());
      basicTrace.setError(spanEsDO.getTraceStatus() == StatusCode.ERROR.getCode());
      basicTrace.getTraceIds().add(spanEsDO.getTraceId());
      traceBrief.getTraces().add(basicTrace);
    }
    return traceBrief;
  }

  /**
   *
   * @param tenant
   * @param start
   * @param end
   * @param traceId
   * @param tags
   * @return the span list, the front end needs to build a trace tree based on the relationship
   *         between spanId and parentSpanId
   * @throws IOException
   */
  @Override
  public Trace queryTrace(String tenant, long start, long end, String traceId, List<Tag> tags)
      throws IOException {
    Trace trace = new Trace();
    trace.setSpans(querySpan(tenant, start, end, traceId, tags));

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

  /**
   * First find the root node of the trace tree (there may be multiple root nodes), and then build
   * the trace tree from the root node
   * 
   * @param tenant
   * @param start
   * @param end
   * @param traceId
   * @param tags
   * @return the tree trace structure, the front end only needs to render, no need to build a tree
   *         relationship
   * @throws Exception
   */
  @Override
  public List<TraceTree> queryTraceTree(String tenant, long start, long end, String traceId,
      List<Tag> tags) throws Exception {
    List<TraceTree> result = new ArrayList<>();
    List<Span> spans = querySpan(tenant, start, end, traceId, tags);
    if (CollectionUtils.isNotEmpty(spans)) {
      List<Span> rootSpans = findRoot1(spans);
      if (CollectionUtils.isNotEmpty(rootSpans)) {
        rootSpans.forEach(span -> {
          TraceTree root = new TraceTree();
          root.setSpan(span);
          List<TraceTree> children = new ArrayList<>();
          root.setChildren(children);
          findChildren1(spans, span, root, children);
          result.add(root);
        });
      }
    }
    return result;
  }

  private List<Span> querySpan(String tenant, long start, long end, String traceId, List<Tag> tags)
      throws IOException {
    List<Span> spans = new ArrayList<>();

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

    searchSourceBuilder.query(buildQuery(tenant, null, null, null,
        Collections.singletonList(traceId), 0, 0, null, start, end, tags, this.timeSeriesField()));

    searchSourceBuilder.size(SPAN_QUERY_MAX_SIZE);
    SearchRequest searchRequest =
        new SearchRequest(new String[] {SpanDO.INDEX_NAME}, searchSourceBuilder);
    SearchResponse searchResponse = client().search(searchRequest, RequestOptions.DEFAULT);
    List<SpanDO> spanRecords = new ArrayList<>();
    for (org.elasticsearch.search.SearchHit hit : searchResponse.getHits().getHits()) {
      String hitJson = hit.getSourceAsString();
      SpanDO spanEsDO = ApmGsonUtils.apmGson().fromJson(hitJson, SpanDO.class);
      spanRecords.add(spanEsDO);
    }

    if (!spanRecords.isEmpty()) {
      for (SpanDO spanEsDO : spanRecords) {
        if (nonNull(spanEsDO)) {
          spans.add(buildSpan(spanEsDO));
        }
      }
    }
    return spans;
  }

  private Span buildSpan(SpanDO spanEsDO) {
    Span span = new Span();
    span.setTraceId(spanEsDO.getTraceId());
    span.setSpanId(spanEsDO.getSpanId());
    span.setParentSpanId(spanEsDO.getParentSpanId());
    span.setStartTime(spanEsDO.getStartTime());
    span.setEndTime(spanEsDO.getEndTime());
    span.setError(spanEsDO.getTraceStatus() == StatusCode.ERROR.getCode());
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
    Object spanLayer = spanEsDO.getTags().get(SpanDO.attributes(SpanDO.SPANLAYER));
    if (spanLayer != null) {
      span.setLayer(spanLayer.toString());
    }
    Object netPeerName = spanEsDO.getTags().get(SpanDO.attributes(SpanDO.NET_PEER_NAME));
    if (netPeerName != null) {
      span.setPeer(netPeerName.toString());
    }
    Object serviceInstanceName =
        spanEsDO.getTags().get(SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME));
    if (serviceInstanceName != null) {
      span.setServiceInstanceName(serviceInstanceName.toString());
    }

    span.setEndpointName(spanEsDO.getName());
    span.setServiceCode(spanEsDO.getTags().get(SpanDO.resource(SpanDO.SERVICE_NAME)).toString());

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
          new io.holoinsight.server.apm.common.model.specification.sw.KeyValue(tagk,
              tagv.toString());
      span.getTags().add(keyValue);
      // mesh span
      if (SpanDO.attributes(Const.MOSN_ATTR).equals(keyValue.getKey())
          && "true".equals(keyValue.getValue())) {
        span.setMesh(true);
      }
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

  /**
   * Find all the root nodes of the trace tree. Since sofatracer may report some special spans
   * (spanId==parentSpanId), special processing is required
   * 
   * @param spans
   * @return
   */
  private List<Span> findRoot1(List<Span> spans) {
    List<Span> rootSpans = new ArrayList<>();
    ListIterator<Span> iterator = spans.listIterator(spans.size());
    while (iterator.hasPrevious()) {
      Span span = iterator.previous();
      if (span.isMesh()) {
        continue;
      }
      String spanId = span.getSpanId();
      String parentSpanId = span.getParentSpanId();

      boolean hasParent = false;
      if (!StringUtils.isEmpty(parentSpanId)) {
        for (Span subSpan : spans) {
          if (!subSpan.isMesh() && subSpan.getSpanId().equals(parentSpanId)
              && !subSpan.equals(span)) {
            hasParent = true;
            // if find parent, quick exit
            break;
          }
        }
      }

      if (!hasParent) {
        // sofatracer mq/rpc server span(parentSpanId == spanId)
        if (spanId.equals(parentSpanId) && parentSpanId.contains(".")) {
          parentSpanId = parentSpanId.substring(0, parentSpanId.lastIndexOf("."));
          span.setParentSpanId(parentSpanId);
          iterator.remove();
          iterator.add(span);
          continue;
        }
        // rootSpan.parentSpanId == ""
        if (StringUtils.isEmpty(parentSpanId)) {
          span.setRoot(true);
          rootSpans.add(span);
        } else {
          // sofatracer may be missing span, supplement the missing span until the root span
          Span missingSpan = new Span();
          missingSpan.setSpanId(parentSpanId);
          missingSpan.setTraceId(span.getTraceId());
          missingSpan.setEndpointName(Const.NOT_APPLICABLE);
          missingSpan.setParentSpanId("");
          missingSpan.setType("");
          // sofatracer spanId -> parentSpanId: 0.1.1 -> 0.1
          if (parentSpanId.contains(".")) {
            missingSpan.setParentSpanId(parentSpanId.substring(0, parentSpanId.lastIndexOf(".")));
          }
          iterator.add(missingSpan);
        }
      }
    }

    rootSpans.sort(Comparator.comparing(Span::getStartTime));
    return rootSpans;
  }

  /**
   * Recursively build a trace tree starting from the root node
   * 
   * @param spans
   * @param parentSpan
   * @param children
   */
  private void findChildren1(List<Span> spans, Span parentSpan, TraceTree parentTree,
      List<TraceTree> children) {
    long parentStartTime = Long.MAX_VALUE;
    long parentEndTime = Long.MIN_VALUE;

    ListIterator<Span> iterator = spans.listIterator(spans.size());
    while (iterator.hasPrevious()) {
      Span span = iterator.previous();
      // find mesh span
      if (span.isMesh() && parentSpan.getSpanId().equals(span.getSpanId())
          && parentSpan.getParentSpanId().equals(span.getParentSpanId())
          && parentSpan.getType().equals(span.getType())) {
        parentTree.setMesh(span);
      }

      if (!span.isMesh() && !span.equals(parentSpan)
          && span.getParentSpanId().equals(parentSpan.getSpanId())) {
        TraceTree child = new TraceTree();
        child.setSpan(span);
        children.add(child);
        List<TraceTree> newChildren = new ArrayList<>();
        child.setChildren(newChildren);

        findChildren1(spans, span, child, newChildren);

        parentStartTime = Math.min(parentStartTime, span.getStartTime());
        parentEndTime = Math.max(parentEndTime, span.getEndTime());
      }
    }

    // The missing span needs to fill in the start and end time,
    // and the front-end drawing time axis depends on the time field
    if (Const.NOT_APPLICABLE.equals(parentSpan.getEndpointName())) {
      parentSpan.setStartTime(parentStartTime);
      parentSpan.setEndTime(parentEndTime);
    }
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
