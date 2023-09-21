/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.engine.elasticsearch.utils.ApmGsonUtils;
import io.holoinsight.server.apm.engine.model.RecordDO;
import io.holoinsight.server.apm.engine.model.ServiceErrorDO;
import io.holoinsight.server.apm.engine.storage.ICommonBuilder;
import io.holoinsight.server.apm.engine.storage.ServiceErrorStorage;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ValueCount;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceErrorEsStorage extends RecordEsStorage<ServiceErrorDO>
    implements ServiceErrorStorage {
  @Autowired
  private RestHighLevelClient client;

  @Autowired
  private ICommonBuilder commonBuilder;

  protected RestHighLevelClient client() {
    return client;
  }

  @Override
  public String timeSeriesField() {
    return RecordDO.TIMESTAMP;
  }

  @Override
  public List<Map<String, String>> getServiceErrorList(String tenant, String serviceName,
      long startTime, long endTime, Map<String, String> termParams) throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(ServiceErrorDO.TENANT, tenant))
            .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));

    if (!StringUtils.isEmpty(serviceName)) {
      queryBuilder.must(QueryBuilders.termQuery(ServiceErrorDO.SERVICE_NAME, serviceName));
    }

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);

    TermsAggregationBuilder aggregationBuilder =
        AggregationBuilders.terms(ServiceErrorDO.ERROR_KIND).field(ServiceErrorDO.ERROR_KIND);

    if (termParams != null && termParams.containsKey(ServiceErrorDO.ERROR_KIND)) {
      aggregationBuilder = aggregationBuilder.subAggregation(AggregationBuilders
          .terms(ServiceErrorDO.ENDPOINT_NAME).field(ServiceErrorDO.ENDPOINT_NAME));
    }

    aggregationBuilder
        .subAggregation(AggregationBuilders.count("total_count").field(ServiceErrorDO.ERROR_KIND))
        .executionHint("map").collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST).size(1000);
    sourceBuilder.aggregation(aggregationBuilder);

    SearchRequest searchRequest = new SearchRequest(ServiceErrorDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    List<Map<String, String>> result = new ArrayList<>();
    Terms terms = response.getAggregations().get(ServiceErrorDO.ERROR_KIND);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      if (termParams != null && termParams.containsKey(ServiceErrorDO.ERROR_KIND)) {
        Terms endpointTerms = bucket.getAggregations().get(ServiceErrorDO.ENDPOINT_NAME);
        for (Terms.Bucket endpointBucket : endpointTerms.getBuckets()) {
          Map<String, String> map = new HashMap<>();
          map.put("errorKind", bucket.getKey().toString());
          map.put("endpointName", endpointBucket.getKey().toString());
          map.put("count", String.valueOf(endpointBucket.getDocCount()));
          result.add(map);
        }
      } else {
        Map<String, String> map = new HashMap<>();
        map.put("errorKind", bucket.getKey().toString());
        ValueCount totalTerm = bucket.getAggregations().get("total_count");
        map.put("count", String.valueOf(totalTerm.getValue()));
        result.add(map);
      }
    }

    return result;
  }

  @Override
  public List<Map<String, String>> getServiceErrorDetail(String tenant, String serviceName,
      long startTime, long endTime, Map<String, String> termParams) throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(ServiceErrorDO.TENANT, tenant))
            .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));

    if (!StringUtils.isEmpty(serviceName)) {
      queryBuilder.must(QueryBuilders.termQuery(ServiceErrorDO.SERVICE_NAME, serviceName));
    }

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);

    SearchRequest searchRequest = new SearchRequest(ServiceErrorDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    List<Map<String, String>> result = new ArrayList<>();
    for (SearchHit searchHit : response.getHits().getHits()) {
      String hitJson = searchHit.getSourceAsString();
      ServiceErrorDO serviceErrorDO =
          ApmGsonUtils.apmGson().fromJson(hitJson, ServiceErrorDO.class);
      Map<String, String> map = new HashMap<>();
      map.put("errorKind", serviceErrorDO.getErrorKind());
      map.put("serviceName", serviceErrorDO.getServiceName());
      map.put("serviceInstanceName", serviceErrorDO.getServiceInstanceName());
      map.put("endpointName", serviceErrorDO.getEndpointName());
      map.put("traceId", serviceErrorDO.getTraceId());
      map.put("spanId", serviceErrorDO.getSpanId());
      map.put("startTime", String.valueOf(serviceErrorDO.getStartTime()));
      map.put("latency", String.valueOf(serviceErrorDO.getLatency()));
      result.add(map);
    }

    return result;
  }
}
