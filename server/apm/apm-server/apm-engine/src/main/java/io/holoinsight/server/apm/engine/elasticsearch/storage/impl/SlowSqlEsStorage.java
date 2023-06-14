/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.engine.elasticsearch.utils.ApmGsonUtils;
import io.holoinsight.server.apm.engine.model.RecordDO;
import io.holoinsight.server.apm.engine.model.SlowSqlDO;
import io.holoinsight.server.apm.engine.storage.ICommonBuilder;
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
import java.util.Map;

@Slf4j
public class SlowSqlEsStorage extends RecordEsStorage<SlowSqlDO> implements SlowSqlStorage {

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
  public List<SlowSql> getSlowSqlList(String tenant, String serviceName, String dbAddress,
      long startTime, long endTime, Map<String, String> termParams) throws IOException {

    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(SlowSqlDO.TENANT, tenant))
            .must(QueryBuilders.rangeQuery(this.timeSeriesField()).gte(startTime).lte(endTime));

    if (!StringUtils.isEmpty(serviceName)) {
      queryBuilder.must(QueryBuilders.termQuery(SlowSqlDO.SERVICE_NAME, serviceName));
    }
    if (!StringUtils.isEmpty(dbAddress)) {
      queryBuilder.must(QueryBuilders.termQuery(SlowSqlDO.ADDRESS, dbAddress));
    }

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);

    SearchRequest searchRequest = new SearchRequest(SlowSqlDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    List<SlowSql> result = new ArrayList<>();
    for (SearchHit searchHit : response.getHits().getHits()) {
      String hitJson = searchHit.getSourceAsString();
      SlowSql slowSql = ApmGsonUtils.apmGson().fromJson(hitJson, SlowSql.class);
      result.add(slowSql);
    }

    return result;
  }
}
