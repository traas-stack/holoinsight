/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.specification.otel.SpanKind;
import io.holoinsight.server.storage.engine.elasticsearch.service.ServiceOverviewEsService;
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
import java.util.List;

@ConditionalOnFeature("trace")
@Service
public class ServiceOverviewEsServiceImpl implements ServiceOverviewEsService {

  @Autowired
  private RestHighLevelClient client;

  @Override
  public List<io.holoinsight.server.storage.common.model.query.Service> getServiceList(
      String tenant, long startTime, long endTime) throws IOException {
    List<io.holoinsight.server.storage.common.model.query.Service> result = new ArrayList<>();

    BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery()
        .must(QueryBuilders.termQuery(SpanEsDO.resource(SpanEsDO.TENANT), tenant))
        .must(QueryBuilders.boolQuery()
            .should(QueryBuilders.termQuery(SpanEsDO.KIND, SpanKind.SERVER))
            .should(QueryBuilders.termQuery(SpanEsDO.KIND, SpanKind.CONSUMER)))
        .must(QueryBuilders.rangeQuery(SpanEsDO.START_TIME).gte(startTime).lte(endTime));

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(CommonBuilder.buildAgg(SpanEsDO.resource(SpanEsDO.SERVICE_NAME)));

    SearchRequest searchRequest = new SearchRequest(SpanEsDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

    Terms terms = response.getAggregations().get(SpanEsDO.resource(SpanEsDO.SERVICE_NAME));
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
