/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

/**
 * Reserved keys of the span. The backend analysis the metrics according the existed tags.
 */
public class SpanTags {
  public static final String HTTP_RESPONSE_STATUS_CODE = "http.status_code";
  /**
   * Deprecated. The old status_code tag, in order to be compatible with the old version of agent.
   * It should be replaced by {@link #HTTP_RESPONSE_STATUS_CODE} or using
   * {@link #RPC_RESPONSE_STATUS_CODE} if status code is related to a rpc call.
   */
  @Deprecated
  public static final String STATUS_CODE = "status_code";

  public static final String RPC_RESPONSE_STATUS_CODE = "rpc.status_code";

  public static final String DB_STATEMENT = "db.statement";

  public static final String DB_TYPE = "db.type";

  /**
   * Tag, x-le(extension logic endpoint) series tag. Value is JSON format.
   * 
   * <pre>
   * {
   *   "name": "GraphQL-service",
   *   "latency": 100,
   *   "status": true
   * }
   * </pre>
   *
   * Also, could use value to indicate this local span is representing a logic endpoint.
   * 
   * <pre>
   * {
   *   "logic-span": true
   * }
   * </pre>
   */
  public static final String LOGIC_ENDPOINT = "x-le";
}
