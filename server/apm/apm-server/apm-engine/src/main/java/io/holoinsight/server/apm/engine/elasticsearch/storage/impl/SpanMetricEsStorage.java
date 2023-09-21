/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.model.query.Duration;
import io.holoinsight.server.apm.common.model.query.MetricValue;
import io.holoinsight.server.apm.common.model.query.MetricValues;
import io.holoinsight.server.apm.common.model.query.StatisticData;
import io.holoinsight.server.apm.common.model.query.StatisticDataList;
import io.holoinsight.server.apm.common.model.specification.OtlpMappings;
import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.TraceState;
import io.holoinsight.server.apm.engine.model.RecordDO;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.postcal.MetricDefine;
import io.holoinsight.server.apm.engine.postcal.PostCalMetricStorage;
import io.opentelemetry.proto.trace.v1.Status;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.filter.Filter;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

public class SpanMetricEsStorage extends PostCalMetricStorage {

  private static final int AGG_TERM_MAX_SIZE = 100;


  @Autowired
  private RestHighLevelClient esClient;

  protected RestHighLevelClient client() {
    return esClient;
  }


  @Override
  public String timeSeriesField() {
    return RecordDO.TIMESTAMP;
  }


  @Override
  protected MetricValues query(MetricDefine metricDefine, String tenant, Duration duration,
      Map<String, Object> conditions, Collection<String> groups) throws IOException {
    List<MetricValue> values = new ArrayList<>();
    MetricValues metricValues = new MetricValues(values);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();


    boolQueryBuilder
        .filter(new TermQueryBuilder(OtlpMappings.toOtlp(metricDefine.getIndex(), Const.TENANT),
            tenant))
        .filter(new RangeQueryBuilder(timeSeriesField()).gte(duration.getStart())
            .lte(duration.getEnd()));

    if (conditions != null) {
      conditions.forEach((conditionKey, conditionVal) -> {
        if (conditionVal instanceof Iterable) {
          boolQueryBuilder.filter(new TermsQueryBuilder(conditionKey, (Iterable<?>) conditionVal));
        } else {
          boolQueryBuilder.filter(new TermsQueryBuilder(conditionKey, conditionVal));
        }
      });
    }

    DateHistogramAggregationBuilder dateHistogramAggregationBuilder =
        AggregationBuilders.dateHistogram(timeSeriesField()).field(timeSeriesField())
            .fixedInterval(new DateHistogramInterval(duration.getStep())).minDocCount(1)
            .extendedBounds(new ExtendedBounds(duration.getStart(), duration.getEnd()));
    AggregationBuilder parentAggregationBuilder = dateHistogramAggregationBuilder;
    if (groups != null) {
      for (String group : groups) {
        AggregationBuilder subAggregationBuilder =
            AggregationBuilders.terms(group).size(AGG_TERM_MAX_SIZE).field(group);
        parentAggregationBuilder.subAggregation(subAggregationBuilder);
        parentAggregationBuilder = subAggregationBuilder;
      }
    }
    parentAggregationBuilder
        .subAggregation(statBuilder(metricDefine.getField(), metricDefine.getFunction()));

    searchSourceBuilder.size(0).query(boolQueryBuilder)
        .aggregation(dateHistogramAggregationBuilder);
    SearchRequest searchRequest =
        new SearchRequest(new String[] {metricDefine.getIndex()}, searchSourceBuilder);
    SearchResponse searchResponse = client().search(searchRequest, RequestOptions.DEFAULT);
    Aggregations aggregations = searchResponse.getAggregations();
    Aggregation aggregation = aggregations.get(timeSeriesField());
    ParsedDateHistogram dateHistogram = (ParsedDateHistogram) aggregation;
    List<? extends Histogram.Bucket> timeBuckets = dateHistogram.getBuckets();
    Map<Map<String, String>, Map<Long, Double>> resultMap = new HashMap<>();

    if (timeBuckets != null) {
      for (Histogram.Bucket bucket : timeBuckets) {
        ParsedDateHistogram.ParsedBucket parsedBucket = (ParsedDateHistogram.ParsedBucket) bucket;
        ZonedDateTime zonedDateTime = (ZonedDateTime) parsedBucket.getKey();
        long time = zonedDateTime.toEpochSecond() * 1000;
        Map<Map<String, String>, Map<String, Double>> valuesMap = new HashMap<>();
        Aggregations timeAggregations = parsedBucket.getAggregations();
        Aggregation timeAggregation = timeAggregations.iterator().next();
        backtrackExtract(metricDefine.getIndex(), timeAggregation, valuesMap, new HashMap<>(),
            false);
        valuesMap.forEach((tags, value) -> {
          Double v = value.values().iterator().next();
          resultMap.computeIfAbsent(tags, k -> new HashMap<>()).put(time,
              (v == null || v == Double.POSITIVE_INFINITY || v == Double.NEGATIVE_INFINITY) ? 0
                  : v);
        });
      }
    }
    resultMap.forEach((tags, timeVals) -> {
      MetricValue metricValue = new MetricValue(tags, timeVals);
      values.add(metricValue);
    });
    return metricValues;
  }

