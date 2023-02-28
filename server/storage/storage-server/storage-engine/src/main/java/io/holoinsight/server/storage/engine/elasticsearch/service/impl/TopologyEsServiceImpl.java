/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.Call;
import io.holoinsight.server.storage.common.model.query.ResponseMetric;
import io.holoinsight.server.storage.common.model.specification.otel.SpanKind;
import io.holoinsight.server.storage.engine.TopologyStorage;
import io.holoinsight.server.storage.engine.elasticsearch.model.EndpointRelationEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.model.ServiceInstanceRelationEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.model.ServiceRelationEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.model.SpanEsDO;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ConditionalOnFeature("trace")
@Service
public class TopologyEsServiceImpl implements TopologyStorage {

  @Autowired
  private RestHighLevelClient client;

  @Override
  public List<Call.DeepCall> getEndpointCalls(String tenant, String service, String endpoint,
      long startTime, long endTime, String sourceOrDest, Map<String, String> termParams)
      throws IOException {
    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(EndpointRelationEsDO.TENANT, tenant))
        .must(QueryBuilders.termQuery(sourceOrDest + "_service_name", service))
        .must(QueryBuilders.termQuery(sourceOrDest + "_endpoint_name", endpoint)).must(
            QueryBuilders.rangeQuery(EndpointRelationEsDO.START_TIME).gte(startTime).lte(endTime));

    CommonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(CommonBuilder.buildAgg(EndpointRelationEsDO.ENTITY_ID));

    SearchRequest searchRequest = new SearchRequest(EndpointRelationEsDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

    return buildEndpointCalls(response);
  }

  @Override
  public List<Call> getComponentCalls(String tenant, String address, long startTime, long endTime,
      String sourceOrDest, Map<String, String> termParams) throws IOException {
    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(EndpointRelationEsDO.TENANT, tenant))
        .must(QueryBuilders.termQuery(sourceOrDest + "_service_name", address))
        .must(QueryBuilders.rangeQuery(ServiceRelationEsDO.START_TIME).gte(startTime).lte(endTime));

    CommonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(CommonBuilder.buildAgg(EndpointRelationEsDO.ENTITY_ID));

    SearchRequest searchRequest = new SearchRequest(ServiceRelationEsDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

    return buildCalls(response);
  }

  @Override
  public Map<String, ResponseMetric> getServiceAggMetric(String tenant, List<String> fieldValueList,
      long startTime, long endTime, String aggField, Map<String, String> termParams)
      throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termsQuery(aggField, fieldValueList))
            .must(QueryBuilders.termQuery(SpanEsDO.resource(SpanEsDO.TENANT), tenant))
            .must(QueryBuilders.rangeQuery(SpanEsDO.START_TIME).gte(startTime).lte(endTime));

    if (!SpanEsDO.NAME.equals(aggField)) {
      queryBuilder.must(
          QueryBuilders.boolQuery().should(QueryBuilders.termQuery(SpanEsDO.KIND, SpanKind.SERVER))
              .should(QueryBuilders.termQuery(SpanEsDO.KIND, SpanKind.CONSUMER)));
    }

    CommonBuilder.addTermParamsWithAttr(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(CommonBuilder.buildAgg(aggField));

    SearchRequest searchRequest = new SearchRequest(SpanEsDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

    return buildServiceMetric(response, aggField);
  }

  @Override
  public List<Call.DeepCall> getServiceInstanceCalls(String tenant, String service,
      String serviceInstance, long startTime, long endTime, String sourceOrDest,
      Map<String, String> termParams) throws IOException {
    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(ServiceInstanceRelationEsDO.TENANT, tenant))
        .must(QueryBuilders.termQuery(sourceOrDest + "_service_name", service))
        .must(QueryBuilders.termQuery(sourceOrDest + "_service_instance_name", serviceInstance))
        .must(QueryBuilders.rangeQuery(ServiceInstanceRelationEsDO.START_TIME).gte(startTime)
            .lte(endTime));

    CommonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(CommonBuilder.buildAgg(ServiceInstanceRelationEsDO.ENTITY_ID));

    SearchRequest searchRequest = new SearchRequest(ServiceInstanceRelationEsDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

    return buildServiceInstanceCalls(response);
  }

  @Override
  public List<Call> getTenantCalls(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws IOException {
    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(ServiceRelationEsDO.TENANT, tenant))
        .must(QueryBuilders.rangeQuery(ServiceRelationEsDO.START_TIME).gte(startTime).lte(endTime));

    CommonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(CommonBuilder.buildAgg(ServiceRelationEsDO.ENTITY_ID));

    SearchRequest searchRequest = new SearchRequest(ServiceRelationEsDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

    return buildCalls(response);
  }

  private Map<String, ResponseMetric> buildServiceMetric(SearchResponse response, String aggField) {
    Map<String, ResponseMetric> result = new HashMap<>();
    Terms terms = response.getAggregations().get(aggField);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String service = bucket.getKey().toString();
      result.put(service, CommonBuilder.buildMetric(bucket));
    }

    return result;
  }

  private List<Call> buildCalls(SearchResponse response) {
    List<Call> calls = new ArrayList<>();
    Terms terms = response.getAggregations().get(ServiceRelationEsDO.ENTITY_ID);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String entityId = bucket.getKey().toString();
      Terms componentTerm = bucket.getAggregations().get(ServiceRelationEsDO.COMPONENT);
      String component = componentTerm.getBuckets().get(0).getKey().toString();

      Call call = new Call();
      call.buildFromServiceRelation(entityId, component);
      call.setMetric(CommonBuilder.buildMetric(bucket));

      calls.add(call);
    }

    return calls;
  }

  private List<Call.DeepCall> buildServiceInstanceCalls(SearchResponse response) {
    List<Call.DeepCall> calls = new ArrayList<>();
    Terms terms = response.getAggregations().get(EndpointRelationEsDO.ENTITY_ID);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String entityId = bucket.getKey().toString();
      Terms componentTerm = bucket.getAggregations().get(ServiceRelationEsDO.COMPONENT);
      String component = componentTerm.getBuckets().get(0).getKey().toString();

      Call.DeepCall call = new Call.DeepCall();
      call.buildFromInstanceRelation(entityId, component);
      call.setMetric(CommonBuilder.buildMetric(bucket));

      calls.add(call);
    }

    return calls;
  }

  private List<Call.DeepCall> buildEndpointCalls(SearchResponse response) {
    List<Call.DeepCall> calls = new ArrayList<>();
    Terms terms = response.getAggregations().get(EndpointRelationEsDO.ENTITY_ID);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String entityId = bucket.getKey().toString();

      Call.DeepCall call = new Call.DeepCall();
      call.buildFromEndpointRelation(entityId);
      call.setMetric(CommonBuilder.buildMetric(bucket));

      calls.add(call);
    }

    return calls;
  }

}
