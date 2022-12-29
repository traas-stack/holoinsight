/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service;

import io.holoinsight.server.query.grpc.QueryProto;

/**
 * @author xiangwanpeng
 * @version : QueryService.java, v 0.1 2022年04月22日 2:37 PM xiangwanpeng Exp $
 */
public interface QueryService {
  QueryProto.QueryResponse queryData(QueryProto.QueryRequest request) throws QueryException;

  QueryProto.QueryResponse queryTags(QueryProto.QueryRequest request) throws QueryException;

  QueryProto.QueryResponse deleteKeys(QueryProto.QueryRequest request) throws QueryException;

  QueryProto.QuerySchemaResponse querySchema(QueryProto.QueryRequest request) throws QueryException;

  QueryProto.QueryMetricsResponse queryMetrics(QueryProto.QueryMetricsRequest request)
      throws QueryException;

  QueryProto.QueryResponse pqlInstantQuery(QueryProto.PqlInstantRequest request)
      throws QueryException;

  QueryProto.QueryResponse pqlRangeQuery(QueryProto.PqlRangeRequest request) throws QueryException;

  QueryProto.TraceBrief queryBasicTraces(QueryProto.QueryTraceRequest request) throws Exception;

  QueryProto.Trace queryTrace(QueryProto.QueryTraceRequest request) throws Exception;

  QueryProto.QueryMetaResponse queryServiceList(QueryProto.QueryMetaRequest request) throws Exception;
  QueryProto.QueryMetaResponse queryEndpointList(QueryProto.QueryMetaRequest request) throws Exception;
  QueryProto.QueryMetaResponse queryServiceInstanceList(QueryProto.QueryMetaRequest request) throws Exception;
  QueryProto.QueryVirtualComponentResponse queryComponentList(QueryProto.QueryMetaRequest request) throws Exception;

  QueryProto.TraceIds queryComponentTraceIds(QueryProto.QueryMetaRequest request) throws Exception;
  QueryProto.Topology queryTopology(QueryProto.QueryTopologyRequest request) throws Exception;
  QueryProto.BizopsEndpoints queryBizEndpointList(QueryProto.QueryMetaRequest request) throws Exception;
  QueryProto.BizopsEndpoints queryBizErrorCodeList(QueryProto.QueryMetaRequest request) throws Exception;
  QueryProto.QuerySlowSqlResponse querySlowSqlList(QueryProto.QueryMetaRequest request) throws Exception;

}
