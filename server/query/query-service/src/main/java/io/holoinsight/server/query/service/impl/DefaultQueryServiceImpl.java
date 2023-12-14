/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.protobuf.MessageOrBuilder;
import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.common.event.Event;
import io.holoinsight.server.apm.common.model.query.MetricValue;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.common.model.query.Pagination;
import io.holoinsight.server.apm.common.model.query.QueryComponentRequest;
import io.holoinsight.server.apm.common.model.query.QueryEndpointRequest;
import io.holoinsight.server.apm.common.model.query.QueryMetricRequest;
import io.holoinsight.server.apm.common.model.query.QueryOrder;
import io.holoinsight.server.apm.common.model.query.QueryServiceInstanceRequest;
import io.holoinsight.server.apm.common.model.query.QueryServiceRequest;
import io.holoinsight.server.apm.common.model.query.QueryTopologyRequest;
import io.holoinsight.server.apm.common.model.query.QueryTraceRequest;
import io.holoinsight.server.apm.common.model.query.Request;
import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.common.model.query.ServiceInstance;
import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.common.model.query.StatisticData;
import io.holoinsight.server.apm.common.model.query.StatisticDataList;
import io.holoinsight.server.apm.common.model.query.StatisticRequest;
import io.holoinsight.server.apm.common.model.query.Topology;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.query.TraceTree;
import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.common.model.specification.OtlpMappings;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.common.utils.GsonUtils;
import io.holoinsight.server.apm.engine.postcal.MetricDefine;
import io.holoinsight.server.common.DurationUtil;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.ProtoJsonUtils;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.model.DetailResult;
import io.holoinsight.server.extension.model.PqlParam;
import io.holoinsight.server.extension.model.QueryMetricsParam;
import io.holoinsight.server.extension.model.QueryParam;
import io.holoinsight.server.extension.model.QueryResult.Point;
import io.holoinsight.server.extension.model.QueryResult.Result;
import io.holoinsight.server.query.common.RpnResolver;
import io.holoinsight.server.query.common.convertor.ApmConvertor;
import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.query.grpc.QueryProto.Point.Builder;
import io.holoinsight.server.query.grpc.QueryProto.PqlInstantRequest;
import io.holoinsight.server.query.service.QueryException;
import io.holoinsight.server.query.service.QueryService;
import io.holoinsight.server.query.service.analysis.AggCenter;
import io.holoinsight.server.query.service.analysis.Mergeable;
import io.holoinsight.server.query.service.apm.ApmAPI;
import io.holoinsight.server.query.service.apm.ApmClient;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import retrofit2.Call;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
public class DefaultQueryServiceImpl implements QueryService {

  @Autowired
  protected MetricStorage metricStorage;

  @Autowired
  private ApmClient apmClient;

  @Autowired
  private SuperCacheService superCacheService;

  private LoadingCache<String, Map<String, MetricDefine>> apmMetrics =
      CacheBuilder.newBuilder().expireAfterWrite(60, TimeUnit.SECONDS)
          .build(new CacheLoader<String, Map<String, MetricDefine>>() {
            @Override
            public Map<String, MetricDefine> load(String tenantName) {
              return listApmMetrics(tenantName);
            }
          });

  @Override
  public QueryProto.QueryResponse queryData(QueryProto.QueryRequest request) throws QueryException {
    return wrap(() -> {
      List<QueryProto.Datasource> datasources = request.getDatasourcesList();
      Assert.notEmpty(datasources, "datasources are empty");
      String tenant = request.getTenant();
      String query = request.getQuery();
      String downsample = request.getDownsample();
      String fillPolicy = request.getFillPolicy();
      if (StringUtils.isBlank(query)
          || datasources.stream().anyMatch(ds -> query.equals(ds.getName()))) {
        return simpleQuery(request, tenant, datasources);
      } else {
        return complexQuery(request, tenant, query, query, downsample, fillPolicy, datasources);
      }
    }, "queryData", request);
  }

  @Override
  public QueryProto.QueryResponse queryTags(QueryProto.QueryRequest request) throws QueryException {
    return wrap(() -> {
      Assert.notEmpty(request.getDatasourcesList(), "datasources are empty");
      QueryProto.QueryResponse.Builder rspBuilder = QueryProto.QueryResponse.newBuilder();
      for (QueryProto.Datasource datasource : request.getDatasourcesList()) {
        List<QueryProto.Result> results = searchTags(request.getTenant(), datasource);
        rspBuilder.addAllResults(results);
      }
      return rspBuilder.build();
    }, "queryTags", request);
  }

  @Override
  public QueryProto.QueryResponse deleteKeys(QueryProto.QueryRequest request)
      throws QueryException {
    return wrap(() -> {
      Assert.notEmpty(request.getDatasourcesList(), "datasources empty!");
      for (QueryProto.Datasource datasource : request.getDatasourcesList()) {
        QueryParam param = QueryStorageUtils.convertToQueryParam(request.getTenant(), datasource);
        metricStorage.deleteKeys(param);
      }
      return QueryProto.QueryResponse.getDefaultInstance();
    }, "deleteKeys", request);
  }

  @Override
  public QueryProto.QuerySchemaResponse querySchema(QueryProto.QueryRequest request)
      throws QueryException {
    return wrap(() -> {
      QueryProto.QuerySchemaResponse.Builder builder = QueryProto.QuerySchemaResponse.newBuilder();
      Map<String, Set<String>> keys = new HashMap<>();
      List<QueryProto.Datasource> datasources = request.getDatasourcesList();
      for (QueryProto.Datasource ds : datasources) {
        if (this.apmMetrics.getUnchecked(request.getTenant()).containsKey(ds.getMetric())) {
          Response<List<String>> schemaRsp = apmClient.getClient(request.getTenant())
              .querySchema(new QueryMetricRequest(request.getTenant(), ds.getMetric(), null, null))
              .execute();
          if (!schemaRsp.isSuccessful()) {
            throw new QueryException(schemaRsp.errorBody().string());
          }
          builder.addResults(QueryProto.KeysResult.newBuilder().setMetric(ds.getMetric())
              .addAllKeys(schemaRsp.body()).build());
        } else {
          QueryParam queryParam = new QueryParam();
          queryParam.setMetric(ds.getMetric());
          queryParam.setTenant(request.getTenant());
          if (StringUtils.isNotEmpty(ds.getQl())) {
            queryParam.setQl(ds.getQl());
          }
          Result result = metricStorage.querySchema(queryParam);
          Map<String, String> tags = result.getTags();
          keys.put(ds.getMetric(), tags.keySet());
        }
      }
      keys.forEach((m, ks) -> builder
          .addResults(QueryProto.KeysResult.newBuilder().setMetric(m).addAllKeys(ks)).build());
      return builder.build();
    }, "querySchema", request);
  }

