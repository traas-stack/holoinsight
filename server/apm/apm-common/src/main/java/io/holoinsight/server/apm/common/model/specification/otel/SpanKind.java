/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.otel;

/**
 * Type of {@link Span}. Can be used to specify additional relationships between spans in addition
 * to a parent/child relationship.
 *
 * @author jiwliu
 * @version : SpanKind.java, v 0.1 2022年10月29日 16:52 xiangwanpeng Exp $
 */
public enum SpanKind {
  /**
   * Default value. Indicates that the span is used internally.
   */
  INTERNAL,

  /**
   * Indicates that the span covers server-side handling of an RPC or other remote request.
   */
  SERVER,

  /**
   * Indicates that the span covers the client-side wrapper around an RPC or other remote request.
   */
  CLIENT,

  /**
   * Indicates that the span describes producer sending a message to a broker. Unlike client and
   * server, there is no direct critical path latency relationship between producer and consumer
   * spans.
   */
  PRODUCER,

  /**
   * Indicates that the span describes consumer receiving a message from a broker. Unlike client and
   * server, there is no direct critical path latency relationship between producer and consumer
   * spans.
   */
  CONSUMER,

  UNKNOWN;

  public static SpanKind fromProto(io.opentelemetry.proto.trace.v1.Span.SpanKind kind) {
    switch (kind) {
      case SPAN_KIND_INTERNAL:
        return INTERNAL;
      case SPAN_KIND_SERVER:
        return SERVER;
      case SPAN_KIND_CLIENT:
        return CLIENT;
      case SPAN_KIND_PRODUCER:
        return PRODUCER;
      case SPAN_KIND_CONSUMER:
        return CONSUMER;
      default:
        return UNKNOWN;
    }
  }
}
