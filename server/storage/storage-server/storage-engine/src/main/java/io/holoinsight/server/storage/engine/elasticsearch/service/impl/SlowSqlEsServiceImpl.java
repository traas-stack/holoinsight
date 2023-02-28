/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.model.query.SlowSql;
import io.holoinsight.server.storage.engine.elasticsearch.model.SlowSqlEsDO;
import io.holoinsight.server.storage.engine.SlowSqlStorage;
import io.holoinsight.server.storage.engine.elasticsearch.utils.EsGsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnFeature("trace")
@Service("slowSqlEsServiceImpl")
public class SlowSqlEsServiceImpl extends RecordEsService<SlowSqlEsDO> implements SlowSqlStorage {

  @Autowired
  private RestHighLevelClient client;


  @Override
  public List<SlowSql> getSlowSqlList(String tenant, String serviceName, String dbAddress,
      long startTime, long endTime) throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(SlowSqlEsDO.TENANT, tenant))
            .must(QueryBuilders.rangeQuery(SlowSqlEsDO.START_TIME).gte(startTime).lte(endTime));

    if (!StringUtils.isEmpty(serviceName)) {
      queryBuilder.must(QueryBuilders.termQuery(SlowSqlEsDO.SERVICE_NAME, serviceName));
    }
    if (!StringUtils.isEmpty(dbAddress)) {
      queryBuilder.must(QueryBuilders.termQuery(SlowSqlEsDO.ADDRESS, dbAddress));
    }

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);

    SearchRequest searchRequest = new SearchRequest(SlowSqlEsDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);

    List<SlowSql> result = new ArrayList<>();
    for (SearchHit searchHit : response.getHits().getHits()) {
      String hitJson = searchHit.getSourceAsString();
      SlowSql slowSql = EsGsonUtils.esGson().fromJson(hitJson, SlowSql.class);
      result.add(slowSql);
    }

    return result;
  }
}
