/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.apm.engine.model.EndpointRelationDO;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.EndpointStorage;
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
import java.util.List;

@Slf4j
public class EndpointEsStorage implements EndpointStorage {

  @Autowired
  private RestHighLevelClient client;

  protected RestHighLevelClient esClient() {
    return client;
  }

  protected String rangeTimeField() {
    return SpanDO.START_TIME;
  }

  @Override
  public List<Endpoint> getEndpointList(String tenant, String service, long startTime, long endTime)
      throws IOException {
    List<Endpoint> result = new ArrayList<>();

    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(EndpointRelationDO.TENANT, tenant))
        .must(QueryBuilders.termQuery(EndpointRelationDO.DEST_SERVICE_NAME, service))
        .must(QueryBuilders.rangeQuery(rangeTimeField()).gte(startTime).lte(endTime));

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(CommonBuilder.buildAgg(EndpointRelationDO.DEST_ENDPOINT_NAME));

    SearchRequest searchRequest = new SearchRequest(EndpointRelationDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = esClient().search(searchRequest, RequestOptions.DEFAULT);

    Terms terms = response.getAggregations().get(EndpointRelationDO.DEST_ENDPOINT_NAME);
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String endpointName = bucket.getKey().toString();

      Endpoint endpoint = new Endpoint();
      endpoint.setName(endpointName);
      endpoint.setMetric(CommonBuilder.buildMetric(bucket));

      result.add(endpoint);
    }

    return result;
  }
}