  @Override
  public QueryProto.QueryMetricsResponse queryMetrics(QueryProto.QueryMetricsRequest request)
      throws QueryException {
    return wrap(() -> {

      QueryProto.QueryMetricsResponse.Builder b = QueryProto.QueryMetricsResponse.newBuilder();

      Set<String> metrics = new HashSet<>();

      QueryMetricsParam param = QueryStorageUtils.convertToQueryMetricsParam(request);
      List<String> suggestRsp = metricStorage.queryMetrics(param);

      if (CollectionUtils.isNotEmpty(suggestRsp)) {
        for (String metric : suggestRsp) {
          metrics.add(metric);
        }
      }

      Map<String, MetricDefine> apmMetrics = this.apmMetrics.getUnchecked(request.getTenant());
      if (apmMetrics != null) {
        for (String apmMetric : apmMetrics.keySet()) {
          if (StringUtils.isBlank(request.getName())
              || request.getExplicit() && StringUtils.equals(apmMetric, request.getName())
              || !request.getExplicit() && StringUtils.contains(apmMetric, request.getName())) {
            metrics.add(apmMetric);
          }
        }
      }

      metrics.forEach(metric -> {
        QueryProto.MetricResult mr = QueryProto.MetricResult.newBuilder().setName(metric).build();
        b.addResults(mr);
      });

      return b.build();
    }, "queryMetrics", request);
  }

  @Override
  public QueryProto.QueryResponse pqlInstantQuery(PqlInstantRequest request) throws QueryException {
    QueryProto.QueryResponse.Builder builder = QueryProto.QueryResponse.newBuilder();
    List<Result> pqlResult = metricStorage.pqlInstantQuery(transToPqlParam(request));
    QueryProto.Result.Builder resultBuilder = QueryProto.Result.newBuilder();
    resultBuilder.setMetric(request.getQuery());
    for (Result result : pqlResult) {
      resultBuilder.putAllTags(result.getTags());
      for (Point point : result.getPoints()) {
        Builder pointBuilder = QueryProto.Point.newBuilder().setTimestamp(point.getTimestamp())
            .setValue(point.getValue());
        resultBuilder.addPoints(pointBuilder.build());
      }
    }
    builder.addResults(resultBuilder.build());
    return builder.build();
  }

  private PqlParam transToPqlParam(PqlInstantRequest request) {
    PqlParam pqlParam = new PqlParam();
    pqlParam.setTenant(request.getTenant());
    pqlParam.setQuery(request.getQuery());
    pqlParam.setTime(request.getTime());
    pqlParam.setTimeout(request.getTimeout());
    pqlParam.setDelta(request.getDelta());
    return pqlParam;
  }

  private PqlParam transToPqlParam(QueryProto.PqlRangeRequest request) {
    PqlParam pqlParam = new PqlParam();
    pqlParam.setTenant(request.getTenant());
    pqlParam.setQuery(request.getQuery());
    pqlParam.setTimeout(request.getTimeout());
    pqlParam.setDelta(request.getDelta());
    pqlParam.setStart(request.getStart());
    pqlParam.setEnd(request.getEnd());
    pqlParam.setStep(request.getStep());
    return pqlParam;
  }

  @Override
  public QueryProto.QueryResponse pqlRangeQuery(QueryProto.PqlRangeRequest request)
      throws QueryException {
    QueryProto.QueryResponse.Builder builder = QueryProto.QueryResponse.newBuilder();
    List<Result> pqlResult = metricStorage.pqlInstantQuery(transToPqlParam(request));
    for (Result result : pqlResult) {
      QueryProto.Result.Builder resultBuilder = QueryProto.Result.newBuilder();
      resultBuilder.setMetric(result.getMetric());
      resultBuilder.putAllTags(result.getTags());
      for (Point point : result.getPoints()) {
        Builder pointBuilder = QueryProto.Point.newBuilder().setTimestamp(point.getTimestamp())
            .setStrValue(point.getStrValue());
        resultBuilder.addPoints(pointBuilder.build());
      }
      builder.addResults(resultBuilder.build());
    }
    return builder.build();
  }

  @Override
  public QueryProto.TraceBrief queryBasicTraces(QueryProto.QueryTraceRequest request)
      throws QueryException {
    return wrap(() -> {
      Assert.isTrue(
          !request.getTraceIdsList().isEmpty()
              || (request.getStart() != 0 && request.getEnd() != 0),
          "traceId or timeRange should be set!");

      ApmAPI apmAPI = apmClient.getClient(request.getTenant());
      QueryTraceRequest queryTraceRequest = new QueryTraceRequest();
      queryTraceRequest.setTenant(request.getTenant());
      queryTraceRequest.setServiceName(request.getServiceName());
      queryTraceRequest.setServiceInstanceName(request.getServiceInstanceName());
      queryTraceRequest.setTraceIds(request.getTraceIdsList());
      queryTraceRequest.setEndpointName(request.getEndpointName());
      queryTraceRequest.setDuration(new Duration(request.getStart(), request.getEnd(), null));

      queryTraceRequest.setMinTraceDuration(request.getMinTraceDuration());
      queryTraceRequest.setMaxTraceDuration(request.getMaxTraceDuration());
      if (StringUtils.isEmpty(request.getTraceState())) {
        queryTraceRequest.setTraceState(TraceState.ALL);
      } else {
        queryTraceRequest.setTraceState(TraceState.valueOf(request.getTraceState()));
      }
      if (StringUtils.isEmpty(request.getQueryOrder())) {
        queryTraceRequest.setQueryOrder(QueryOrder.BY_DURATION);
      } else {
        queryTraceRequest.setQueryOrder(QueryOrder.valueOf(request.getQueryOrder()));
      }
      Assert.isTrue(request.getPageSize() > 0, "page size should be set!");
      queryTraceRequest.setPaging(new Pagination(request.getPageNum(), request.getPageSize()));
      queryTraceRequest.setTags(ApmConvertor.convertTagsMap(request.getTagsMap()));
      Call<TraceBrief> call = apmAPI.queryBasicTraces(queryTraceRequest);
      Response<TraceBrief> traceBriefRsp = call.execute();
      if (!traceBriefRsp.isSuccessful()) {
        throw new QueryException(traceBriefRsp.errorBody().string());
      }
      return ApmConvertor.convertTraceBrief(traceBriefRsp.body());
    }, "queryBasicTraces", request);
  }

