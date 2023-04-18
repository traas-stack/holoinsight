/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service;

import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.apm.common.model.query.QueryTraceRequest;
import io.holoinsight.server.apm.common.model.query.ServiceInstance;
import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.common.model.query.Topology;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.common.service.query.KeyResult;
import io.holoinsight.server.home.common.service.query.QueryResponse;
import io.holoinsight.server.home.common.service.query.QuerySchemaResponse;
import io.holoinsight.server.home.common.service.query.Result;
import io.holoinsight.server.home.common.service.query.ValueResult;
import io.holoinsight.server.home.common.util.Debugger;
import io.holoinsight.server.query.common.convertor.ApmConvertor;
import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.query.grpc.QueryProto.Datasource;
import io.holoinsight.server.query.grpc.QueryServiceGrpc;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class QueryClientService {

  @GrpcClient("queryService")
  private QueryServiceGrpc.QueryServiceBlockingStub queryServiceBlockingStub;

  public QueryResponse query(QueryProto.QueryRequest request) {
    Debugger.print("QueryService", "query, request: " + J.toJson(request));

    QueryProto.QueryResponse res = queryServiceBlockingStub.queryData(request);

    Debugger.print("QueryService", "query, response: " + J.toJson(res));

    QueryResponse response = new QueryResponse();
    List<Result> results = new ArrayList<>();

    Map<String, Datasource> map = new HashMap<>();
    for (Datasource datasource : request.getDatasourcesList()) {
      map.put(datasource.getMetric(), datasource);
    }

    for (QueryProto.Result result : res.getResultsList()) {
      Result r = new Result();
      r.setMetric(result.getMetric());
      r.setTags(result.getTagsMap());
      List<Object[]> values = new ArrayList<>();

      for (QueryProto.Point p : result.getPointsList()) {
        Object[] value = new Object[] {p.getTimestamp(),
            p.getValue() != 0 || StringUtils.isBlank(p.getStrValue()) ? String.valueOf(p.getValue())
                : p.getStrValue()};
        values.add(value);
      }
      r.setValues(values);
      results.add(r);
    }

    response.setResults(results);

    return response;
  }

  public QueryResponse queryTags(QueryProto.QueryRequest request) {
    Debugger.print("QueryService", "queryTags, request: " + J.toJson(request));

    QueryProto.QueryResponse res = queryServiceBlockingStub.queryTags(request);

    Debugger.print("QueryService", "queryTags, response: " + J.toJson(res));
    QueryResponse response = new QueryResponse();
    List<Result> results = new ArrayList<>();

    for (QueryProto.Result result : res.getResultsList()) {
      Result r = new Result();
      r.setMetric(result.getMetric());
      r.setTags(result.getTagsMap());
      results.add(r);
    }

    response.setResults(results);

    return response;
  }

  public void delTags(QueryProto.QueryRequest request) {
    Debugger.print("QueryService", "delTags, request: " + J.toJson(request));
    QueryProto.QueryResponse res = queryServiceBlockingStub.deleteKeys(request);
    Debugger.print("QueryService", "delTags, response: " + J.toJson(res));
  }

  public QuerySchemaResponse querySchema(QueryProto.QueryRequest request) {
    Debugger.print("QueryService", "querySchema, request: " + J.toJson(request));

    QueryProto.QuerySchemaResponse schema = queryServiceBlockingStub.querySchema(request);

    Debugger.print("QueryService", "querySchema, response: " + J.toJson(schema));
    QuerySchemaResponse schemaResponse = new QuerySchemaResponse();
    List<KeyResult> results = new ArrayList<>();

    for (QueryProto.KeysResult result : schema.getResultsList()) {
      KeyResult r = new KeyResult();
      r.setMetric(result.getMetric());
      r.setTags(result.getKeysList());
      results.add(r);
    }

    schemaResponse.setResults(results);

    return schemaResponse;
  }

  public List<String> queryMetrics(QueryProto.QueryMetricsRequest request) {
    QueryProto.QueryMetricsResponse metrics = queryServiceBlockingStub.queryMetrics(request);

    List<String> results = new ArrayList<>();
    if (metrics != null && metrics.getResultsCount() != 0) {
      List<QueryProto.MetricResult> metricResult = metrics.getResultsList();
      results =
          metricResult.stream().map(QueryProto.MetricResult::getName).collect(Collectors.toList());
    }

    return results;
  }

  public ValueResult queryTagValues(QueryProto.QueryRequest request, String key) {

    QueryProto.QueryResponse response = queryServiceBlockingStub.queryData(request);
    ValueResult results = new ValueResult();
    Set<String> values = new HashSet<>();
    for (QueryProto.Result result : response.getResultsList()) {
      values.add(result.getTagsMap().get(key));
    }
    results.setTag(key);
    results.setValues(new ArrayList<>(values));
    return results;
  }

  public QueryResponse pqlInstantQuery(QueryProto.PqlInstantRequest request) {
    QueryProto.QueryResponse res = queryServiceBlockingStub.pqlInstantQuery(request);

    QueryResponse response = new QueryResponse();
    List<Result> results = new ArrayList<>();

    for (QueryProto.Result result : res.getResultsList()) {
      Result r = new Result();
      r.setMetric(result.getMetric());
      r.setTags(result.getTagsMap());
      List<Object[]> values = new ArrayList<>();

      for (QueryProto.Point p : result.getPointsList()) {
        Object[] value = new Object[] {p.getTimestamp(), p.getStrValue()};
        values.add(value);
      }

      r.setValues(values);
      results.add(r);
    }

    response.setResults(results);

    return response;
  }

  public QueryResponse pqlRangeQuery(QueryProto.PqlRangeRequest request) {
    QueryProto.QueryResponse res = queryServiceBlockingStub.pqlRangeQuery(request);

    QueryResponse response = new QueryResponse();
    List<Result> results = new ArrayList<>();

    for (QueryProto.Result result : res.getResultsList()) {
      Result r = new Result();
      r.setMetric(result.getMetric());
      r.setTags(result.getTagsMap());
      List<Object[]> values = new ArrayList<>();

      for (QueryProto.Point p : result.getPointsList()) {
        Object[] value = new Object[] {p.getTimestamp(), p.getStrValue()};
        values.add(value);
      }

      r.setValues(values);
      results.add(r);
    }

    response.setResults(results);

    return response;
  }

  public TraceBrief queryBasicTraces(QueryTraceRequest request) {
    QueryProto.TraceBrief traceBrief =
        queryServiceBlockingStub.queryBasicTraces(convertRequest(request));
    return ApmConvertor.convertTraceBrief(traceBrief);
  }

  public Trace queryTrace(QueryTraceRequest request) {
    QueryProto.Trace trace = queryServiceBlockingStub.queryTrace(convertRequest(request));
    return ApmConvertor.convertTrace(trace);
  }

  public List<io.holoinsight.server.apm.common.model.query.Service> queryServiceList(
      QueryProto.QueryMetaRequest request) {
    QueryProto.QueryMetaResponse queryMetaResponse =
        queryServiceBlockingStub.queryServiceList(request);
    List<io.holoinsight.server.apm.common.model.query.Service> result =
        new ArrayList<>(queryMetaResponse.getMataList().size());
    queryMetaResponse.getMataList().forEach(meta -> {
      result.add(ApmConvertor.deConvertService(meta));
    });
    return result;
  }

  public List<Endpoint> queryEndpointList(QueryProto.QueryMetaRequest request) {
    QueryProto.QueryMetaResponse queryMetaResponse =
        queryServiceBlockingStub.queryEndpointList(request);
    List<Endpoint> result = new ArrayList<>(queryMetaResponse.getMataList().size());
    queryMetaResponse.getMataList().forEach(meta -> {
      result.add(ApmConvertor.deConvertEndpoint(meta));
    });
    return result;
  }

  public List<ServiceInstance> queryServiceInstanceList(QueryProto.QueryMetaRequest request) {
    QueryProto.QueryMetaResponse queryMetaResponse =
        queryServiceBlockingStub.queryServiceInstanceList(request);
    List<ServiceInstance> result = new ArrayList<>(queryMetaResponse.getMataList().size());
    queryMetaResponse.getMataList().forEach(meta -> {
      result.add(ApmConvertor.deConvertServiceInstance(meta));
    });
    return result;
  }

  public List<VirtualComponent> queryComponentList(QueryProto.QueryMetaRequest request) {
    QueryProto.QueryVirtualComponentResponse queryDbList =
        queryServiceBlockingStub.queryComponentList(request);
    List<VirtualComponent> result = new ArrayList<>(queryDbList.getComponentList().size());
    queryDbList.getComponentList().forEach(db -> {
      result.add(ApmConvertor.deConvertDb(db));
    });
    return result;
  }

  public List<String> queryComponentTraceIds(QueryProto.QueryMetaRequest request) {
    QueryProto.TraceIds traceIds = queryServiceBlockingStub.queryComponentTraceIds(request);
    return traceIds.getTraceIdList();
  }

  public Topology queryTopology(QueryProto.QueryTopologyRequest request) {
    QueryProto.Topology topology = queryServiceBlockingStub.queryTopology(request);
    return ApmConvertor.deConvertTopology(topology);
  }

  public List<SlowSql> querySlowSqlList(QueryProto.QueryMetaRequest request) {
    QueryProto.QuerySlowSqlResponse querySlowSqlList =
        queryServiceBlockingStub.querySlowSqlList(request);
    List<SlowSql> result = new ArrayList<>(querySlowSqlList.getSlowSqlCount());
    querySlowSqlList.getSlowSqlList().forEach(sql -> {
      result.add(ApmConvertor.deConvertSlowSql(sql));
    });
    return result;
  }

  private QueryProto.QueryTraceRequest convertRequest(QueryTraceRequest request) {
    QueryProto.QueryTraceRequest.Builder requestBuilder =
        QueryProto.QueryTraceRequest.newBuilder().setTenant(request.getTenant());
    if (StringUtils.isNotEmpty(request.getServiceName())) {
      requestBuilder.setServiceName(request.getServiceName());
    }
    if (StringUtils.isNotEmpty(request.getServiceInstanceName())) {
      requestBuilder.setServiceInstanceName(request.getServiceInstanceName());
    }
    if (!CollectionUtils.isEmpty(request.getTraceIds())) {
      requestBuilder.addAllTraceIds(request.getTraceIds());
    }
    if (StringUtils.isNotEmpty(request.getEndpointName())) {
      requestBuilder.setEndpointName(request.getEndpointName());
    }
    if (request.getDuration() != null) {
      requestBuilder.setStart(request.getDuration().getStart());
      requestBuilder.setEnd(request.getDuration().getEnd());
    }
    requestBuilder.setMinTraceDuration(request.getMinTraceDuration());
    requestBuilder.setMaxTraceDuration(request.getMaxTraceDuration());
    if (request.getTraceState() != null) {
      requestBuilder.setTraceState(request.getTraceState().name());
    }
    if (request.getQueryOrder() != null) {
      requestBuilder.setQueryOrder(request.getQueryOrder().name());
    }
    if (request.getPaging() != null) {
      requestBuilder.setPageNum(request.getPaging().getPageNum());
      requestBuilder.setPageSize(request.getPaging().getPageSize());
    }
    if (CollectionUtils.isNotEmpty(request.getTags())) {
      Map<String, String> tags = new HashMap<>();
      request.getTags().forEach(tag -> tags.put(tag.getKey(), tag.getValue()));
      requestBuilder.putAllTags(tags);
    }
    return requestBuilder.build();
  }

  public QueryProto.QueryResponse queryData(QueryProto.QueryRequest request) {

    QueryProto.QueryResponse response = queryServiceBlockingStub.queryData(request);
    return response;
  }

  public QueryProto.QueryResponse queryTag(QueryProto.QueryRequest request) {

    QueryProto.QueryResponse response = queryServiceBlockingStub.queryTags(request);
    return response;
  }

  public QueryProto.QueryResponse queryPqlRange(QueryProto.PqlRangeRequest request) {
    QueryProto.QueryResponse response = queryServiceBlockingStub.pqlRangeQuery(request);
    return response;
  }
}
