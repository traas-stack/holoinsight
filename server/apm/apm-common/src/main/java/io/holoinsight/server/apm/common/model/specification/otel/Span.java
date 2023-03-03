/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.otel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author jiwliu
 * @version : Span.java, v 0.1 2022年10月29日 16:50 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Span {
  private String traceId;
  private String spanId;
  private String traceState;
  private String parentSpanId;
  private String name;
  private SpanKind kind;
  private long startTimeUnixNano;
  private long endTimeUnixNano;
  private List<KeyValue> attributes;
  private int droppedAttributesCount;
  private List<Event> events;
  private int droppedEventsCount;
  private List<Link> links;
  private int droppedLinksCount;
  private Status status;
}
