/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.storage.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.Duration;
import io.holoinsight.server.storage.common.model.query.MetricValue;
import io.holoinsight.server.storage.common.model.query.MetricValues;
import io.holoinsight.server.storage.common.model.specification.OtlpMappings;
import io.holoinsight.server.storage.engine.MetricDefine;
import io.holoinsight.server.storage.engine.MetricsManager;
import io.holoinsight.server.storage.engine.model.SpanDO;
import io.holoinsight.server.storage.engine.storage.MetricStorage;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.index.query.TermsQueryBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.histogram.*;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.elasticsearch.search.aggregations.metrics.ParsedPercentiles;
import org.elasticsearch.search.aggregations.metrics.Percentile;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author jiwliu
 * @version : MetricEsServiceImpl.java, v 0.1 2022年09月29日 16:58 xiangwanpeng Exp $
 */
@ConditionalOnFeature("trace")
@Service("spanMetricEsStorage")
@Primary
public class SpanMetricEsStorage implements MetricStorage {

  private static final int AGG_TERM_MAX_SIZE = 100;

  @Autowired
  private MetricsManager metricsManager;
  @Autowired
  private RestHighLevelClient restHighLevelClient;

  @Override
  public List<String> listMetrics() {
    return metricsManager.listMetrics();
  }

  @Override
  public MetricValues queryMetric(String tenant, String metric, Duration duration,
      Map<String, Object> conditions) throws IOException {
    Assert.notNull(duration, "duration can not be null!");
    MetricDefine metricDefine = metricsManager.getMetric(metric);
    Assert.notNull(metricDefine, String.format("metric not found: %s", metric));
    Map<String, Object> mergedConditions = new HashMap<>();
    if (conditions != null) {
      conditions.forEach((k, v) -> mergedConditions.put(OtlpMappings.sw2Otlp(k), v));
    }
    if (metricDefine.getConditions() != null) {
      mergedConditions.putAll(metricDefine.getConditions());
    }

    return queryFromEs(tenant, metricDefine.getIndex(), duration, mergedConditions,
        metricDefine.getField(), metricDefine.getFunction(), metricDefine.getGroups());
  }

  @Override
  public List<String> querySchema(String metric) {
    Assert.notNull(metric, "metric can not be null!");
    MetricDefine metricDefine = metricsManager.getMetric(metric);
    Assert.notNull(metricDefine, String.format("metric not found: %s", metric));
    return metricDefine.getGroups().stream().map(OtlpMappings::otlp2Sw)
        .collect(Collectors.toList());
  }

  private MetricValues queryFromEs(String tenant, String index, Duration duration,
      Map<String, Object> conditions, String field, String function, List<String> groups)
      throws IOException {
    List<MetricValue> values = new ArrayList<>();
    MetricValues metricValues = new MetricValues(values);
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
    boolQueryBuilder.filter(new TermQueryBuilder(SpanDO.resource(SpanDO.TENANT), tenant)).filter(
        new RangeQueryBuilder(SpanDO.START_TIME).gte(duration.getStart()).lte(duration.getEnd()));

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
        AggregationBuilders.dateHistogram(SpanDO.START_TIME).field(SpanDO.START_TIME)
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
    parentAggregationBuilder.subAggregation(statBuilder(field, function));

    searchSourceBuilder.size(0).query(boolQueryBuilder)
        .aggregation(dateHistogramAggregationBuilder);
    SearchRequest searchRequest = new SearchRequest(new String[] {index}, searchSourceBuilder);
    SearchResponse searchResponse =
        restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
    Aggregations aggregations = searchResponse.getAggregations();
    Aggregation aggregation = aggregations.get(SpanDO.START_TIME);
    ParsedDateHistogram dateHistogram = (ParsedDateHistogram) aggregation;
    List<? extends Histogram.Bucket> timeBuckets = dateHistogram.getBuckets();
    Map<Map<String, String>, Map<Long, Double>> valuesMap = new HashMap<>();
    if (timeBuckets != null) {
      for (Histogram.Bucket bucket : timeBuckets) {
        ParsedDateHistogram.ParsedBucket parsedBucket = (ParsedDateHistogram.ParsedBucket) bucket;
        ZonedDateTime zonedDateTime = (ZonedDateTime) parsedBucket.getKey();
        long time = zonedDateTime.toEpochSecond() * 1000;
        Aggregations timeAggregations = parsedBucket.getAggregations();
        Map<String, String> tags = new HashMap<>();
        Aggregation timeAggregation = timeAggregations.iterator().next();
        backtrackExtract(time, timeAggregation, valuesMap, tags);
      }
    }
    valuesMap.forEach((tags, timeVals) -> {
      MetricValue metricValue = new MetricValue(tags, timeVals);
      values.add(metricValue);
    });
    return metricValues;
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

  private void backtrackExtract(Long time, Aggregation aggregation,
      Map<Map<String, String>, Map<Long, Double>> tagsValues, Map<String, String> tags) {
    if (aggregation instanceof NumericMetricsAggregation.SingleValue) {
      NumericMetricsAggregation.SingleValue singleValue =
          (NumericMetricsAggregation.SingleValue) aggregation;
      double value = singleValue.value();
      Map<String, String> newTags = new HashMap<>(tags);
      tagsValues.computeIfAbsent(newTags, t -> new TreeMap<>()).put(time, value);
      return;
    } else if (aggregation instanceof ParsedPercentiles) {
      ParsedPercentiles parsedPercentiles = (ParsedPercentiles) aggregation;
      for (Percentile percentile : parsedPercentiles) {
        double percent = percentile.getPercent();
        double value = percentile.getValue();
        Map<String, String> newTags = new HashMap<>(tags);
        newTags.put("percent", String.valueOf(percent));
        tagsValues.computeIfAbsent(newTags, t -> new HashMap<>()).put(time, value);
      }
      return;
    }
    if (aggregation instanceof ParsedStringTerms) {
      ParsedStringTerms parsedStringTerms = (ParsedStringTerms) aggregation;
      String tagK = parsedStringTerms.getName();
      List<? extends Terms.Bucket> buckets = parsedStringTerms.getBuckets();
      if (buckets != null) {
        for (Terms.Bucket bucket : buckets) {
          String tagV = bucket.getKeyAsString();
          tags.put(OtlpMappings.otlp2Sw(tagK), tagV);
          Aggregation subAggregation = bucket.getAggregations().iterator().next();
          backtrackExtract(time, subAggregation, tagsValues, tags);
          tags.remove(tagK);
        }
      }
    } else {
      throw new UnsupportedOperationException(
          "unsupported aggregation type: " + aggregation.getClass().getName());
    }
  }
}
