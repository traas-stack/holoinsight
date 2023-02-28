/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.specification.otel.SpanKind;
import io.holoinsight.server.storage.engine.storage.ServiceOverviewStorage;
import io.holoinsight.server.storage.engine.model.SpanDO;

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
@Service("serviceOverviewEsStorage")
public class ServiceOverviewEsStorage implements ServiceOverviewStorage {

  @Autowired
  private RestHighLevelClient client;

  @Override
  public List<io.holoinsight.server.storage.common.model.query.Service> getServiceList(
      String tenant, long startTime, long endTime) throws IOException {
    List<io.holoinsight.server.storage.common.model.query.Service> result = new ArrayList<>();

    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery(SpanDO.resource(SpanDO.TENANT), tenant))
            .must(QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery(SpanDO.KIND, SpanKind.SERVER))
                .should(QueryBuilders.termQuery(SpanDO.KIND, SpanKind.CONSUMER)))
            .must(QueryBuilders.rangeQuery(SpanDO.START_TIME).gte(startTime).lte(endTime));

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(CommonBuilder.buildAgg(SpanDO.resource(SpanDO.SERVICE_NAME)));

    SearchRequest searchRequest = new SearchRequest(SpanDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

    Terms terms = response.getAggregations().get(SpanDO.resource(SpanDO.SERVICE_NAME));
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String serviceName = bucket.getKey().toString();

      io.holoinsight.server.storage.common.model.query.Service service =
          new io.holoinsight.server.storage.common.model.query.Service();
      service.setName(serviceName);
      service.setMetric(CommonBuilder.buildMetric(bucket));

      result.add(service);
    }

    return result;
  }

}
