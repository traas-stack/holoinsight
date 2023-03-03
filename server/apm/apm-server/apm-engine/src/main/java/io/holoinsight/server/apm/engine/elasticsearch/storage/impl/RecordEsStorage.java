/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.engine.elasticsearch.utils.EsGsonUtils;
import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.engine.model.RecordDO;
import io.holoinsight.server.apm.engine.storage.RecordStorage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

/**
 * @author jiwliu
 * @version : RecordEsService.java, v 0.1 2022年10月12日 11:52 xiangwanpeng Exp $
 */
@ConditionalOnFeature("trace")
@Slf4j
public class RecordEsStorage<T extends RecordDO> implements RecordStorage<T> {

  @Autowired
  private RestHighLevelClient esClient;

  public void batchInsert(List<T> entities) throws IOException {
    if (CollectionUtils.isNotEmpty(entities)) {
      StopWatch stopWatch = StopWatch.createStarted();
      BulkRequest bulkRequest = new BulkRequest();
      entities.forEach(entity -> {
        String writeIndexName = writeIndexName(entity.indexName(), entity.getTimeBucket());
        bulkRequest.add(new IndexRequest(writeIndexName).source(EsGsonUtils.esGson().toJson(entity),
            XContentType.JSON));
      });
      BulkResponse bulkItemRsp = esClient.bulk(bulkRequest, RequestOptions.DEFAULT);
      log.info("[apm] batch_insert finish, engine=elasticsearch, size={}, cost={}",
          CollectionUtils.size(entities), stopWatch.getTime());
    }
  }

  private static String writeIndexName(String indexName, long timeBucket) {
    return indexName + Const.LINE + timeBucket / 1000000;
  }

}
