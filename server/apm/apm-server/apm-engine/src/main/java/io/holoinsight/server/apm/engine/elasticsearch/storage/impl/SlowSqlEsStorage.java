/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.engine.elasticsearch.utils.EsGsonUtils;
import io.holoinsight.server.apm.engine.model.SlowSqlDO;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.apm.engine.storage.SlowSqlStorage;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class SlowSqlEsStorage extends RecordEsStorage<SlowSqlDO> implements SlowSqlStorage {

  @Autowired
  private RestHighLevelClient client;

  protected RestHighLevelClient esClient() {
    return client;
  }

  protected String rangeTimeField() {
    return SpanDO.START_TIME;
  }

  @Override
  public List<SlowSql> getSlowSqlList(String tenant, String serviceName, String dbAddress,
      long startTime, long endTime) throws IOException {

    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(SlowSqlDO.TENANT, tenant))
            .must(QueryBuilders.rangeQuery(rangeTimeField()).gte(startTime).lte(endTime));

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
    SearchResponse response = esClient().search(searchRequest, RequestOptions.DEFAULT);

    List<SlowSql> result = new ArrayList<>();
    for (SearchHit searchHit : response.getHits().getHits()) {
      String hitJson = searchHit.getSourceAsString();
      SlowSql slowSql = EsGsonUtils.esGson().fromJson(hitJson, SlowSql.class);
      result.add(slowSql);
    }

    return result;
  }
}
