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

  QueryProto.TraceBrief queryBasicTraces(QueryProto.QueryTraceRequest request)
      throws QueryException;

  QueryProto.Trace queryTrace(QueryProto.QueryTraceRequest request) throws QueryException;

  QueryProto.TraceTreeList queryTraceTree(QueryProto.QueryTraceRequest request)
      throws QueryException;

  QueryProto.StatisticData billingTrace(QueryProto.QueryTraceRequest request) throws QueryException;

  QueryProto.StatisticDataList statisticTrace(QueryProto.StatisticRequest request)
      throws QueryException;

  QueryProto.QueryMetaResponse queryServiceList(QueryProto.QueryMetaRequest request)
      throws QueryException;

  QueryProto.QueryMetaResponse queryEndpointList(QueryProto.QueryMetaRequest request)
      throws QueryException;

  QueryProto.QueryMetaResponse queryServiceInstanceList(QueryProto.QueryMetaRequest request)
      throws QueryException;

  QueryProto.QueryVirtualComponentResponse queryComponentList(QueryProto.QueryMetaRequest request)
      throws QueryException;

  QueryProto.TraceIds queryComponentTraceIds(QueryProto.QueryMetaRequest request)
      throws QueryException;

  QueryProto.Topology queryTopology(QueryProto.QueryTopologyRequest request) throws QueryException;

  QueryProto.QuerySlowSqlResponse querySlowSqlList(QueryProto.QueryMetaRequest request)
      throws QueryException;

  QueryProto.CommonMapTypeDataList queryServiceErrorList(QueryProto.QueryMetaRequest request)
      throws QueryException;

  QueryProto.CommonMapTypeDataList queryServiceErrorDetail(QueryProto.QueryMetaRequest request)
      throws QueryException;

  QueryProto.QueryDetailResponse queryDetailData(QueryProto.QueryRequest request)
      throws QueryException;

  QueryProto.QueryEventResponse queryEvents(QueryProto.QueryEventRequest request)
      throws QueryException;
}
