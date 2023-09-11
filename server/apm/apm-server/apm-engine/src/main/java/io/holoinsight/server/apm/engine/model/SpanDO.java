/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.model;

import io.holoinsight.server.apm.common.model.specification.otel.KeyValue;
import io.holoinsight.server.apm.common.model.specification.otel.Resource;
import io.holoinsight.server.apm.common.model.specification.otel.Span;
import io.holoinsight.server.apm.common.model.storage.annotation.Column;
import io.holoinsight.server.apm.common.model.storage.annotation.FlatColumn;
import io.holoinsight.server.apm.common.model.storage.annotation.ModelAnnotation;
import io.holoinsight.server.apm.common.utils.GsonUtils;
import io.holoinsight.server.apm.common.utils.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.holoinsight.server.apm.engine.model.SpanDO.INDEX_NAME;

/**
 * @author jiwliu
 * @version : SpanEsDO.java, v 0.1 2022年11月01日 20:34 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ModelAnnotation(name = INDEX_NAME)
public class SpanDO extends RecordDO {

  private static final long serialVersionUID = -1058433983320362682L;

  public static final String INDEX_NAME = "holoinsight-span";

  public static final String TRACE_ID = "trace_id";
  public static final String NAME = "name";
  public static final String SPAN_ID = "span_id";
  public static final String PARENT_SPAN_ID = "parent_span_id";
  public static final String KIND = "kind";
  public static final String LINKS = "links";
  public static final String EVENTS = "events";
  public static final String REF_TYPE = "refType";
  public static final String TRACE_STATUS = "trace_status";
  public static final String TENANT = "tenant";

  public static final String SERVICE_NAME = "service.name";

  public static final String SERVICE_INSTANCE_ID = "service.instance.id";
  public static final String SERVICE_INSTANCE_NAME = "service.instance.name";

  public static final String NET_PEER_NAME = "net.peer.name";
  public static final String SPANLAYER = "spanLayer";

  public static final String SW8_SEGMENT_ID = "sw8.segment_id";

  public static final String ENDPOINT_NAME = "endpoint_name";
  public static final String START_TIME = "start_time";
  public static final String END_TIME = "end_time";
  public static final String LATENCY = "latency";
  public static final String IS_ERROR = "is_error";
  public static final String DATA_BINARY = "data_binary";
  public static final String TAGS = "tags";

  private static final String ATTRIBUTES_PREFIX = "attributes.";
  private static final String RESOURCE_PREFIX = "resource.";

  @Column(name = START_TIME)
  private long startTime;
  @Column(name = END_TIME)
  private long endTime;
  @Column(name = TRACE_ID)
  private String traceId;
  @Column(name = PARENT_SPAN_ID)
  private String parentSpanId;
  @Column(name = SPAN_ID)
  private String spanId;
  @Column(name = NAME)
  private String name;
  @Column(name = KIND)
  private String kind;
  @Column(name = LINKS)
  private String links;
  @Column(name = EVENTS)
  private String events;
  @Column(name = TRACE_STATUS)
  private int traceStatus;
  @Column(name = LATENCY)
  private int latency;
  @FlatColumn
  private Map<String, Object> tags;

  @Override
  public String indexName() {
    return INDEX_NAME;
  }

  public static SpanDO fromSpan(Span span, Resource resource) {
    SpanDO spanEsDO = new SpanDO();
    spanEsDO.setTimestamp(TimeUtils.unixNano2MS(span.getEndTimeUnixNano()));
    spanEsDO.setStartTime(TimeUtils.unixNano2MS(span.getStartTimeUnixNano()));
    spanEsDO.setEndTime(TimeUtils.unixNano2MS(span.getEndTimeUnixNano()));
    spanEsDO.setTraceId(span.getTraceId());
    spanEsDO.setParentSpanId(span.getParentSpanId());
    spanEsDO.setSpanId(span.getSpanId());
    spanEsDO.setName(span.getName());
    spanEsDO.setKind(span.getKind().name());
    spanEsDO.setLinks(GsonUtils.get().toJson(span.getLinks()));
    spanEsDO.setEvents(GsonUtils.get().toJson(span.getEvents()));
    spanEsDO.setTraceStatus(span.getStatus().getStatusCode().getCode());
    spanEsDO.setLatency(
        (int) (TimeUtils.unixNano2MS((span.getEndTimeUnixNano() - span.getStartTimeUnixNano()))));
    Map<String, Object> tags = new HashMap<>();
    spanEsDO.setTags(tags);
    List<KeyValue> spanAttrKvs = span.getAttributes();
    if (CollectionUtils.isNotEmpty(spanAttrKvs)) {
      for (KeyValue kv : spanAttrKvs) {
        tags.put(SpanDO.attributes(kv.getKey()), kv.getValue());
      }
    }
    if (resource != null && CollectionUtils.isNotEmpty(resource.getAttributes())) {
      for (KeyValue kv : resource.getAttributes()) {
        tags.put(SpanDO.resource(kv.getKey()), kv.getValue());
      }
    }
    return spanEsDO;
  }

  public static String attributes(String name) {
    return name == null ? null : (ATTRIBUTES_PREFIX + name);
  }

  public static String resource(String name) {
    return name == null ? null : (RESOURCE_PREFIX + name);
  }
}
