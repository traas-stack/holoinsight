/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import io.holoinsight.server.common.dao.entity.GaeaCollectConfigDO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
@Getter
public class CollectConfigDelta {
  public static final int STATUS_OK = 0;
  public static final int STATUS_RETRY = 1;
  public static final int STATUS_INVALID = 2;

  final String tableName;

  List<GaeaCollectConfigDO> add = new ArrayList<>(1);
  List<GaeaCollectConfigDO> delete = new ArrayList<>(1);

  @Setter
  private int status = STATUS_OK;

  public CollectConfigDelta(String tableName) {
    this.tableName = Objects.requireNonNull(tableName);
  }

  public void add(GaeaCollectConfigDO c) {
    for (GaeaCollectConfigDO gcc : add) {
      if (gcc.getId().equals(c.getId())) {
        return;
      }
    }
    add.add(c);
  }

  public void delete(GaeaCollectConfigDO c) {
    for (GaeaCollectConfigDO gcc : delete) {
      if (gcc.getId().equals(c.getId())) {
        return;
      }
    }
    delete.add(c);
  }

}
