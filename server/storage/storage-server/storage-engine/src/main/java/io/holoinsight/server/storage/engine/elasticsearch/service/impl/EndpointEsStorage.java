/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.Endpoint;
import io.holoinsight.server.storage.engine.elasticsearch.model.EndpointRelationEsDO;
import io.holoinsight.server.storage.engine.EndpointStorage;
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
import java.util.List;

@ConditionalOnFeature("trace")
@Service("endpointEsStorage")
public class EndpointEsStorage implements EndpointStorage {

  @Autowired
  private RestHighLevelClient client;

  @Override
  public List<Endpoint> getEndpointList(String tenant, String service, long startTime, long endTime)
      throws IOException {
    List<Endpoint> result = new ArrayList<>();

    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(EndpointRelationEsDO.TENANT, tenant))
        .must(QueryBuilders.termQuery(EndpointRelationEsDO.DEST_SERVICE_NAME, service)).must(
            QueryBuilders.rangeQuery(EndpointRelationEsDO.START_TIME).gte(startTime).lte(endTime));

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(CommonBuilder.buildAgg(EndpointRelationEsDO.DEST_ENDPOINT_NAME));

    SearchRequest searchRequest = new SearchRequest(EndpointRelationEsDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

    Terms terms = response.getAggregations().get(EndpointRelationEsDO.DEST_ENDPOINT_NAME);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String endpointName = bucket.getKey().toString();

      Endpoint endpoint = new Endpoint();
      endpoint.setName(endpointName);
      endpoint.setMetric(CommonBuilder.buildMetric(bucket));

      result.add(endpoint);
    }
    return result;
  }

  // @Override
  // public List<BizopsEndpoint> getEntryEndpointList(String tenant, long startTime, long endTime)
  // throws IOException {
  // List<BizopsEndpoint> result = new ArrayList<>();
  //
  // TermsAggregationBuilder aggregationBuilder = AggregationBuilders
  // .terms(SegmentEsDO.SERVICE_NAME).field(SegmentEsDO.SERVICE_NAME)
  // .subAggregation(AggregationBuilders.terms(SegmentEsDO.ENDPOINT_NAME).field(SegmentEsDO.ENDPOINT_NAME)
  // .subAggregation(AggregationBuilders.terms(SegmentEsDO.STAMP).field(SegmentEsDO.STAMP)
  // .subAggregation(AggregationBuilders.terms(SegmentEsDO.ENTRYLAYER).field(SegmentEsDO.ENTRYLAYER)
  // .subAggregation(AggregationBuilders.avg("avg_latency").field(SegmentEsDO.LATENCY))
  // .subAggregation(AggregationBuilders.percentiles("p95_latency").field(SegmentEsDO.LATENCY).percentiles(95.0))
  // .subAggregation(AggregationBuilders.percentiles("p99_latency").field(SegmentEsDO.LATENCY).percentiles(99.0))
  // .subAggregation(AggregationBuilders.cardinality("total_count").field(SegmentEsDO.TRACE_ID))
  // .subAggregation(AggregationBuilders.filter(ServiceRelationEsDO.TRACE_STATUS,
  // QueryBuilders.termQuery(ServiceRelationEsDO.TRACE_STATUS, "2"))
  // .subAggregation(AggregationBuilders.cardinality("error_count").field(SegmentEsDO.TRACE_ID))))))
  // .executionHint("map")
  // .collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST)
  // .size(1000);
  //
  // BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
  // .must(QueryBuilders.termQuery(SegmentEsDO.TENANT, tenant))
  // .must(QueryBuilders.termQuery(SegmentEsDO.TAGS, Const.ISENTRYTAG))
  // .must(QueryBuilders.rangeQuery(SegmentEsDO.START_TIME).gte(startTime).lte(endTime));
  //
  // SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
  // sourceBuilder.size(1000);
  // sourceBuilder.query(queryBuilder);
  // sourceBuilder.aggregation(aggregationBuilder);
  //
  // SearchRequest searchRequest = new SearchRequest(SegmentEsDO.INDEX_NAME);
  // searchRequest.source(sourceBuilder);
  // SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
  //
  // Terms terms = response.getAggregations().get(SegmentEsDO.SERVICE_NAME);
  // for (Terms.Bucket bucket : terms.getBuckets()) {
  // String service = bucket.getKey().toString();
  //
  // Terms endpointTerm = bucket.getAggregations().get(SegmentEsDO.ENDPOINT_NAME);
  // for (Terms.Bucket endpointTermBucket : endpointTerm.getBuckets()) {
  // String endpointName = endpointTermBucket.getKey().toString();
  //
  // Terms stampTerm = endpointTermBucket.getAggregations().get(SegmentEsDO.STAMP);
  // for (Terms.Bucket stampTermBucket : stampTerm.getBuckets()) {
  // String stamp = stampTermBucket.getKey().toString();
  //
  // Terms layerTerm = stampTermBucket.getAggregations().get(SegmentEsDO.ENTRYLAYER);
  // for (Terms.Bucket layerTermBucket : layerTerm.getBuckets()) {
  // String layer = layerTermBucket.getKey().toString();
  //
  // BizopsEndpoint endpoint = new BizopsEndpoint();
  // endpoint.setService(service);
  // endpoint.setEndpoint(endpointName);
  // endpoint.setStamp(stamp);
  // endpoint.setSpanLayer(layer);
  // endpoint.setMetric(CommonBuilder.buildMetricWithDistinct(layerTermBucket));
  //
  // result.add(endpoint);
  // }
  // }
  // }
  // }
  // return result;
  // }
  //
  // @Override
  // public List<BizopsEndpoint> getErrorCodeList(String tenant, String service, String endpoint,
  // boolean isEntry, int traceIdSize, long startTime, long endTime) throws IOException {
  // List<BizopsEndpoint> result = new ArrayList<>();
  //
  // TermsAggregationBuilder aggregationBuilder = AggregationBuilders
  // .terms(SegmentEsDO.ENTRYERRORCODE).field(SegmentEsDO.ENTRYERRORCODE)
  // .subAggregation(AggregationBuilders.terms(SegmentEsDO.ENTRYROOTERRORCODE).field(SegmentEsDO.ENTRYROOTERRORCODE)
  // .subAggregation(AggregationBuilders.terms(SegmentEsDO.TRACE_ID).field(SegmentEsDO.TRACE_ID))
  // .subAggregation(AggregationBuilders.avg("avg_latency").field(SegmentEsDO.LATENCY))
  // .subAggregation(AggregationBuilders.percentiles("p95_latency").field(SegmentEsDO.LATENCY).percentiles(95.0))
  // .subAggregation(AggregationBuilders.percentiles("p99_latency").field(SegmentEsDO.LATENCY).percentiles(99.0))
  // .subAggregation(AggregationBuilders.count("total_count").field(SegmentEsDO.TRACE_ID))
  // .subAggregation(AggregationBuilders.filter(ServiceRelationEsDO.TRACE_STATUS,
  // QueryBuilders.termQuery(ServiceRelationEsDO.TRACE_STATUS, "2"))
  // .subAggregation(AggregationBuilders.count("error_count").field(SegmentEsDO.TRACE_ID))))
  // .executionHint("map")
  // .collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST)
  // .size(1000);
  //
  // BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
  // .must(QueryBuilders.termQuery(SegmentEsDO.TENANT, tenant))
  // .must(QueryBuilders.termQuery(SegmentEsDO.SERVICE_NAME, service))
  // .must(QueryBuilders.termQuery(SegmentEsDO.ENDPOINT_NAME, endpoint))
  // .must(QueryBuilders.rangeQuery(SegmentEsDO.START_TIME).gte(startTime).lte(endTime));
  //
  // if (isEntry) {
  // queryBuilder.must(QueryBuilders.termQuery(SegmentEsDO.TAGS, Const.ISENTRYTAG));
  // }
  //
  // SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
  // sourceBuilder.size(1000);
  // sourceBuilder.query(queryBuilder);
  // sourceBuilder.aggregation(aggregationBuilder);
  //
  // SearchRequest searchRequest = new SearchRequest(SegmentEsDO.INDEX_NAME);
  // searchRequest.source(sourceBuilder);
  // SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
  //
  // Terms terms = response.getAggregations().get(SegmentEsDO.ENTRYERRORCODE);
  // for (Terms.Bucket bucket : terms.getBuckets()) {
  // String errorCode = bucket.getKey().toString();
  //
  // Terms rootErrorCodeTerms = bucket.getAggregations().get(SegmentEsDO.ENTRYROOTERRORCODE);
  // for (Terms.Bucket rootErrorCodeTerm : rootErrorCodeTerms.getBuckets()) {
  // String rootErrorCode = rootErrorCodeTerm.getKey().toString();
  //
  // Terms traceIdTerms = rootErrorCodeTerm.getAggregations().get(SegmentEsDO.TRACE_ID);
  // List<String> traceIds = new ArrayList<>(traceIdSize);
  //
  // for (int i = 0; i < traceIdSize && i < traceIdTerms.getBuckets().size(); i++) {
  // traceIds.add(traceIdTerms.getBuckets().get(i).getKey().toString());
  // }
  //
  // BizopsEndpoint errEndpoint = new BizopsEndpoint();
  // errEndpoint.setService(service);
  // errEndpoint.setEndpoint(endpoint);
  // errEndpoint.setErrorCode(errorCode);
  // errEndpoint.setRootErrorCode(rootErrorCode);
  // errEndpoint.setTraceIds(traceIds);
  // errEndpoint.setMetric(CommonBuilder.buildMetric(rootErrorCodeTerm));
  //
  // result.add(errEndpoint);
  // }
  // }
  // return result;
  // }

}
