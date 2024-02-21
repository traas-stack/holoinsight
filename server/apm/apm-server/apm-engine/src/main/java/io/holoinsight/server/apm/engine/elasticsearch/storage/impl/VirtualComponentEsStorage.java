/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.common.model.specification.sw.RequestType;
import io.holoinsight.server.apm.engine.model.EndpointRelationDO;
import io.holoinsight.server.apm.engine.model.RecordDO;
import io.holoinsight.server.apm.engine.model.ServiceRelationDO;
import io.holoinsight.server.apm.engine.storage.ICommonBuilder;
import io.holoinsight.server.apm.engine.storage.VirtualComponentStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregator;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class VirtualComponentEsStorage implements VirtualComponentStorage {

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
  public List<VirtualComponent> getComponentList(String tenant, String service, long startTime,
      long endTime, RequestType type, String sourceOrDest, Map<String, String> termParams)
      throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(ServiceRelationDO.TENANT, tenant))
            .must(QueryBuilders.termQuery(sourceOrDest + "_service_name", service))
            .must(QueryBuilders.termQuery(ServiceRelationDO.TYPE, type.name()))
            .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(commonBuilder.buildAgg(ServiceRelationDO.ENTITY_ID));

    SearchRequest searchRequest = new SearchRequest(ServiceRelationDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    return buildComponentList(response, sourceOrDest);
  }

  @Override
  public List<String> getTraceIds(String tenant, String service, String address, long startTime,
      long endTime, Map<String, String> termParams) throws IOException {
    TermsAggregationBuilder aggregationBuilder = AggregationBuilders
        .terms(ServiceRelationDO.TRACE_ID).field(ServiceRelationDO.TRACE_ID).executionHint("map")
        .collectMode(Aggregator.SubAggCollectionMode.BREADTH_FIRST).size(1000);

    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(ServiceRelationDO.TENANT, tenant))
            .must(QueryBuilders.termQuery(ServiceRelationDO.DEST_SERVICE_NAME, address))
            .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));
    if (!StringUtils.isEmpty(service)) {
      queryBuilder.must(QueryBuilders.termQuery(ServiceRelationDO.SOURCE_SERVICE_NAME, service));
    }

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(aggregationBuilder);

    SearchRequest searchRequest = new SearchRequest(ServiceRelationDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    List<String> traceIds = new ArrayList<>();
    Terms terms = response.getAggregations().get(ServiceRelationDO.TRACE_ID);

    for (Terms.Bucket bucket : terms.getBuckets()) {
      String traceId = bucket.getKey().toString();
      traceIds.add(traceId);
    }

    return traceIds;
  }

  private List<VirtualComponent> buildComponentList(SearchResponse response, String sourceOrDest) {
    List<VirtualComponent> result = new ArrayList<>();
    Terms terms = response.getAggregations().get(EndpointRelationDO.ENTITY_ID);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String entityId = bucket.getKey().toString();
      Terms componentTerm = bucket.getAggregations().get(ServiceRelationDO.COMPONENT);
      String component = componentTerm.getBuckets().get(0).getKey().toString();

      VirtualComponent db = new VirtualComponent();
      db.buildFromServiceRelation(entityId, component, sourceOrDest);
      db.setMetric(commonBuilder.buildMetric(bucket));

      result.add(db);
    }

    return result;
  }
}
