/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.ttl;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.model.storage.Model;
import io.holoinsight.server.apm.core.ttl.DataCleaner;
import io.holoinsight.server.apm.engine.elasticsearch.HoloinsightEsConfiguration;
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

@Slf4j
public class EsDataCleaner implements DataCleaner {

  private Map<String, Long> indexLatestSuccess = new HashMap<>();

  @Autowired
  private RestHighLevelClient esClient;
  @Autowired
  private HoloinsightEsConfiguration esConfiguration;

  protected RestHighLevelClient client() {
    return esClient;
  }

  protected long ttl(Model model) {
    return model.getTtl() != 0 ? model.getTtl() : esConfiguration.getTtl() * 60000L * 60 * 24;
  }

  @Override
  public void clean(Model model) throws IOException {
    String name = model.getName();
    long ttl = ttl(model);
    long deadline = Long.parseLong(new DateTime().plus(-ttl).toString("yyyyMMdd"));
    Long latestSuccessDeadline = this.indexLatestSuccess.get(name);
    if (latestSuccessDeadline != null && deadline <= latestSuccessDeadline) {
      return;
    }
    GetAliasesResponse getAliasesResponse =
        client().indices().getAlias(new GetAliasesRequest(name), RequestOptions.DEFAULT);
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
              client().indices().delete(new DeleteIndexRequest(index), RequestOptions.DEFAULT);
          if (acknowledgedResponse.isAcknowledged()) {
            this.indexLatestSuccess.put(name, deadline);
          }
        }
      }
    }
  }
}
