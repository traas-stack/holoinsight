/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import io.holoinsight.server.common.dao.entity.GaeaCollectConfigDO;

import java.util.Collections;
import java.util.List;

/**
 * 基于sqlite做一个cache0, 提高加载速度
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
public class CollectConfigDOCache {
  public GaeaCollectConfigDO get(long id) {
    return null;
  }

  public void delete(long id) {}

  public List<GaeaCollectConfigDO> selectAll() {
    return Collections.emptyList();
  }

  public void batchInsert(List<GaeaCollectConfigDO> configs) {}

  public void batchDelete(List<Long> ids) {}
}