  @Override
  public StatisticData billing(final String tenant, String serviceName, String serviceInstanceName,
      String endpointName, List<String> traceIds, int minTraceDuration, int maxTraceDuration,
      TraceState traceState, long start, long end, List<Tag> tags) throws Exception {

    BoolQueryBuilder queryBuilder =
        SpanEsStorage.buildQuery(tenant, serviceName, serviceInstanceName, endpointName, traceIds,
            minTraceDuration, maxTraceDuration, traceState, start, end, tags, timeSeriesField());

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder
        .aggregation(AggregationBuilders.cardinality("service_count")
            .field(SpanDO.resource(SpanDO.SERVICE_NAME)))
        .aggregation(AggregationBuilders.cardinality("service_instance_count")
            .field(SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME)))
        .aggregation(AggregationBuilders.cardinality("endpoint_count").field(SpanDO.NAME))
        .aggregation(AggregationBuilders.cardinality("trace_count").field(SpanDO.TRACE_ID))
        .aggregation(AggregationBuilders.filter(SpanDO.TRACE_STATUS,
            QueryBuilders.termQuery(SpanDO.TRACE_STATUS, Status.StatusCode.STATUS_CODE_ERROR_VALUE))
            .subAggregation(AggregationBuilders.cardinality("error_count").field(SpanDO.TRACE_ID)))
        .aggregation(AggregationBuilders.avg("avg_latency").field(SpanDO.LATENCY));
    sourceBuilder.trackTotalHits(true);

    SearchRequest searchRequest = new SearchRequest(SpanDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    Map<Map<String, String>, Map<String, Double>> valuesMap = new HashMap<>();
    backtrackExtract(SpanDO.INDEX_NAME, response.getAggregations().iterator().next(), valuesMap,
        new HashMap<>(), true);

    Map.Entry<Map<String, String>, Map<String, Double>> tagsValue =
        valuesMap.entrySet().iterator().next();
    Map<String, Double> values = new TreeMap<>(tagsValue.getValue());
    return new StatisticData(tagsValue.getKey(), values);

  }

  public StatisticDataList statistic(long startTime, long endTime, List<String> groups,
      List<AggregationBuilder> aggregations) throws IOException {

    Assert.notEmpty(groups, "statistic groups must be specified");

    List<StatisticData> result = new ArrayList<>();

    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.rangeQuery(timeSeriesField()).gte(startTime).lte(endTime));

