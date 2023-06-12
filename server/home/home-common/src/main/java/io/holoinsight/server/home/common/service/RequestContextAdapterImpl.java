/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service;

import io.holoinsight.server.query.grpc.QueryProto;

/**
 * @author masaimu
 * @version 2023-06-09 17:31:00
 */
public class RequestContextAdapterImpl implements RequestContextAdapter {
  @Override
  public QueryProto.QueryRequest requestAdapte(QueryProto.QueryRequest request) {
    return request;
  }

  @Override
  public QueryProto.PqlRangeRequest requestAdapte(QueryProto.PqlRangeRequest request) {
    return request;
  }
}
