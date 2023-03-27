/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.common.model.query.ServiceInstance;
import io.holoinsight.server.apm.common.model.specification.otel.SpanKind;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.ServiceInstanceStorage;
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
public class ServiceInstanceEsStorage implements ServiceInstanceStorage {

  @Autowired
  private RestHighLevelClient client;

  protected RestHighLevelClient esClient() {
    return client;
  }

  @Override
  public List<ServiceInstance> getServiceInstanceList(String tenant, String service, long startTime,
      long endTime) throws IOException {
    List<ServiceInstance> result = new ArrayList<>();

    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery(SpanDO.resource(SpanDO.TENANT), tenant))
            .must(QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery(SpanDO.KIND, SpanKind.SERVER))
                .should(QueryBuilders.termQuery(SpanDO.KIND, SpanKind.CONSUMER)))
            .must(QueryBuilders.termQuery(SpanDO.resource(SpanDO.SERVICE_NAME), service))
            .must(QueryBuilders.rangeQuery(SpanDO.START_TIME).gte(startTime).lte(endTime));

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder
        .aggregation(CommonBuilder.buildAgg(SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME)));

    SearchRequest searchRequest = new SearchRequest(SpanDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = esClient().search(searchRequest, RequestOptions.DEFAULT);

    Terms terms = response.getAggregations().get(SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME));
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String serviceInstanceName = bucket.getKey().toString();

      ServiceInstance serviceInstance = new ServiceInstance();
      serviceInstance.setName(serviceInstanceName);
      serviceInstance.setMetric(CommonBuilder.buildMetric(bucket));

      result.add(serviceInstance);
    }

    return result;
  }
}
