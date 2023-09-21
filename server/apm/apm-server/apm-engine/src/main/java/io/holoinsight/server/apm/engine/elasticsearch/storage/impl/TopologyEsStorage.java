/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.common.model.query.Call;
import io.holoinsight.server.apm.common.model.query.ResponseMetric;
import io.holoinsight.server.apm.common.model.specification.otel.SpanKind;
import io.holoinsight.server.apm.engine.model.*;
import io.holoinsight.server.apm.engine.storage.ICommonBuilder;
import io.holoinsight.server.apm.engine.storage.TopologyStorage;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class TopologyEsStorage implements TopologyStorage {

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
  public List<Call> getServiceCalls(String tenant, String service, long startTime, long endTime,
      String sourceOrDest, Map<String, String> termParams) throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(ServiceRelationDO.TENANT, tenant))
            .must(QueryBuilders.termQuery(sourceOrDest + "_service_name", service))
            .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(commonBuilder.buildAgg(ServiceRelationDO.ENTITY_ID));

    SearchRequest searchRequest = new SearchRequest(ServiceRelationDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    return buildCalls(response);
  }

  @Override
  public List<Call.DeepCall> getEndpointCalls(String tenant, String service, String endpoint,
      long startTime, long endTime, String sourceOrDest, Map<String, String> termParams)
      throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(EndpointRelationDO.TENANT, tenant))
            .must(QueryBuilders.termQuery(sourceOrDest + "_service_name", service))
            .must(QueryBuilders.termQuery(sourceOrDest + "_endpoint_name", endpoint))
            .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(commonBuilder.buildAgg(EndpointRelationDO.ENTITY_ID));

    SearchRequest searchRequest = new SearchRequest(EndpointRelationDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    return buildEndpointCalls(response);
  }

  @Override
  public List<Call> getComponentCalls(String tenant, String address, long startTime, long endTime,
      String sourceOrDest, Map<String, String> termParams) throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(EndpointRelationDO.TENANT, tenant))
            .must(QueryBuilders.termQuery(sourceOrDest + "_service_name", address))
            .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(commonBuilder.buildAgg(EndpointRelationDO.ENTITY_ID));

    SearchRequest searchRequest = new SearchRequest(ServiceRelationDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    return buildCalls(response);
  }

  @Override
  public Map<String, ResponseMetric> getServiceAggMetric(String tenant, List<String> fieldValueList,
      long startTime, long endTime, String aggField, Map<String, String> termParams)
      throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(aggField, fieldValueList))
            .must(QueryBuilders.termQuery(SpanDO.resource(SpanDO.TENANT), tenant))
            .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));

    if (!SpanDO.NAME.equals(aggField)) {
      queryBuilder.must(
          QueryBuilders.boolQuery().should(QueryBuilders.termQuery(SpanDO.KIND, SpanKind.SERVER))
              .should(QueryBuilders.termQuery(SpanDO.KIND, SpanKind.CONSUMER)));
    }

    commonBuilder.addTermParamsWithAttrPrefix(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(commonBuilder.buildAgg(aggField));

    SearchRequest searchRequest = new SearchRequest(SpanDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    return buildServiceMetric(response, aggField);
  }

  @Override
  public List<Call.DeepCall> getServiceInstanceCalls(String tenant, String service,
      String serviceInstance, long startTime, long endTime, String sourceOrDest,
      Map<String, String> termParams) throws IOException {
    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(ServiceInstanceRelationDO.TENANT, tenant))
        .must(QueryBuilders.termQuery(sourceOrDest + "_service_name", service))
        .must(QueryBuilders.termQuery(sourceOrDest + "_service_instance_name", serviceInstance))
        .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(commonBuilder.buildAgg(ServiceInstanceRelationDO.ENTITY_ID));

    SearchRequest searchRequest = new SearchRequest(ServiceInstanceRelationDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    return buildServiceInstanceCalls(response);
  }

  @Override
  public List<Call> getTenantCalls(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(ServiceRelationDO.TENANT, tenant))
            .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(commonBuilder.buildAgg(ServiceRelationDO.ENTITY_ID));

    SearchRequest searchRequest = new SearchRequest(ServiceRelationDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    return buildCalls(response);
  }

  private Map<String, ResponseMetric> buildServiceMetric(SearchResponse response, String aggField) {
    Map<String, ResponseMetric> result = new HashMap<>();
    Terms terms = response.getAggregations().get(aggField);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String service = bucket.getKey().toString();
      result.put(service, commonBuilder.buildMetric(bucket));
    }

    return result;
  }

  private List<Call> buildCalls(SearchResponse response) {
    List<Call> calls = new ArrayList<>();
    Terms terms = response.getAggregations().get(ServiceRelationDO.ENTITY_ID);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String entityId = bucket.getKey().toString();
      Terms componentTerm = bucket.getAggregations().get(ServiceRelationDO.COMPONENT);
      String component = "";
      if (componentTerm != null && !componentTerm.getBuckets().isEmpty()) {
        component = componentTerm.getBuckets().get(0).getKey().toString();
      }

      Call call = new Call();
      call.buildFromServiceRelation(entityId, component);
      call.setMetric(commonBuilder.buildMetric(bucket));

      calls.add(call);
    }

    return calls;
  }

  private List<Call.DeepCall> buildServiceInstanceCalls(SearchResponse response) {
    List<Call.DeepCall> calls = new ArrayList<>();
    Terms terms = response.getAggregations().get(EndpointRelationDO.ENTITY_ID);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String entityId = bucket.getKey().toString();
      Terms componentTerm = bucket.getAggregations().get(ServiceRelationDO.COMPONENT);
      String component = "";
      if (componentTerm != null && !componentTerm.getBuckets().isEmpty()) {
        component = componentTerm.getBuckets().get(0).getKey().toString();
      }
      Call.DeepCall call = new Call.DeepCall();
      call.buildFromInstanceRelation(entityId, component);
      call.setMetric(commonBuilder.buildMetric(bucket));

      calls.add(call);
    }

    return calls;
  }

  private List<Call.DeepCall> buildEndpointCalls(SearchResponse response) {
    List<Call.DeepCall> calls = new ArrayList<>();
    Terms terms = response.getAggregations().get(EndpointRelationDO.ENTITY_ID);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String entityId = bucket.getKey().toString();

      Call.DeepCall call = new Call.DeepCall();
      call.buildFromEndpointRelation(entityId);
      call.setMetric(commonBuilder.buildMetric(bucket));

      calls.add(call);
    }

    return calls;
  }

}
