/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.holoinsight.server.agg.v1.core.conf.AggFunc;
import io.holoinsight.server.agg.v1.core.conf.Select;
import io.holoinsight.server.agg.v1.core.conf.SelectItem;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
@Data
public class XSelect {
  public static final String NO_FIELD = "-";

  private final Select inner;
  private final List<XSelectItem> items;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private transient Map<String, Map<String, List<XSelectItem>>> electToItemMap;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private transient Map<String, XSelectItem> asToItemMap;

  XSelect(Select inner, List<XSelectItem> items) {
    this.inner = inner;
    this.items = items;
  }

  public XSelectItem getSelectItemByAs(String as) {
    if (asToItemMap == null) {
      asToItemMap = new HashMap<>();
      for (XSelectItem item : items) {
        asToItemMap.put(item.getInner().getAs(), item);
      }
    }
    return asToItemMap.get(as);
  }

  public Map<String, List<XSelectItem>> getSelectItem(String metric) {
    if (electToItemMap == null) {
      electToItemMap = new HashMap<>();
      for (int index = 0; index < items.size(); index++) {
        XSelectItem item = items.get(index);
        SelectItem.Elect e = item.getInner().getElect();

        int aggType = item.getInner().getAgg().getTypeInt();
        if (aggType == AggFunc.TYPE_HLL || aggType == AggFunc.TYPE_COUNT) {
          electToItemMap.computeIfAbsent(e.getMetric(), i -> new HashMap<>())
              .computeIfAbsent(NO_FIELD, i -> new LinkedList<>()) //
              .add(item);
        } else {
          electToItemMap.computeIfAbsent(e.getMetric(), i -> new HashMap<>())
              .computeIfAbsent(e.getField(), i -> new LinkedList<>()) //
              .add(item);
        }
      }
    }
    return electToItemMap.get(metric);
  }

  public XSelectItem getItem(int index) {
    return items.get(index);
  }

}
