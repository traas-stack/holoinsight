/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.storage.impl;

import io.holoinsight.server.storage.common.model.query.SlowSql;
import io.holoinsight.server.storage.engine.elasticsearch.utils.EsGsonUtils;
import io.holoinsight.server.storage.engine.model.SlowSqlDO;
import io.holoinsight.server.storage.engine.storage.SlowSqlStorage;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SlowSqlEsStorage extends RecordEsStorage<SlowSqlDO> implements SlowSqlStorage {

  @Autowired
  private RestHighLevelClient client;

  @Override
  public List<SlowSql> getSlowSqlList(String tenant, String serviceName, String dbAddress,
      long startTime, long endTime) throws IOException {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(SlowSqlDO.TENANT, tenant))
            .must(QueryBuilders.rangeQuery(SlowSqlDO.START_TIME).gte(startTime).lte(endTime));

    if (!StringUtils.isEmpty(serviceName)) {
      queryBuilder.must(QueryBuilders.termQuery(SlowSqlDO.SERVICE_NAME, serviceName));
    }
    if (!StringUtils.isEmpty(dbAddress)) {
      queryBuilder.must(QueryBuilders.termQuery(SlowSqlDO.ADDRESS, dbAddress));
    }

    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);

    SearchRequest searchRequest = new SearchRequest(SlowSqlDO.INDEX_NAME);
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
