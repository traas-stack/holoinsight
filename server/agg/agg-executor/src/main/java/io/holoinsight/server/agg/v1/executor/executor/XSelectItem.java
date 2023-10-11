/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import io.holoinsight.server.agg.v1.core.conf.SelectItem;
import lombok.Data;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
@Data
public class XSelectItem {
  private final SelectItem inner;
  private final int index;
  private final XWhere where;

  XSelectItem(SelectItem inner, int index, XWhere where) {
    this.inner = inner;
    this.index = index;
    this.where = where;
  }
}
