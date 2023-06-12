/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.common.model.specification.otel.SpanKind;
import io.holoinsight.server.apm.engine.model.RecordDO;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.ICommonBuilder;
import io.holoinsight.server.apm.engine.storage.ServiceOverviewStorage;
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
import java.util.Map;

@Slf4j
public class ServiceOverviewEsStorage implements ServiceOverviewStorage {

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
  public List<Service> getServiceList(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws IOException {
    List<Service> result = new ArrayList<>();

    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery()
            .must(QueryBuilders.termQuery(SpanDO.resource(SpanDO.TENANT), tenant))
            .must(QueryBuilders.boolQuery()
                .should(QueryBuilders.termQuery(SpanDO.KIND, SpanKind.SERVER))
                .should(QueryBuilders.termQuery(SpanDO.KIND, SpanKind.CONSUMER)))
            .must(QueryBuilders.rangeQuery(timeSeriesField()).gte(startTime).lte(endTime));

    commonBuilder.addTermParamsWithAttrPrefix(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(0);
    sourceBuilder.query(queryBuilder);
    sourceBuilder.aggregation(commonBuilder.buildAgg(SpanDO.resource(SpanDO.SERVICE_NAME)));

    SearchRequest searchRequest = new SearchRequest(SpanDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    Terms terms = response.getAggregations().get(SpanDO.resource(SpanDO.SERVICE_NAME));
    for (Terms.Bucket bucket : terms.getBuckets()) {
      String serviceName = bucket.getKey().toString();

      Service service = new Service();
      service.setName(serviceName);
      service.setMetric(commonBuilder.buildMetric(bucket));

      result.add(service);
    }
    return result;
  }
}
