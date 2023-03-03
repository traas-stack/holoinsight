/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

/**
 * RPC request type.
 */
public enum RequestType {
  DATABASE, HTTP, RPC, GRPC,
  /**
   * Logic request only.
   */
  LOGIC, TCP, MQ, CACHE
}
