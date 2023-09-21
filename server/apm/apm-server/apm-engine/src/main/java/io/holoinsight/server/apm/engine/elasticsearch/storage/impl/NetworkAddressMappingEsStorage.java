/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.engine.elasticsearch.utils.ApmGsonUtils;
import io.holoinsight.server.apm.engine.model.NetworkAddressMappingDO;
import io.holoinsight.server.apm.engine.model.RecordDO;
import io.holoinsight.server.apm.engine.storage.NetworkAddressMappingStorage;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NetworkAddressMappingEsStorage extends RecordEsStorage<NetworkAddressMappingDO>
    implements NetworkAddressMappingStorage {

  @Autowired
  private RestHighLevelClient client;

  protected RestHighLevelClient client() {
    return client;
  }

  @Override
  public String timeSeriesField() {
    return RecordDO.TIMESTAMP;
  }

  @Override
  public List<NetworkAddressMappingDO> loadByTime(long startTime) throws IOException {
    List<NetworkAddressMappingDO> networkAddressMapping = new ArrayList<>();
    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(1000);
    searchSourceBuilder.query(new RangeQueryBuilder(this.timeSeriesField()).gte(startTime)
        .lte(System.currentTimeMillis()));
    SearchRequest searchRequest =
        new SearchRequest(new String[] {NetworkAddressMappingDO.INDEX_NAME}, searchSourceBuilder);

    SearchResponse searchResponse = client().search(searchRequest, RequestOptions.DEFAULT);

    for (SearchHit searchHit : searchResponse.getHits().getHits()) {
      String hitJson = searchHit.getSourceAsString();
      NetworkAddressMappingDO networkAddressMappingEsDO =
          ApmGsonUtils.apmGson().fromJson(hitJson, NetworkAddressMappingDO.class);
      networkAddressMapping.add(networkAddressMappingEsDO);
    }

    return networkAddressMapping;
  }
}
