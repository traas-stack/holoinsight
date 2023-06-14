/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.utils.DownSampling;
import io.holoinsight.server.apm.common.utils.TimeBucket;
import io.holoinsight.server.apm.engine.elasticsearch.utils.ApmGsonUtils;
import io.holoinsight.server.apm.engine.model.RecordDO;
import io.holoinsight.server.apm.engine.storage.WritableStorage;
import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

@ConditionalOnFeature("trace")
@Slf4j
public class RecordEsStorage<T extends RecordDO> implements WritableStorage<T> {

  @Autowired
  private RestHighLevelClient esClient;

  protected RestHighLevelClient client() {
    return esClient;
  }

  public void insert(List<T> entities) throws IOException {

    if (CollectionUtils.isNotEmpty(entities)) {
      BulkRequest bulkRequest = new BulkRequest();
      entities.forEach(entity -> {
        String writeIndexName = writeIndexName(entity);

        bulkRequest.add(new IndexRequest(writeIndexName).opType(DocWriteRequest.OpType.CREATE)
            .source(ApmGsonUtils.apmGson().toJson(entity), XContentType.JSON));
      });
      BulkResponse bulkItemRsp = client().bulk(bulkRequest, RequestOptions.DEFAULT);
      if (bulkItemRsp.hasFailures()) {
        throw new RuntimeException(bulkItemRsp.buildFailureMessage());
      }
    }
  }

  @Override
  public String writeIndexName(T entity) {
    long timeBucket = TimeBucket.getTimeBucket(entity.getTimestamp(), DownSampling.Second);
    return entity.indexName() + Const.LINE + timeBucket / 1000000;
  }

}
