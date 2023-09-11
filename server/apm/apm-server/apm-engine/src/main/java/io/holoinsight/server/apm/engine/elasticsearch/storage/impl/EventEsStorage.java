/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.common.event.Event;
import io.holoinsight.server.apm.engine.elasticsearch.utils.ApmGsonUtils;
import io.holoinsight.server.apm.engine.model.EventDO;
import io.holoinsight.server.apm.engine.model.RecordDO;
import io.holoinsight.server.apm.engine.storage.EventStorage;
import io.holoinsight.server.apm.engine.storage.ICommonBuilder;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class EventEsStorage extends RecordEsStorage<EventDO> implements EventStorage {

  @Autowired
  private RestHighLevelClient client;

  @Autowired
  private ICommonBuilder commonBuilder;

  protected RestHighLevelClient client() {
    return client;
  }

  @Override
  public List<Event> queryEvents(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws Exception {
    BoolQueryBuilder queryBuilder =
        QueryBuilders.boolQuery().must(QueryBuilders.termQuery(EventDO.TENANT, tenant))
            .must(QueryBuilders.rangeQuery(RecordDO.TIMESTAMP).gte(startTime).lte(endTime));

    commonBuilder.addTermParams(queryBuilder, termParams);
    SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
    sourceBuilder.size(1000);
    sourceBuilder.query(queryBuilder);

    SearchRequest searchRequest = new SearchRequest(EventDO.INDEX_NAME);
    searchRequest.source(sourceBuilder);
    SearchResponse response = client().search(searchRequest, RequestOptions.DEFAULT);

    List<Event> result = new ArrayList<>();
    for (SearchHit searchHit : response.getHits().getHits()) {
      String hitJson = searchHit.getSourceAsString();
      EventDO eventDO = ApmGsonUtils.apmGson().fromJson(hitJson, EventDO.class);
      Event event = new Event();
      BeanUtils.copyProperties(eventDO, event);
      result.add(event);
    }

    return result;
  }

}
