/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service;

import io.holoinsight.server.query.grpc.QueryProto;

/**
 * @author masaimu
 * @version 2023-06-09 17:30:00
 */
public interface RequestContextAdapter {

  QueryProto.QueryRequest requestAdapte(QueryProto.QueryRequest request);

  QueryProto.PqlRangeRequest requestAdapte(QueryProto.PqlRangeRequest request);

}
