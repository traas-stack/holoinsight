/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import lombok.Getter;

/**
 * 与 CollectConfigDelta 类似, 但是解析成了我们的业务模型
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
@Getter
public class CollectTemplateDelta {
  private static final Logger LOGGER = LoggerFactory.getLogger(CollectTemplateDelta.class);

  final String tableName;

  List<CollectTemplate> add = new ArrayList<>(1);

  List<CollectTemplate> delete = new ArrayList<>(1);

  public CollectTemplateDelta(String tableName) {
    this.tableName = Objects.requireNonNull(tableName);
  }

  public static CollectTemplateDelta one(CollectTemplate t) {
    CollectTemplateDelta d = new CollectTemplateDelta(t.getTableName());
    d.add = Collections.singletonList(t);
    d.delete = Collections.emptyList();
    return d;
  }

  public CollectTemplate getAdd0() {
    return add.size() > 0 ? add.get(0) : null;
  }

  public void add(CollectTemplate t) {
    for (CollectTemplate t2 : add) {
      if (t.getId() == t2.getId()) {
        return;
      }
    }
    add.add(t);
  }

  public void delete(CollectTemplate t) {
    for (CollectTemplate t2 : delete) {
      if (t.getId() == t2.getId()) {
        return;
      }
    }
    delete.add(t);
  }
}
