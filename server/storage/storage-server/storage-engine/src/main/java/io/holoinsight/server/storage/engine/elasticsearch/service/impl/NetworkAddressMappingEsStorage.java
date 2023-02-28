/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.elasticsearch.utils.EsGsonUtils;
import io.holoinsight.server.storage.engine.model.NetworkAddressMappingDO;
import io.holoinsight.server.storage.engine.storage.NetworkAddressMappingStorage;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@ConditionalOnFeature("trace")
@Service("networkAddressMappingEsStorage")
@Primary
public class NetworkAddressMappingEsStorage extends RecordEsStorage<NetworkAddressMappingDO>
    implements NetworkAddressMappingStorage {

  @Autowired
  private RestHighLevelClient esClient;

  @Override
  public List<NetworkAddressMappingDO> loadByTime(long timeBucketInMinute) throws IOException {
    List<NetworkAddressMappingDO> networkAddressMapping = new ArrayList<>();

    SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
    searchSourceBuilder.size(1000);
    searchSourceBuilder
        .query(new RangeQueryBuilder(NetworkAddressMappingDO.TIME_BUCKET).gte(timeBucketInMinute));
    SearchRequest searchRequest =
        new SearchRequest(new String[] {NetworkAddressMappingDO.INDEX_NAME}, searchSourceBuilder);

    SearchResponse searchResponse = esClient.search(searchRequest, RequestOptions.DEFAULT);

    for (SearchHit searchHit : searchResponse.getHits().getHits()) {
      String hitJson = searchHit.getSourceAsString();
      NetworkAddressMappingDO networkAddressMappingEsDO =
          EsGsonUtils.esGson().fromJson(hitJson, NetworkAddressMappingDO.class);
      networkAddressMapping.add(networkAddressMappingEsDO);
    }

    return networkAddressMapping;
  }
}
