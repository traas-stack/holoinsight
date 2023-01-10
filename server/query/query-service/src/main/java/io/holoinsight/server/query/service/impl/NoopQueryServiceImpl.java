/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.impl;

import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.query.grpc.QueryProto.BizopsEndpoints;
import io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest;
import io.holoinsight.server.query.grpc.QueryProto.PqlRangeRequest;
import io.holoinsight.server.query.grpc.QueryProto.QueryMetaRequest;
import io.holoinsight.server.query.grpc.QueryProto.QueryMetaResponse;
import io.holoinsight.server.query.grpc.QueryProto.QueryMetricsRequest;
import io.holoinsight.server.query.grpc.QueryProto.QueryMetricsResponse;
import io.holoinsight.server.query.grpc.QueryProto.QueryRequest;
import io.holoinsight.server.query.grpc.QueryProto.QueryResponse;
import io.holoinsight.server.query.grpc.QueryProto.QuerySchemaResponse;
import io.holoinsight.server.query.grpc.QueryProto.QueryTopologyRequest;
import io.holoinsight.server.query.grpc.QueryProto.QueryTraceRequest;
import io.holoinsight.server.query.grpc.QueryProto.QueryVirtualComponentResponse;
import io.holoinsight.server.query.grpc.QueryProto.Topology;
import io.holoinsight.server.query.grpc.QueryProto.Trace;
import io.holoinsight.server.query.grpc.QueryProto.TraceBrief;
import io.holoinsight.server.query.grpc.QueryProto.TraceIds;
import io.holoinsight.server.query.service.QueryException;
import io.holoinsight.server.query.service.QueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author xiangwanpeng
 * @Description TODO
 * @date 2022/12/5
 */
public class NoopQueryServiceImpl implements QueryService {

  @Override
  public QueryResponse queryData(QueryRequest request) throws QueryException {
    return null;
  }

  @Override
  public QueryResponse queryTags(QueryRequest request) throws QueryException {
    return null;
  }

  @Override
  public QueryResponse deleteKeys(QueryRequest request) throws QueryException {
    return null;
  }

  @Override
  public QuerySchemaResponse querySchema(QueryRequest request) throws QueryException {
    return null;
  }

  @Override
  public QueryMetricsResponse queryMetrics(QueryMetricsRequest request) throws QueryException {
    return null;
  }

  @Override
  public QueryResponse pqlInstantQuery(PqlInstantRequest request) throws QueryException {
    return null;
  }

  @Override
  public QueryResponse pqlRangeQuery(PqlRangeRequest request) throws QueryException {
    return null;
  }

  @Override
  public TraceBrief queryBasicTraces(QueryTraceRequest request) throws Exception {
    return null;
  }

  @Override
  public Trace queryTrace(QueryTraceRequest request) throws Exception {
    return null;
  }

  @Override
  public QueryMetaResponse queryServiceList(QueryMetaRequest request) throws Exception {
    return null;
  }

  @Override
  public QueryMetaResponse queryEndpointList(QueryMetaRequest request) throws Exception {
    return null;
  }

  @Override
  public QueryMetaResponse queryServiceInstanceList(QueryMetaRequest request) throws Exception {
    return null;
  }

  @Override
  public QueryVirtualComponentResponse queryComponentList(QueryMetaRequest request)
      throws Exception {
    return null;
  }

  @Override
  public TraceIds queryComponentTraceIds(QueryMetaRequest request) throws Exception {
    return null;
  }

  @Override
  public Topology queryTopology(QueryTopologyRequest request) throws Exception {
    return null;
  }

  @Override
  public BizopsEndpoints queryBizEndpointList(QueryMetaRequest request) throws Exception {
    return null;
  }

  @Override
  public BizopsEndpoints queryBizErrorCodeList(QueryMetaRequest request) throws Exception {
    return null;
  }

  @Override
  public QueryProto.QuerySlowSqlResponse querySlowSqlList(QueryMetaRequest request)
      throws Exception {
    return null;
  }
}