    TermsAggregationBuilder aggregationBuilder =
        AggregationBuilders.terms(groups.get(0)).field(groups.get(0)).size(10000);
    TermsAggregationBuilder parentAggregationBuilder = aggregationBuilder;
    for (int i = 1; i < groups.size(); i++) {
      String group = groups.get(i);
      TermsAggregationBuilder subAggregationBuilder =
          AggregationBuilders.terms(group).field(group).size(10000);
      parentAggregationBuilder.subAggregation(subAggregationBuilder);
      parentAggregationBuilder = subAggregationBuilder;
    }
    parentAggregationBuilder
        .subAggregation(AggregationBuilders.cardinality("service_count")
            .field(SpanDO.resource(SpanDO.SERVICE_NAME)))
        .subAggregation(AggregationBuilders.cardinality("service_instance_count")
            .field(SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME)))
        .subAggregation(AggregationBuilders.cardinality("endpoint_count").field(SpanDO.NAME))
        .subAggregation(AggregationBuilders.cardinality("trace_count").field(SpanDO.TRACE_ID))
        .subAggregation(AggregationBuilders.filter(SpanDO.TRACE_STATUS,
            QueryBuilders.termQuery(SpanDO.TRACE_STATUS, Status.StatusCode.STATUS_CODE_ERROR_VALUE))
            .subAggregation(AggregationBuilders.cardinality("error_count").field(SpanDO.TRACE_ID)))
        .subAggregation(AggregationBuilders.avg("avg_latency").field(SpanDO.LATENCY));

    if (aggregations != null) {
      for (AggregationBuilder subAggregation : aggregations) {
        parentAggregationBuilder.subAggregation(subAggregation);
      }
    }
    aggregationBuilder.executionHint("map")
        .collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST).size(1000);

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(aggregationBuilder);
    sourceBuilder.trackTotalHits(true);

    SearchRequest searchRequest = new SearchRequest(SpanDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    Map<Map<String, String>, Map<String, Double>> valuesMap = new HashMap<>();
    backtrackExtract(SpanDO.INDEX_NAME, response.getAggregations().iterator().next(), valuesMap,
        new HashMap<>(), true);

    valuesMap.forEach((tags, vals) -> {

      Map<String, Double> values = new TreeMap<>(vals);

      StatisticData statisticData = new StatisticData();
      statisticData.setResources(tags);
      statisticData.setDatas(values);

      result.add(statisticData);
    });

    return new StatisticDataList(result);
  }

  private AggregationBuilder statBuilder(String field, String function) {
    switch (function) {
      case "avg":
        return AggregationBuilders.avg(field).field(field);
      case "min":
        return AggregationBuilders.min(field).field(field);
      case "max":
        return AggregationBuilders.max(field).field(field);
      case "sum":
        return AggregationBuilders.sum(field).field(field);
      case "count":
        return AggregationBuilders.count(field).field(field);
      case "percentiles":
        return AggregationBuilders.percentiles(field).field(field).keyed(false);
      case "cardinality":
        return AggregationBuilders.cardinality(field).field(field);
      default:
        throw new UnsupportedOperationException("unsupported function: " + function);
    }
  }

  private void backtrackExtract(String index, Aggregation aggregation,
      Map<Map<String, String>, Map<String, Double>> tagsValues, Map<String, String> tags,
      boolean includeDocCount) {
    if (aggregation instanceof NumericMetricsAggregation.SingleValue) {
      NumericMetricsAggregation.SingleValue singleValue =
          (NumericMetricsAggregation.SingleValue) aggregation;
      double value = singleValue.value();
      Map<String, String> newTags = new HashMap<>(tags);
      tagsValues.computeIfAbsent(newTags, k -> new HashMap<>()).put(singleValue.getName(), value);
      return;
    } else if (aggregation instanceof ParsedPercentiles) {
      ParsedPercentiles parsedPercentiles = (ParsedPercentiles) aggregation;
      for (Percentile percentile : parsedPercentiles) {
        double percent = percentile.getPercent();
        double value = percentile.getValue();
        Map<String, String> newTags = new HashMap<>(tags);
        newTags.put("percent", String.valueOf(percent));
        tagsValues.computeIfAbsent(newTags, k -> new HashMap<>()).put(parsedPercentiles.getName(),
            value);
      }
      return;
    } else if (aggregation instanceof Filter) {
      Filter filter = (Filter) aggregation;
      for (Aggregation subAggregation : filter.getAggregations()) {
        backtrackExtract(index, subAggregation, tagsValues, tags, includeDocCount);
      }
      return;
    }
    if (aggregation instanceof ParsedTerms) {
      ParsedTerms parsedTerms = (ParsedTerms) aggregation;
      String tagK = parsedTerms.getName();
      List<? extends Terms.Bucket> buckets = parsedTerms.getBuckets();
      if (buckets != null) {
        for (Terms.Bucket bucket : buckets) {
          String tagV = bucket.getKeyAsString();
          tags.put(OtlpMappings.fromOtlp(index, tagK), tagV);
          extend(tags);
          boolean docCountAdded = false;
          for (Aggregation subAggregation : bucket.getAggregations()) {
            if (includeDocCount && !(subAggregation instanceof ParsedTerms) && !docCountAdded) {
              tagsValues.computeIfAbsent(new HashMap<>(tags), k -> new HashMap<>())
                  .put("span_count", (double) bucket.getDocCount());
              docCountAdded = true;
            }
            backtrackExtract(index, subAggregation, tagsValues, tags, includeDocCount);
          }
          tags.remove(tagK);
        }
      }
    } else {
      throw new UnsupportedOperationException(
          "unsupported aggregation type: " + aggregation.getClass().getName());
    }
  }

  public void extend(Map<String, String> tags) {}

}
