/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;
import com.google.protobuf.MessageOrBuilder;

import io.holoinsight.server.common.ProtoJsonUtils;
import io.holoinsight.server.extension.MetricStorage;
import io.holoinsight.server.extension.model.QueryMetricsParam;
import io.holoinsight.server.extension.model.QueryParam;
import io.holoinsight.server.query.common.RpnResolver;
import io.holoinsight.server.query.grpc.QueryProto;
import io.holoinsight.server.query.service.QueryException;
import io.holoinsight.server.query.service.QueryService;
import io.holoinsight.server.query.service.analysis.AnalysisCenter;
import io.holoinsight.server.query.service.analysis.Mergable;
import io.holoinsight.server.storage.common.utils.GsonUtils;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultQueryServiceImpl implements QueryService {

  @Autowired
  protected MetricStorage metricStorage;


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
        return simpleQuery(tenant, datasources);
      } else {
        return complexQuery(tenant, query, downsample, fillPolicy, datasources);
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
      String tenant = request.getTenant();
      Map<String, Set<String>> keys = new HashMap<>();
      List<QueryProto.Datasource> datasources = request.getDatasourcesList();
      for (QueryProto.Datasource ds : datasources) {
        List<QueryProto.Result> results = searchTags(tenant, ds);
        results.forEach(r -> keys.computeIfAbsent(r.getMetric(), __ -> new HashSet<>())
            .addAll(r.getTagsMap().keySet()));
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
      QueryMetricsParam param = QueryStorageUtils.convertToQueryMetricsParam(request);
      List<String> suggestRsp = metricStorage.queryMetrics(param);

      QueryProto.QueryMetricsResponse.Builder b = QueryProto.QueryMetricsResponse.newBuilder();
      if (CollectionUtils.isNotEmpty(suggestRsp)) {
        for (String metric : suggestRsp) {
          QueryProto.MetricResult mr = QueryProto.MetricResult.newBuilder().setName(metric).build();
          b.addResults(mr);
        }
      }
      return b.build();
    }, "queryMetrics", request);
  }

  @Override
  public QueryProto.QueryResponse pqlInstantQuery(QueryProto.PqlInstantRequest request)
      throws QueryException {
    throw new UnsupportedOperationException();
  }

  @Override
  public QueryProto.QueryResponse pqlRangeQuery(QueryProto.PqlRangeRequest request)
      throws QueryException {
    throw new UnsupportedOperationException();
  }

  @Override
  public QueryProto.TraceBrief queryBasicTraces(QueryProto.QueryTraceRequest request)
      throws QueryException {
    return QueryProto.TraceBrief.getDefaultInstance();
  }

  @Override
  public QueryProto.Trace queryTrace(QueryProto.QueryTraceRequest request) throws QueryException {
    return QueryProto.Trace.getDefaultInstance();
  }

  @Override
  public QueryProto.QueryMetaResponse queryServiceList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return QueryProto.QueryMetaResponse.getDefaultInstance();
  }

  @Override
  public QueryProto.QueryMetaResponse queryEndpointList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return QueryProto.QueryMetaResponse.getDefaultInstance();
  }

  @Override
  public QueryProto.QueryMetaResponse queryServiceInstanceList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return QueryProto.QueryMetaResponse.getDefaultInstance();
  }

  @Override
  public QueryProto.QueryVirtualComponentResponse queryComponentList(
      QueryProto.QueryMetaRequest request) throws QueryException {
    return QueryProto.QueryVirtualComponentResponse.getDefaultInstance();
  }

  @Override
  public QueryProto.TraceIds queryComponentTraceIds(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return QueryProto.TraceIds.getDefaultInstance();
  }

  @Override
  public QueryProto.Topology queryTopology(QueryProto.QueryTopologyRequest request)
      throws QueryException {
    return QueryProto.Topology.getDefaultInstance();
  }

  @Override
  public QueryProto.BizopsEndpoints queryBizEndpointList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return QueryProto.BizopsEndpoints.getDefaultInstance();
  }

  @Override
  public QueryProto.BizopsEndpoints queryBizErrorCodeList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return QueryProto.BizopsEndpoints.getDefaultInstance();
  }

  @Override
  public QueryProto.QuerySlowSqlResponse querySlowSqlList(QueryProto.QueryMetaRequest request)
      throws QueryException {
    return QueryProto.QuerySlowSqlResponse.getDefaultInstance();
  }

  private QueryProto.QueryResponse simpleQuery(String tenant,
      List<QueryProto.Datasource> datasources) throws QueryException {
    List<QueryProto.QueryResponse> rsps = new ArrayList<>(datasources.size());
    for (QueryProto.Datasource datasource : datasources) {
      QueryProto.QueryResponse.Builder queryResponseBuilder = queryDs(tenant, datasource);
      rsps.add(queryResponseBuilder.build());
    }
    List<QueryProto.Result> results =
        rsps.stream().flatMap(rsp -> rsp.getResultsList().stream()).collect(Collectors.toList());
    return QueryProto.QueryResponse.newBuilder().addAllResults(results).build();
  }

  private QueryProto.QueryResponse complexQuery(String tenant, String query, String downsample,
      String fillPolicy, List<QueryProto.Datasource> datasources) throws QueryException {
    List<String> queryExprs = Lists.newArrayList(StringUtils.split(query, ","));
    Map<String, QueryProto.Datasource> dsMap = datasources.stream()
        .collect(Collectors.toMap(QueryProto.Datasource::getName, Function.identity()));
    QueryProto.QueryResponse.Builder rspBuilder = QueryProto.QueryResponse.newBuilder();
    List<QueryProto.Result> results = new ArrayList<>();
    List<String> singleQueryExprs = queryExprs.stream()
        .filter(qe -> new RpnResolver().expr2Infix(qe).size() == 1).collect(Collectors.toList());
    for (String singleQueryExpr : singleQueryExprs) {
      queryExprs.remove(singleQueryExpr);
      QueryProto.Datasource ds = dsMap.get(singleQueryExpr);
      QueryProto.QueryResponse.Builder builder = queryDs(tenant, ds);
      results.addAll(builder.getResultsList());
    }
    // simpleQuery(tenant, datasources);
    for (String queryExpr : queryExprs) {
      RpnResolver rpnResolver = new RpnResolver();
      List<String> queryExprArgs = rpnResolver.expr2Infix(queryExpr);

      List<String> dsNames =
          datasources.stream().map(QueryProto.Datasource::getName).collect(Collectors.toList());
      Map<Map, Map<Long, Map<String, Double>>> detailMap = new HashMap<>();
      for (QueryProto.Datasource datasource : datasources) {
        String dsName = datasource.getName();
        List<QueryProto.Result> rspResults = queryDs(tenant, datasource).getResultsList();
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
      }

      detailMap.forEach((tags, map0) -> {
        QueryProto.Result.Builder resultBuilder = QueryProto.Result.newBuilder();
        resultBuilder.setMetric(queryExpr).putAllTags(tags);
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
          Double rpnResult = rpnResolver.calByInfix(rpnArgs);
          QueryProto.Point.Builder pointBuilder = QueryProto.Point.newBuilder();
          pointBuilder.setTimestamp(timestamp).setValue(rpnResult);
          QueryProto.Point point = pointBuilder.build();
          resultBuilder.addPoints(point);
        });
        QueryProto.Result result = resultBuilder.build();
        results.add(result);
      });
    }
    rspBuilder.addAllResults(results);
    if (CollectionUtils.isNotEmpty(datasources) && StringUtils.isNotEmpty(downsample)
        && StringUtils.isNotEmpty(fillPolicy)) {
      QueryProto.Datasource ds = datasources.get(0);
      fillData(rspBuilder, ds.getStart(), ds.getEnd(), downsample, fillPolicy);
    }
    return rspBuilder.build();
  }

  protected QueryProto.QueryResponse.Builder queryDs(String tenant,
      QueryProto.Datasource datasource) throws QueryException {
    if (AnalysisCenter.isAnalysis(datasource.getAggregator())) {
      return analysis(tenant, datasource);
    } else {
      return queryMetricStore(tenant, datasource);
    }
  }


  private QueryProto.QueryResponse.Builder queryMetricStore(String tenant,
      QueryProto.Datasource datasource) {

    QueryParam param = QueryStorageUtils.convertToQueryParam(tenant, datasource);
    List<io.holoinsight.server.extension.model.QueryResult.Result> results =
        metricStorage.queryData(param);

    QueryProto.QueryResponse.Builder builder = QueryProto.QueryResponse.newBuilder();
    for (io.holoinsight.server.extension.model.QueryResult.Result result : results) {
      QueryProto.Result.Builder resultBuilder = QueryProto.Result.newBuilder();
      resultBuilder.setMetric(
          StringUtils.isNotEmpty(datasource.getName()) ? datasource.getName() : result.getMetric())
          .putAllTags(result.getTags());
      for (io.holoinsight.server.extension.model.QueryResult.Point point : result.getPoints()) {
        QueryProto.Point.Builder pointBuilder =
            QueryProto.Point.newBuilder().setTimestamp(point.getTimestamp());

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

  private QueryProto.QueryResponse.Builder analysis(String tenant,
      QueryProto.Datasource datasource) {

    QueryParam queryParam = QueryStorageUtils.convertToQueryParam(tenant, datasource);
    List<io.holoinsight.server.extension.model.QueryResult.Result> results =
        metricStorage.queryData(queryParam);

    QueryProto.QueryResponse.Builder builder = QueryProto.QueryResponse.newBuilder();

    Map<Long, List<Pair<Map<String, String>, Mergable>>> detailMap = new HashMap<>();

    String aggregator = datasource.getAggregator();

    for (val result : results) {
      Map<String, String> tags = result.getTags();
      val points = result.getPoints();
      for (val point : points) {
        long timestamp = point.getTimestamp();
        String value = point.getStrValue();
        Mergable mergable = AnalysisCenter.parseAnalysis(tags, value, aggregator);
        if (mergable != null) {
          detailMap.computeIfAbsent(timestamp, _0 -> new ArrayList<>())
              .add(Pair.of(tags, mergable));
        }
      }
    }

    detailMap.forEach((timestamp, pairs) -> {
      Map<Map<String, String>, Optional<Pair<Map<String, String>, Mergable>>> reducedByTags =
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
        QueryProto.Result.Builder resultBuilder = QueryProto.Result.newBuilder();
        resultBuilder.setMetric(StringUtils.isNotEmpty(datasource.getName()) ? datasource.getName()
            : datasource.getMetric()).putAllTags(tags);
        QueryProto.Point.Builder pointBuilder = QueryProto.Point.newBuilder()
            .setTimestamp(timestamp).setStrValue(GsonUtils.toJson(vals.get().getRight()));
        resultBuilder.addPoints(pointBuilder);
        builder.addResults(resultBuilder);
      });
    });

    return builder;
  }

  private void fillData(QueryProto.QueryResponse.Builder rspBuilder, long start, long end,
      String downsample, String fillPolicy) {
    rspBuilder.getResultsBuilderList().forEach(resultBuilder -> {
      List<QueryProto.Point.Builder> pointBuilders = resultBuilder.getPointsBuilderList();
      Map<Long, QueryProto.Point.Builder> timePointMap = new TreeMap<>(pointBuilders.stream()
          .collect(Collectors.toMap(QueryProto.Point.Builder::getTimestamp, Function.identity())));
      long sample = QueryStorageUtils.convertDownSample(downsample);
      for (long period =
          start % sample == 0 ? start : start / sample * sample + sample; period <= end; period +=
              sample) {
        if (!timePointMap.containsKey(period)) {
          QueryProto.Point.Builder builder = QueryProto.Point.newBuilder().setTimestamp(period);
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
      List<io.holoinsight.server.extension.model.QueryResult.Result> results =
          metricStorage.queryTags(param);
      for (io.holoinsight.server.extension.model.QueryResult.Result result : results) {
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
   * @return
   * @param <T>
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
