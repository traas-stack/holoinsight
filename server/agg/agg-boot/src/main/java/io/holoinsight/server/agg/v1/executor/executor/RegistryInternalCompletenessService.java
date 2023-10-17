/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import io.holoinsight.server.agg.v1.executor.CompletenessService;
import io.holoinsight.server.agg.v1.executor.ExpectedCompleteness;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.registry.core.collecttarget.CollectTargetKey;
import io.holoinsight.server.registry.core.collecttarget.CollectTargetStorage;
import io.holoinsight.server.registry.core.dim.ProdDimService;
import io.holoinsight.server.registry.core.template.CollectTemplate;
import io.holoinsight.server.registry.core.template.TemplateStorage;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/10/9
 *
 * @author xzchaoo
 */
@Slf4j
public class RegistryInternalCompletenessService implements CompletenessService {
  @Autowired
  private CollectTargetStorage collectTargetStorage;
  @Autowired
  private TemplateStorage templateStorage;
  @Autowired
  private ProdDimService prodDimService;

  @Override
  public ExpectedCompleteness getByCollectTableName(String tableName) {
    Set<Long> ids = templateStorage.get(tableName);
    if (ids == null || ids.isEmpty()) {
      return null;
    }

    Long id = ids.iterator().next();
    CollectTemplate t = templateStorage.get(id);
    if (t == null) {
      return null;
    }
    Set<CollectTargetKey> keys = collectTargetStorage.getKeysByTemplateId(id);

    List<String> uks = new ArrayList<>(keys.size());
    for (CollectTargetKey key : keys) {
      String dimId = key.getDimId();
      if (dimId.startsWith("dim2:")) {
        uks.add(dimId.substring("dim2:".length()));
      }
    }

    if (uks.isEmpty()) {
      return null;
    }

    ExpectedCompleteness ec = new ExpectedCompleteness();
    QueryExample qe = new QueryExample();
    qe.getParams().put("_uk", uks);
    Map<String, Map<String, Object>> m =
        prodDimService.queryByExample(t.getCollectRange().getCloudmonitor().getTable(), qe);
    ec.setExpectedTargets(new ArrayList<>(m.values()));
    return ec;
  }

  @Override
  public ExpectedCompleteness getByDimTable(String dimTable) {
    Map<String, Map<String, Object>> m = prodDimService.queryAll(dimTable);
    ExpectedCompleteness ec = new ExpectedCompleteness();
    ec.setExpectedTargets(new ArrayList<>(m.values()));
    return ec;
  }
}
