/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.constants.Const;
import io.holoinsight.server.storage.engine.elasticsearch.model.RecordEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.utils.EsGsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @author jiwliu
 * @version : RecordEsService.java, v 0.1 2022年10月12日 11:52 wanpeng.xwp Exp $
 */
@ConditionalOnFeature("trace")
@Service
public class RecordEsService<T extends RecordEsDO> {

  @Autowired
  private RestHighLevelClient esClient;

  public void batchInsert(List<T> entities) throws IOException {
    if (CollectionUtils.isNotEmpty(entities)) {
      BulkRequest bulkRequest = new BulkRequest();
      entities.forEach(entity -> {
        String writeIndexName = writeIndexName(entity.indexName(), entity.getTimeBucket());
        bulkRequest.add(new IndexRequest(writeIndexName).source(EsGsonUtils.esGson().toJson(entity),
            XContentType.JSON));
      });
      esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }
  }

  private static String writeIndexName(String indexName, long timeBucket) {
    return indexName + Const.LINE + timeBucket / 1000000;
  }

}
