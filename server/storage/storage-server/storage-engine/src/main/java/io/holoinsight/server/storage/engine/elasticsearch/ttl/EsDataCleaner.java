/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.ttl;

import io.holoinsight.server.storage.common.constants.Const;
import io.holoinsight.server.storage.common.model.storage.Model;
import io.holoinsight.server.storage.core.ttl.DataCleaner;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.admin.indices.alias.get.GetAliasesRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.GetAliasesResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author jiwliu
 * @version : EsDataCleaner.java, v 0.1 2022年10月12日 20:38 xiangwanpeng Exp $
 */
@Slf4j
public class EsDataCleaner implements DataCleaner {

  private Map<String, Long> indexLatestSuccess = new HashMap<>();

  @Autowired
  private RestHighLevelClient esClient;

  @Override
  public void clean(Model model) throws IOException {
    String name = model.getName();
    long ttl = model.getTtl();
    long deadline = Long.parseLong(new DateTime().plus(-ttl).toString("yyyyMMdd"));
    Long latestSuccessDeadline = this.indexLatestSuccess.get(name);
    if (latestSuccessDeadline != null && deadline <= latestSuccessDeadline) {
      return;
    }
    GetAliasesResponse getAliasesResponse =
        esClient.indices().getAlias(new GetAliasesRequest(name), RequestOptions.DEFAULT);
    Collection<String> indices = getAliasesResponse.getAliases().keySet();
    if (CollectionUtils.isNotEmpty(indices)) {
      for (String index : indices) {
        String indexSuffix = index.substring(index.lastIndexOf(Const.LINE) + 1);
        if (!StringUtils.isNumeric(indexSuffix)) {
          continue;
        }
        long indexTimeBucket = Long.parseLong(indexSuffix);
        if (deadline >= indexTimeBucket) {
          AcknowledgedResponse acknowledgedResponse =
              esClient.indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
          if (acknowledgedResponse.isAcknowledged()) {
            this.indexLatestSuccess.put(name, deadline);
          }
        }
      }
    }
  }
}