  @Override
  public QueryProto.Trace queryTrace(QueryProto.QueryTraceRequest request) throws QueryException {
    return wrap(() -> {
      Assert.isTrue(!request.getTraceIdsList().isEmpty(), "trace id should be set!");
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());
      QueryTraceRequest queryTraceRequest = new QueryTraceRequest();
      queryTraceRequest.setDuration(new Duration(request.getStart(), request.getEnd(), null));
      queryTraceRequest.setTenant(request.getTenant());
      queryTraceRequest.setTraceIds(request.getTraceIdsList());
      queryTraceRequest.setTags(ApmConvertor.convertTagsMap(request.getTagsMap()));
      Call<Trace> call = apmAPI.queryTrace(queryTraceRequest);
      Response<Trace> traceRsp = call.execute();
      if (!traceRsp.isSuccessful()) {
        throw new QueryException(traceRsp.errorBody().string());
      }
      return ApmConvertor.convertTrace(traceRsp.body());
    }, "queryTrace", request);
  }

  @Override
  public QueryProto.TraceTreeList queryTraceTree(QueryProto.QueryTraceRequest request)
      throws QueryException {
    return wrap(() -> {
      Assert.isTrue(!request.getTraceIdsList().isEmpty(), "trace id should be set!");
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());
      QueryTraceRequest queryTraceRequest = new QueryTraceRequest();
      queryTraceRequest.setDuration(new Duration(request.getStart(), request.getEnd(), null));
      queryTraceRequest.setTenant(request.getTenant());
      queryTraceRequest.setTraceIds(request.getTraceIdsList());
      queryTraceRequest.setTags(ApmConvertor.convertTagsMap(request.getTagsMap()));
      Call<List<TraceTree>> call = apmAPI.queryTraceTree(queryTraceRequest);
      Response<List<TraceTree>> traceRsp = call.execute();
      if (!traceRsp.isSuccessful()) {
        throw new QueryException(traceRsp.errorBody().string());
      }
      return ApmConvertor.convertTraceTree(traceRsp.body());
    }, "queryTraceTree", request);
  }

  @Override
  public QueryProto.StatisticData billingTrace(QueryProto.QueryTraceRequest request)
      throws QueryException {
    return wrap(() -> {
      Assert.isTrue(
          !request.getTraceIdsList().isEmpty()
              || (request.getStart() != 0 && request.getEnd() != 0),
          "traceId or timeRange should be set!");

      ApmAPI apmAPI = apmClient.getClient(request.getTenant());
      QueryTraceRequest queryTraceRequest = new QueryTraceRequest();
      queryTraceRequest.setTenant(request.getTenant());
      queryTraceRequest.setServiceName(request.getServiceName());
      queryTraceRequest.setServiceInstanceName(request.getServiceInstanceName());
      queryTraceRequest.setTraceIds(request.getTraceIdsList());
      queryTraceRequest.setEndpointName(request.getEndpointName());
      queryTraceRequest.setDuration(new Duration(request.getStart(), request.getEnd(), null));

      queryTraceRequest.setMinTraceDuration(request.getMinTraceDuration());
      queryTraceRequest.setMaxTraceDuration(request.getMaxTraceDuration());
      if (StringUtils.isEmpty(request.getTraceState())) {
        queryTraceRequest.setTraceState(TraceState.ALL);
      } else {
        queryTraceRequest.setTraceState(TraceState.valueOf(request.getTraceState()));
      }
      queryTraceRequest.setTags(ApmConvertor.convertTagsMap(request.getTagsMap()));
      Call<StatisticData> call = apmAPI.billing(queryTraceRequest);
      Response<StatisticData> statisticDataRsp = call.execute();
      if (!statisticDataRsp.isSuccessful()) {
        throw new QueryException(statisticDataRsp.errorBody().string());
      }
      return ApmConvertor.convertStatisticData(statisticDataRsp.body());
    }, "billingTrace", request);
  }

  @Override
  public QueryProto.StatisticDataList statisticTrace(QueryProto.StatisticRequest request)
      throws QueryException {
    return wrap(() -> {
      Assert.isTrue((request.getStart() != 0 && request.getEnd() != 0), "timeRange should be set!");

      Assert.notEmpty((request.getGroupsList()),
          "groups should be set, or you should use billing API!");

      ApmAPI apmAPI = apmClient.getClient(request.getTenant());
      StatisticRequest queryTraceRequest = new StatisticRequest();
      queryTraceRequest.setTenant(request.getTenant());
      queryTraceRequest.setStart(request.getStart());
      queryTraceRequest.setEnd(request.getEnd());
      queryTraceRequest.setGroups(request.getGroupsList());
      Call<StatisticDataList> call = apmAPI.statistic(queryTraceRequest);
      Response<StatisticDataList> statisticDataRsp = call.execute();
      if (!statisticDataRsp.isSuccessful()) {
        throw new QueryException(statisticDataRsp.errorBody().string());
      }
      return ApmConvertor.convert(statisticDataRsp.body());
    }, "statisticTrace", request);
  }

  @Override
  public QueryProto.QueryMetaResponse queryServiceList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return wrap(() -> {
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());

      QueryServiceRequest queryServiceRequest = new QueryServiceRequest();
      queryServiceRequest.setTenant(request.getTenant());
      queryServiceRequest.setStartTime(request.getStart());
      queryServiceRequest.setEndTime(request.getEnd());
      if (request.getTermParamsMap() != null) {
        queryServiceRequest.setTermParams(request.getTermParamsMap());
      }

      Call<List<Service>> serviceList = apmAPI.queryServiceList(queryServiceRequest);
      Response<List<Service>> listResponse = serviceList.execute();
      if (!listResponse.isSuccessful()) {
        throw new QueryException(listResponse.errorBody().string());
      }

      QueryProto.QueryMetaResponse.Builder builder = QueryProto.QueryMetaResponse.newBuilder();
      listResponse.body().forEach(service -> {
        builder.addMata((ApmConvertor.convertService(service)));
      });

      return builder.build();
    }, "queryServiceList", request);
  }

  @Override
  public QueryProto.QueryMetaResponse queryEndpointList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return wrap(() -> {
      Assert.notNull(request.getServiceName(), "serviceName is null!");
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());

      QueryEndpointRequest queryEndpointRequest = new QueryEndpointRequest();
      queryEndpointRequest.setTenant(request.getTenant());
      queryEndpointRequest.setServiceName(request.getServiceName());
      queryEndpointRequest.setStartTime(request.getStart());
      queryEndpointRequest.setEndTime(request.getEnd());
      if (request.getTermParamsMap() != null) {
        queryEndpointRequest.setTermParams(request.getTermParamsMap());
      }

      Call<List<Endpoint>> listCall = apmAPI.queryEndpointList(queryEndpointRequest);
      Response<List<Endpoint>> listResponse = listCall.execute();
      if (!listResponse.isSuccessful()) {
        throw new QueryException(listResponse.errorBody().string());
      }

      QueryProto.QueryMetaResponse.Builder builder = QueryProto.QueryMetaResponse.newBuilder();
      listResponse.body().forEach(endpoint -> {
        builder.addMata((ApmConvertor.convertEndpoint(endpoint)));
      });

      return builder.build();
    }, "queryEndpointList", request);
  }

  @Override
  public QueryProto.QueryMetaResponse queryServiceInstanceList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return wrap(() -> {
      Assert.notNull(request.getServiceName(), "serviceName is null!");
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());

      QueryServiceInstanceRequest queryServiceRequest = new QueryServiceInstanceRequest();
      queryServiceRequest.setTenant(request.getTenant());
      queryServiceRequest.setServiceName(request.getServiceName());
      queryServiceRequest.setStartTime(request.getStart());
      queryServiceRequest.setEndTime(request.getEnd());
      if (request.getTermParamsMap() != null) {
        queryServiceRequest.setTermParams(request.getTermParamsMap());
      }

      Call<List<ServiceInstance>> listCall = apmAPI.queryServiceInstanceList(queryServiceRequest);
      Response<List<ServiceInstance>> listResponse = listCall.execute();
      if (!listResponse.isSuccessful()) {
        throw new QueryException(listResponse.errorBody().string());
      }

      QueryProto.QueryMetaResponse.Builder builder = QueryProto.QueryMetaResponse.newBuilder();
      listResponse.body().forEach(service -> {
        builder.addMata(ApmConvertor.convertServiceInstance(service));
      });

      return builder.build();
    }, "queryServiceInstanceList", request);
  }

  @Override
  public QueryProto.QueryVirtualComponentResponse queryComponentList(
      QueryProto.QueryMetaRequest request) throws QueryException {
    return wrap(() -> {
      Assert.notNull(request.getServiceName(), "serviceName is null!");
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());

      QueryComponentRequest queryComponentRequest = new QueryComponentRequest();
      queryComponentRequest.setTenant(request.getTenant());
      queryComponentRequest.setServiceName(request.getServiceName());
      queryComponentRequest.setStartTime(request.getStart());
      queryComponentRequest.setEndTime(request.getEnd());
      if (request.getTermParamsMap() != null) {
        queryComponentRequest.setTermParams(request.getTermParamsMap());
      }

      Call<List<VirtualComponent>> listCall = null;

      switch (request.getCategory().toUpperCase()) {
        case "DB":
          listCall = apmAPI.queryDbList(queryComponentRequest);
          break;
        case "CACHE":
          listCall = apmAPI.queryCacheList(queryComponentRequest);
          break;
        case "MQ":
          listCall = apmAPI.queryMQList(queryComponentRequest);
          break;
        default:
          log.warn("[queryComponentList] category not support!");
      }

      Response<List<VirtualComponent>> listResponse = listCall.execute();
      if (!listResponse.isSuccessful()) {
        throw new QueryException(listResponse.errorBody().string());
      }

      QueryProto.QueryVirtualComponentResponse.Builder builder =
          QueryProto.QueryVirtualComponentResponse.newBuilder();
      listResponse.body().forEach(db -> {
        builder.addComponent(ApmConvertor.convertDb(db));
      });

      return builder.build();
    }, "queryComponentList", request);
  }

  @Override
  public QueryProto.TraceIds queryComponentTraceIds(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return wrap(() -> {
      Assert.notNull(request.getServiceName(), "serviceName is null!");
      Assert.notNull(request.getAddress(), "address is null!");
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());

      QueryComponentRequest queryComponentRequest = new QueryComponentRequest();
      queryComponentRequest.setTenant(request.getTenant());
      queryComponentRequest.setServiceName(request.getServiceName());
      queryComponentRequest.setAddress(request.getAddress());
      queryComponentRequest.setStartTime(request.getStart());
      queryComponentRequest.setEndTime(request.getEnd());
      if (request.getTermParamsMap() != null) {
        queryComponentRequest.setTermParams(request.getTermParamsMap());
      }

      Call<List<String>> listCall = apmAPI.queryComponentTraceIds(queryComponentRequest);
      Response<List<String>> listResponse = listCall.execute();
      if (!listResponse.isSuccessful()) {
        throw new QueryException(listResponse.errorBody().string());
      }

      QueryProto.TraceIds.Builder builder = QueryProto.TraceIds.newBuilder();
      builder.addAllTraceId(listResponse.body());

      return builder.build();
    }, "queryComponentTraceIds", request);
  }

  @Override
  public QueryProto.Topology queryTopology(QueryProto.QueryTopologyRequest request)
      throws QueryException {
    return wrap(() -> {
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());

      QueryTopologyRequest queryTopologyRequest = new QueryTopologyRequest();
      queryTopologyRequest.setTenant(request.getTenant());
      queryTopologyRequest.setStartTime(request.getStart());
      queryTopologyRequest.setEndTime(request.getEnd());
      if (request.hasAddress()) {
        queryTopologyRequest.setAddress(request.getAddress());
      }
      if (request.hasServiceName()) {
        queryTopologyRequest.setServiceName(request.getServiceName());
      }
      if (request.hasServiceInstanceName()) {
        queryTopologyRequest.setServiceInstanceName(request.getServiceInstanceName());
      }
      if (request.hasEndpointName()) {
        queryTopologyRequest.setEndpointName(request.getEndpointName());
      }
      if (request.hasDepth()) {
        queryTopologyRequest.setDepth(request.getDepth());
      }
      if (request.getTermParamsMap() != null) {
        queryTopologyRequest.setTermParams(request.getTermParamsMap());
      }

      Call<Topology> topologyCall = null;
      switch (request.getCategory().toUpperCase()) {
        case "TENANT":
          topologyCall = apmAPI.queryTenantTopology(queryTopologyRequest);
          break;
        case "SERVICE":
          topologyCall = apmAPI.queryServiceTopology(queryTopologyRequest);
          break;
        case "SERVICE_INSTANCE":
          topologyCall = apmAPI.queryServiceInstanceTopology(queryTopologyRequest);
          break;
        case "ENDPOINT":
          topologyCall = apmAPI.queryEndpointTopology(queryTopologyRequest);
          break;
        case "DB":
        case "CACHE":
          topologyCall = apmAPI.queryDbTopology(queryTopologyRequest);
          break;
        case "MQ":
          topologyCall = apmAPI.queryMQTopology(queryTopologyRequest);
          break;
        default:
          log.warn("[queryTopology] category not support!");
      }

      Response<Topology> topologyResponse = topologyCall.execute();
      if (!topologyResponse.isSuccessful()) {
        throw new QueryException(topologyResponse.errorBody().string());
      }
      QueryProto.Topology topology = ApmConvertor.convertTopology(topologyResponse.body());

      return topology;
    }, "queryTopology", request);
  }

  @Override
  public QueryProto.QuerySlowSqlResponse querySlowSqlList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return wrap(() -> {
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());

      QueryComponentRequest queryComponentRequest = new QueryComponentRequest();
      queryComponentRequest.setTenant(request.getTenant());
      queryComponentRequest.setServiceName(request.getServiceName());
      queryComponentRequest.setAddress(request.getAddress());
      queryComponentRequest.setStartTime(request.getStart());
      queryComponentRequest.setEndTime(request.getEnd());
      if (request.getTermParamsMap() != null) {
        queryComponentRequest.setTermParams(request.getTermParamsMap());
      }

      Call<List<SlowSql>> listCall = apmAPI.querySlowSqlList(queryComponentRequest);
      Response<List<SlowSql>> listResponse = listCall.execute();
      if (!listResponse.isSuccessful()) {
        throw new QueryException(listResponse.errorBody().string());
      }

      QueryProto.QuerySlowSqlResponse.Builder builder =
          QueryProto.QuerySlowSqlResponse.newBuilder();
      listResponse.body().forEach(slowSql -> {
        builder.addSlowSql(ApmConvertor.convertSlowSql(slowSql));
      });

      return builder.build();
    }, "querySlowSqlList", request);
  }

  @Override
  public QueryProto.CommonMapTypeDataList queryServiceErrorList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return wrap(() -> {
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());

      QueryServiceRequest queryServiceRequest = new QueryServiceRequest();
      queryServiceRequest.setTenant(request.getTenant());
      queryServiceRequest.setServiceName(request.getServiceName());
      queryServiceRequest.setStartTime(request.getStart());
      queryServiceRequest.setEndTime(request.getEnd());
      if (request.getTermParamsMap() != null) {
        queryServiceRequest.setTermParams(request.getTermParamsMap());
      }
      Call<List<Map<String, String>>> serviceList =
          apmAPI.queryServiceErrorList(queryServiceRequest);
      Response<List<Map<String, String>>> listResponse = serviceList.execute();
      if (!listResponse.isSuccessful()) {
        throw new QueryException(listResponse.errorBody().string());
      }
      QueryProto.CommonMapTypeDataList.Builder builder =
          QueryProto.CommonMapTypeDataList.newBuilder();
      listResponse.body().forEach(data -> {
        QueryProto.CommonMapTypeData.Builder tmpBuilder = QueryProto.CommonMapTypeData.newBuilder();
        tmpBuilder.putAllData(data);
        builder.addCommonMapTypeData(tmpBuilder.build());
      });

      return builder.build();
    }, "queryServiceErrorList", request);
  }

  @Override
  public QueryProto.CommonMapTypeDataList queryServiceErrorDetail(
      QueryProto.QueryMetaRequest request) throws QueryException {
    return wrap(() -> {
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());

      QueryServiceRequest queryServiceRequest = new QueryServiceRequest();
      queryServiceRequest.setTenant(request.getTenant());
      queryServiceRequest.setServiceName(request.getServiceName());
      queryServiceRequest.setStartTime(request.getStart());
      queryServiceRequest.setEndTime(request.getEnd());
      if (request.getTermParamsMap() != null) {
        queryServiceRequest.setTermParams(request.getTermParamsMap());
      }

      Call<List<Map<String, String>>> serviceList =
          apmAPI.queryServiceErrorDetail(queryServiceRequest);
      Response<List<Map<String, String>>> listResponse = serviceList.execute();
      if (!listResponse.isSuccessful()) {
        throw new QueryException(listResponse.errorBody().string());
      }
      QueryProto.CommonMapTypeDataList.Builder builder =
          QueryProto.CommonMapTypeDataList.newBuilder();
      listResponse.body().forEach(data -> {
        QueryProto.CommonMapTypeData.Builder tmpBuilder = QueryProto.CommonMapTypeData.newBuilder();
        tmpBuilder.putAllData(data);
        builder.addCommonMapTypeData(tmpBuilder.build());
      });

      return builder.build();
    }, "queryServiceErrorDetail", request);
  }

  @Override
  public QueryProto.QueryDetailResponse queryDetailData(QueryProto.QueryRequest request)
      throws QueryException {
    return wrap(() -> {
      List<QueryProto.Datasource> datasources = request.getDatasourcesList();
      Assert.notEmpty(datasources, "datasources are empty");
      String tenant = request.getTenant();

      List<QueryProto.QueryDetailResponse> rsps = new ArrayList<>(datasources.size());
      for (QueryProto.Datasource datasource : datasources) {
        QueryProto.QueryDetailResponse.Builder queryResponseBuilder =
            queryDetail(tenant, datasource);
        rsps.add(queryResponseBuilder.build());
      }
      List<QueryProto.DetailResult> results =
          rsps.stream().flatMap(rsp -> rsp.getResultsList().stream()).collect(Collectors.toList());
      return QueryProto.QueryDetailResponse.newBuilder().addAllResults(results).build();
    }, "queryDetailData", request);
  }

  @Override
  public QueryProto.QueryEventResponse queryEvents(QueryProto.QueryEventRequest request)
      throws QueryException {
    return wrap(() -> {
      ApmAPI apmAPI = apmClient.getClient(request.getTenant());

      Request queryRequest = new Request();
      queryRequest.setTenant(request.getTenant());
      queryRequest.setStartTime(request.getStart());
      queryRequest.setEndTime(request.getEnd());
      if (request.getTermParamsMap() != null) {
        queryRequest.setTermParams(request.getTermParamsMap());
      }

      Call<List<Event>> events = apmAPI.queryEvents(queryRequest);
      Response<List<Event>> listResponse = events.execute();
      if (!listResponse.isSuccessful()) {
        throw new QueryException(listResponse.errorBody().string());
      }
      QueryProto.QueryEventResponse.Builder builder = QueryProto.QueryEventResponse.newBuilder();
      listResponse.body().forEach(data -> {
        builder.addEvent(ApmConvertor.convertEvent(data));
      });

      return builder.build();
    }, "queryEvents", request);
  }

  private QueryProto.QueryResponse simpleQuery(QueryProto.QueryRequest request, String tenant,
      List<QueryProto.Datasource> datasources) throws QueryException {
    List<QueryProto.QueryResponse> rsps = new ArrayList<>(datasources.size());
    for (QueryProto.Datasource datasource : datasources) {
      QueryProto.QueryResponse.Builder queryResponseBuilder = queryDs(request, tenant, datasource);
      rsps.add(queryResponseBuilder.build());
    }
    List<QueryProto.Result> results =
        rsps.stream().flatMap(rsp -> rsp.getResultsList().stream()).collect(Collectors.toList());
    return QueryProto.QueryResponse.newBuilder().addAllResults(results).build();
  }

  private QueryProto.QueryResponse complexQuery(QueryProto.QueryRequest request, String tenant,
      String expression, String as, String downsample, String fillPolicy,
      List<QueryProto.Datasource> datasources) throws QueryException {
    List<String> queryExprs = Lists.newArrayList(StringUtils.split(expression, ","));
    List<String> queryAs = Lists.newArrayList(StringUtils.split(as, ","));
    Map<String, String> asMap = new HashMap<>();
    if (CollectionUtils.size(queryExprs) == CollectionUtils.size(queryAs)) {
      for (int i = 0; i < CollectionUtils.size(queryExprs); i++) {
        asMap.put(queryExprs.get(i), queryAs.get(i));
      }
    }
    QueryProto.QueryResponse.Builder rspBuilder = QueryProto.QueryResponse.newBuilder();
    List<QueryProto.Result> results = new ArrayList<>();

    List<String> dsNames =
        datasources.stream().map(QueryProto.Datasource::getName).collect(Collectors.toList());
    Map<String, List<QueryProto.Result>> resultMap = new HashMap<>();
    for (QueryProto.Datasource datasource : datasources) {
      String dsName = datasource.getName();
      List<QueryProto.Result> rspResults = queryDs(request, tenant, datasource).getResultsList();
      resultMap.put(dsName, rspResults);
    }

    List<String> singleQueryExprs = queryExprs.stream()
        .filter(qe -> new RpnResolver().expr2Infix(qe).size() == 1).collect(Collectors.toList());
    for (String singleQueryExpr : singleQueryExprs) {
      queryExprs.remove(singleQueryExpr);
      List<QueryProto.Result> resultList = resultMap.get(singleQueryExpr);
      results.addAll(resultList);
    }
    // simpleQuery(tenant, datasources);

    for (String queryExpr : queryExprs) {
      results.addAll(
          calculate(queryExpr, resultMap, asMap.getOrDefault(queryExpr, queryExpr), dsNames));
    }
    rspBuilder.addAllResults(results);
    if (CollectionUtils.isNotEmpty(datasources) && StringUtils.isNotEmpty(downsample)
        && StringUtils.isNotEmpty(fillPolicy)) {
      QueryProto.Datasource ds = datasources.get(0);
      fillData(rspBuilder, ds.getStart(), ds.getEnd(), downsample, fillPolicy);
    }
    return rspBuilder.build();
  }

  private List<QueryProto.Result> calculate(String queryExpr,
      Map<String, List<QueryProto.Result>> resultMap, String as, Collection<String> dsNames) {
    List<QueryProto.Result> results = new ArrayList<>();
    Map<Map, Map<Long, Map<String, Double>>> detailMap = new HashMap<>();
    resultMap.forEach((dsName, rspResults) -> {
      for (QueryProto.Result result : rspResults) {
        Map<String, String> tags = result.getTagsMap();
        List<QueryProto.Point> points = result.getPointsList();
        for (QueryProto.Point point : points) {
          Long timestamp = point.getTimestamp();
          Double value = point.getValue();
          detailMap.computeIfAbsent(tags, _0 -> new TreeMap<>())
              .computeIfAbsent(timestamp, _1 -> new HashMap<>()).put(dsName, value);
        }
      }
    });
    RpnResolver rpnResolver = new RpnResolver();
    List<String> queryExprArgs = rpnResolver.expr2Infix(queryExpr);
    detailMap.forEach((tags, map0) -> {
      QueryProto.Result.Builder resultBuilder = QueryProto.Result.newBuilder();
      resultBuilder.setMetric(as).putAllTags(tags);
      map0.forEach((timestamp, map2) -> {
        dsNames.forEach(dsName -> map2.putIfAbsent(dsName, 0d));
        List rpnArgs = queryExprArgs.stream().map(expr -> {
          if (NumberUtils.isCreatable(expr)) {
            return Double.parseDouble(expr);
          } else if (map2.containsKey(expr)) {
            return map2.get(expr);
          } else {
            return expr;
          }
        }).collect(Collectors.toList());
        try {
          Double rpnResult = rpnResolver.calByInfix(rpnArgs);
          Builder pointBuilder = QueryProto.Point.newBuilder();
          pointBuilder.setTimestamp(timestamp).setValue(rpnResult);
          QueryProto.Point point = pointBuilder.build();
          resultBuilder.addPoints(point);
        } catch (Exception e) {
          // disable this point and expect data to be filled from the outer layer
        }
      });
      QueryProto.Result result = resultBuilder.build();
      results.add(result);
    });
    return results;
  }

  protected QueryProto.QueryResponse.Builder queryDs(QueryProto.QueryRequest request, String tenant,
      QueryProto.Datasource datasource) throws QueryException {
    MetricDefine apmMetric = this.apmMetrics.getUnchecked(tenant).get(datasource.getMetric());
    // virtual metric
    if (superCacheService.getSc() != null && superCacheService.getSc().expressionMetricList != null
        && superCacheService.getSc().expressionMetricList.containsKey(datasource.getMetric())) {
      QueryProto.QueryRequest.Builder template =
          superCacheService.getSc().expressionMetricList.get(datasource.getMetric()).toBuilder();
      return queryVirtual(template, request, tenant, datasource);
    } else if (apmMetric != null) {
      return queryApm(tenant, datasource, apmMetric);
    } else if (AggCenter.isAggregator(datasource.getAggregator())) {
      return analysis(tenant, datasource);
    } else {
      return queryMetricStore(tenant, datasource);
    }
  }

  private QueryProto.QueryResponse.Builder queryVirtual(QueryProto.QueryRequest.Builder template,
      QueryProto.QueryRequest request, String tenant, QueryProto.Datasource datasource)
      throws QueryException {
    template.setTenant(tenant);
    if (StringUtils.isNotBlank(datasource.getDownsample())) {
      template.setDownsample(datasource.getDownsample());
    }
    for (QueryProto.Datasource.Builder dsTemplate : template.getDatasourcesBuilderList()) {
      dsTemplate.setStart(datasource.getStart());
      dsTemplate.setEnd(datasource.getEnd());
      if (CollectionUtils.isNotEmpty(datasource.getFiltersList())) {
        dsTemplate.clearFilters().addAllFilters(datasource.getFiltersList());
      }
      dsTemplate.setDownsample(datasource.getDownsample());
      dsTemplate.setSlidingWindow(datasource.getSlidingWindow());
      if (CollectionUtils.isNotEmpty(datasource.getGroupByList())) {
        dsTemplate.clearGroupBy().addAllGroupBy(datasource.getGroupByList());
      }
    }
    QueryProto.QueryResponse.Builder rspBuilder = queryData(template.build()).toBuilder();
    for (QueryProto.Result.Builder resultBuilder : rspBuilder.getResultsBuilderList()) {
      resultBuilder.setMetric(request.getQuery());
    }
    return rspBuilder;
  }

  private QueryProto.QueryResponse.Builder queryApm(String tenant, QueryProto.Datasource datasource,
      MetricDefine metricDefine) throws QueryException {
    if (datasource.getApmMaterialized()) {
      if (metricDefine.isMaterialized()) {
        return queryMetricStore(tenant, datasource);
      } else if (StringUtils.isNotEmpty(metricDefine.getMaterializedExp())) {
        List<String> exprs = new RpnResolver().expr2Infix(metricDefine.getMaterializedExp());
        Map<String, List<QueryProto.Result>> resultMap = new HashMap<>();
        for (String expr : exprs) {
          String func = "none";
          String metricName = expr;
          int leftBracket = expr.indexOf("{");
          int rightBracket = expr.indexOf("}");
          if (leftBracket != -1 && rightBracket != -1 && leftBracket < rightBracket) {
            func = expr.substring(0, leftBracket);
            metricName = expr.substring(leftBracket + 1, rightBracket);
          }
          MetricDefine argMetricDefine = this.apmMetrics.getUnchecked(tenant).get(metricName);
          if (argMetricDefine != null) {
            QueryProto.Datasource.Builder argDsBuilder = datasource.toBuilder();
            argDsBuilder.setMetric(metricName);
            argDsBuilder.setName(expr);
            argDsBuilder.setAggregator(func);
            if (CollectionUtils.isNotEmpty(metricDefine.getGroups())) {
              List<String> groups = metricDefine.getGroups().stream()
                  .map(group -> OtlpMappings.fromOtlp(metricDefine.getIndex(), group))
                  .collect(Collectors.toList());
              argDsBuilder.addAllGroupBy(groups);
            }
            List<QueryProto.Result> dsResults =
                queryApm(tenant, argDsBuilder.build(), argMetricDefine).getResultsList();
            resultMap.put(argDsBuilder.getName(), dsResults);
          }
        }
        QueryProto.QueryResponse.Builder rspBuilder = QueryProto.QueryResponse.newBuilder();
        List<QueryProto.Result> results = calculate(metricDefine.getMaterializedExp(), resultMap,
            StringUtils.isNotEmpty(datasource.getName()) ? datasource.getName()
                : datasource.getMetric(),
            resultMap.keySet());
        rspBuilder.addAllResults(results);
        if (StringUtils.isNotEmpty(datasource.getDownsample())
            && StringUtils.isNotEmpty(datasource.getFillPolicy())) {
          fillData(rspBuilder, datasource.getStart(), datasource.getEnd(),
              datasource.getDownsample(), datasource.getFillPolicy());
        }
        return rspBuilder;
      }
    }
    return queryApmFromSearchEngine(tenant, datasource);
  }

  private QueryProto.QueryResponse.Builder queryApmFromSearchEngine(String tenant,
      QueryProto.Datasource datasource) throws QueryException {
    return wrap(() -> {
      String metric = datasource.getMetric();
      long start = datasource.getStart();
      long end = datasource.getEnd();
      String step =
          StringUtils.isEmpty(datasource.getDownsample()) ? "1m" : datasource.getDownsample();
      List<QueryProto.QueryFilter> filters = datasource.getFiltersList();
      Map<String, Object> conditions = new HashMap<>();
      filters.forEach(filter -> {
        if ("literal".equals(filter.getType())) {
          conditions.put(filter.getName(), filter.getValue());
        } else if ("literal_or".equals(filter.getType())) {
          conditions.put(filter.getName(),
              Arrays.asList(StringUtils.split(filter.getValue(), "|")));
        }
      });
      ApmAPI apmAPI = apmClient.getClient(tenant);
      QueryMetricRequest request =
          new QueryMetricRequest(tenant, metric, new Duration(start, end, step), conditions);
      Call<MetricValues> call = apmAPI.queryMetricData(request);
      Response<MetricValues> metricValuesRsp = call.execute();
      if (!metricValuesRsp.isSuccessful()) {
        throw new QueryException(metricValuesRsp.errorBody().string());
      }
      MetricValues metricValues = metricValuesRsp.body();
      QueryProto.QueryResponse.Builder builder = QueryProto.QueryResponse.newBuilder();
      if (metricValues != null && CollectionUtils.isNotEmpty(metricValues.getValues())) {
        List<MetricValue> values = metricValues.getValues();
        for (MetricValue value : values) {
          QueryProto.Result.Builder resultBuilder = QueryProto.Result.newBuilder();
          resultBuilder.setMetric(
              StringUtils.isNotEmpty(datasource.getName()) ? datasource.getName() : metric);
          resultBuilder.putAllTags(value.getTags());
          if (value.getValues() != null) {
            Map<Long, Double> sorted = new TreeMap<>(value.getValues());
            sorted.forEach((t, v) -> resultBuilder
                .addPoints(QueryProto.Point.newBuilder().setTimestamp(t).setValue(v).build()));
          }
          builder.addResults(resultBuilder);
        }
      }
      String fillPolicy = datasource.getFillPolicy();
      if (StringUtils.isNotEmpty(fillPolicy) && !StringUtils.equalsIgnoreCase(fillPolicy, "none")
          && StringUtils.isNotEmpty(datasource.getDownsample())) {
        fillData(builder, datasource.getStart(), datasource.getEnd(), datasource.getDownsample(),
            fillPolicy);
      }
      return builder;
    });
  }

  private Map<String, MetricDefine> listApmMetrics(String tenant) {
    try {
      ApmAPI apmAPI = apmClient.getClient(tenant);
      if (apmAPI == null) {
        return new HashMap<>();
      }
      Call<List<MetricDefine>> call = apmAPI.listMetricDefines();
      Response<List<MetricDefine>> metricsRsp = call.execute();
      if (!metricsRsp.isSuccessful()) {
        throw new QueryException(metricsRsp.errorBody().string());
      }
      return metricsRsp.body().stream()
          .collect(Collectors.toMap(MetricDefine::getName, Function.identity()));
    } catch (Exception e) {
      log.error("[apm] list metrics failed, tenant={}", tenant, e);
      return new HashMap<>();
    }
  }

  private QueryProto.QueryResponse.Builder queryMetricStore(String tenant,
      QueryProto.Datasource datasource) {
    QueryParam param = QueryStorageUtils.convertToQueryParam(tenant, datasource);
    List<Result> results = metricStorage.queryData(param);

    QueryProto.QueryResponse.Builder builder = QueryProto.QueryResponse.newBuilder();
    for (Result result : results) {
      QueryProto.Result.Builder resultBuilder = QueryProto.Result.newBuilder();
      resultBuilder.setMetric(
          StringUtils.isNotEmpty(datasource.getName()) ? datasource.getName() : result.getMetric())
          .putAllTags(result.getTags());
      for (Point point : result.getPoints()) {
        Builder pointBuilder = QueryProto.Point.newBuilder().setTimestamp(point.getTimestamp());

        if (point.getStrValue() != null) {
          pointBuilder.setStrValue(String.valueOf(point.getStrValue()));
        } else {
          pointBuilder.setValue(point.getValue());
        }
        resultBuilder.addPoints(pointBuilder);
      }

      builder.addResults(resultBuilder);
    }
    String fillPolicy = datasource.getFillPolicy();
    if (StringUtils.isNotEmpty(fillPolicy) && !StringUtils.equalsIgnoreCase(fillPolicy, "none")
        && StringUtils.isNotEmpty(datasource.getDownsample())) {
      fillData(builder, datasource.getStart(), datasource.getEnd(), datasource.getDownsample(),
          fillPolicy);
    }
    return builder;
  }

  private QueryProto.QueryDetailResponse.Builder queryDetail(String tenant,
      QueryProto.Datasource datasource) {
    QueryParam param = QueryStorageUtils.convertToQueryParam(tenant, datasource);
    DetailResult detailResult = metricStorage.queryDetail(param);
    QueryProto.QueryDetailResponse.Builder builder = QueryProto.QueryDetailResponse.newBuilder();
    QueryProto.DetailResult.Builder detailResultBuilder = QueryProto.DetailResult.newBuilder();
    if (detailResult == null || detailResult.isEmpty()) {
      log.info("detailResult is empty for {}", J.toJson(datasource));
      return builder;
    }
    detailResultBuilder.addAllHeaders(detailResult.getHeaders()) //
        .addAllTables(detailResult.getTables()) //
        .setSql(detailResult.getSql());
    for (DetailResult.DetailRow detailRow : detailResult.getPoints()) {
      if (CollectionUtils.isEmpty(detailRow.getValues())) {
        continue;
      }
      QueryProto.DetailRow.Builder resultBuilder = QueryProto.DetailRow.newBuilder();
      for (DetailResult.Value value : detailRow.getValues()) {
        QueryProto.DetailValue.Builder valueBuilder = QueryProto.DetailValue.newBuilder();
        valueBuilder.setType(value.getType().name());
        switch (value.getType()) {
          case String:
            valueBuilder.setStrValue(getStrValue(value.getValue()));
            break;
          case Timestamp:
            valueBuilder.setTimestampValue(getLongValue(value.getValue()));
            break;
          case Double:
            valueBuilder.setDoubleValue(getDoubleValue(value.getValue()));
            break;
          case Boolean:
            valueBuilder.setBoolValue(getBoolValue(value.getValue()));
            break;
        }
        resultBuilder.addValues(valueBuilder.build());
      }
      detailResultBuilder.addRows(resultBuilder.build());
    }
    builder.addResults(detailResultBuilder.build());
    return builder;
  }

  private boolean getBoolValue(Object value) {
    if (value instanceof Boolean) {
      return (boolean) value;
    }
    return false;
  }

  private double getDoubleValue(Object value) {
    if (value instanceof Number) {
      return ((Number) value).doubleValue();
    }
    return 0;
  }

  private long getLongValue(Object value) {
    if (value instanceof Number) {
      return ((Number) value).longValue();
    }
    return 0L;
  }

  private String getStrValue(Object value) {
    if (value instanceof String) {
      return (String) value;
    }
    return StringUtils.EMPTY;
  }

  private QueryProto.QueryResponse.Builder analysis(String tenant,
      QueryProto.Datasource datasource) {

    QueryParam queryParam = QueryStorageUtils.convertToQueryParam(tenant, datasource);
    queryParam.setAggregator("none");

    List<Result> results = metricStorage.queryData(queryParam);

    QueryProto.QueryResponse.Builder builder = QueryProto.QueryResponse.newBuilder();
    Map<Map<String, String>, QueryProto.Result.Builder> tagsResults = new HashMap<>();

    Map<Long, List<Pair<Map<String, String>, Mergeable>>> detailMap = new HashMap<>();

    String aggregator = datasource.getAggregator();

    for (val result : results) {
      Map<String, String> tags = result.getTags();
      val points = result.getPoints();
      for (val point : points) {
        long timestamp = point.getTimestamp();
        String value = point.getStrValue();
        Mergeable mergeable = AggCenter.parseMergeable(tags, value, aggregator);
        if (mergeable != null) {
          detailMap.computeIfAbsent(timestamp, _0 -> new ArrayList<>())
              .add(Pair.of(tags, mergeable));
        }
      }
    }

    detailMap.forEach((timestamp, pairs) -> {
      Map<Map<String, String>, Optional<Pair<Map<String, String>, Mergeable>>> reducedByTags =
          pairs.stream().collect(Collectors.groupingBy(pair -> {
            Map resultTags = new HashMap();
            if (CollectionUtils.isNotEmpty(queryParam.getGroupBy())) {
              queryParam.getGroupBy().forEach(gb -> resultTags.put(gb, pair.getLeft().get(gb)));
            }
            return resultTags;
          }, Collectors.reducing((pair1, pair2) -> {
            pair1.getValue().merge(pair2.getValue());
            return pair1;
          })));
      reducedByTags.forEach((tags, vals) -> {
        QueryProto.Result.Builder resultBuilder = tagsResults.computeIfAbsent(tags,
            k -> QueryProto.Result.newBuilder()
                .setMetric(StringUtils.isNotEmpty(datasource.getName()) ? datasource.getName()
                    : datasource.getMetric())
                .putAllTags(tags));
        Builder pointBuilder = QueryProto.Point.newBuilder().setTimestamp(timestamp)
            .setStrValue(GsonUtils.toJson(vals.get().getRight()));
        resultBuilder.addPoints(pointBuilder);
      });
    });
    tagsResults.values().forEach(builder::addResults);
    return builder;
  }

  private void fillData(QueryProto.QueryResponse.Builder rspBuilder, long start, long end,
      String downsample, String fillPolicy) {
    rspBuilder.getResultsBuilderList().forEach(resultBuilder -> {
      List<Builder> pointBuilders = resultBuilder.getPointsBuilderList();
      Map<Long, Builder> timePointMap = new TreeMap<>(pointBuilders.stream()
          .collect(Collectors.toMap(Builder::getTimestamp, Function.identity())));
      long sample = DurationUtil.parse(downsample).toMillis();
      for (long period =
          start % sample == 0 ? start : start / sample * sample + sample; period <= end; period +=
              sample) {
        if (!timePointMap.containsKey(period)) {
          Builder builder = QueryProto.Point.newBuilder().setTimestamp(period);
          Object filledVal = QueryStorageUtils.convertFillPolocy(fillPolicy);
          if (filledVal instanceof Number) {
            builder.setValue(((Number) filledVal).doubleValue());
          } else {
            builder.setStrValue(String.valueOf(filledVal));
          }
          timePointMap.put(period, builder);
        }
      }
      resultBuilder.clearPoints();
      timePointMap.values().forEach(resultBuilder::addPoints);
    });
  }

  protected List<QueryProto.Result> searchTags(String tenant, QueryProto.Datasource datasource)
      throws QueryException {
    try {
      List<QueryProto.Result> pbResults = new ArrayList<>();
      QueryParam param = QueryStorageUtils.convertToQueryParam(tenant, datasource);
      List<Result> results = metricStorage.queryTags(param);
      for (Result result : results) {
        pbResults.add(QueryStorageUtils.convertToPb(result));
      }
      return pbResults;
    } catch (Exception e) {
      throw new QueryException(e.getMessage(), e);
    }
  }

  /**
   * wrap exception to QueryException
   *
   * @param call
   * @param mark
   * @param request
   * @param <T>
   * @return
   * @throws QueryException
   */
  protected static <T> T wrap(Callable<T> call, String mark, MessageOrBuilder request)
      throws QueryException {
    try {
      return call.call();
    } catch (Exception e) {
      if (e instanceof QueryException) {
        throw (QueryException) e;
      }
      log.error("[{}] query fail, request={}", mark, ProtoJsonUtils.toJson(request), e);
      throw new QueryException(e.getMessage(), e);
    }
  }

  protected static <T> T wrap(Callable<T> call) throws QueryException {
    try {
      return call.call();
    } catch (Exception e) {
      if (e instanceof QueryException) {
        throw (QueryException) e;
      }
      throw new QueryException(e.getMessage(), e);
    }
  }
}
