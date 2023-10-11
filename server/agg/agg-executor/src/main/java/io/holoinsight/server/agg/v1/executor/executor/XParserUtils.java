/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.agg.v1.core.conf.GroupBy;
import io.holoinsight.server.agg.v1.core.conf.GroupByItem;
import io.holoinsight.server.agg.v1.core.conf.Select;
import io.holoinsight.server.agg.v1.core.conf.SelectItem;
import io.holoinsight.server.agg.v1.core.conf.Where;
import io.holoinsight.server.agg.v1.pb.AggProtos;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
public class XParserUtils {
  public static XAggTask parse(AggTask aggTask) {
    if (aggTask.getGroupBy() != null) {
      if (aggTask.getGroupBy().getKeyLimit() <= 0) {
        aggTask.getGroupBy().setKeyLimit(GroupBy.DEFAULT_KEY_LIMIT);
      }
      for (GroupByItem item : aggTask.getGroupBy().getItems()) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(item.getTag()));
        if (StringUtils.isEmpty(item.getAs())) {
          item.setAs(item.getTag());
        }
      }
    }

    return new XAggTask(aggTask, parseSelect(aggTask.getSelect()), parseWhere(aggTask.getWhere()));
  }

  private static XWhere parseWhere(Where w) {
    if (w == null || MapUtils.isEmpty(w.getSimple())) {
      return XWhere.ALWAYS_TRUE;
    }

    return in -> testWhere(w, in);
  }

  private static XSelect parseSelect(Select s) {
    List<SelectItem> items = s.getItems();
    List<XSelectItem> xitems = new ArrayList<>(items.size());
    for (int i = 0; i < items.size(); i++) {
      SelectItem item = items.get(i);
      XSelectItem xitem = parseSelectItem(i, item);
      xitems.add(xitem);
    }
    return new XSelect(s, xitems);
  }

  private static XSelectItem parseSelectItem(int index, SelectItem s) {
    Preconditions.checkNotNull(s, "SelectItem");
    Preconditions.checkNotNull(s.getAgg(), "SelectItem.agg");
    Preconditions.checkNotNull(s.getAgg().getType(), "SelectItem.agg");
    Preconditions.checkNotNull(s.getElect(), "SelectItem.elect");
    Preconditions.checkNotNull(s.getElect().getField(), "SelectItem.elect");
    Preconditions.checkNotNull(s.getElect().getField(), "SelectItem.elect");
    Preconditions.checkArgument(StringUtils.isNotEmpty(s.getAs()), "SelectItem.as");

    return new XSelectItem(s, index, parseWhere(s.getWhere()));
  }

  private static boolean testWhere(Where w, AggProtos.InDataNode in) {
    if (w == null) {
      return true;
    }
    Map<String, Set<String>> simple = w.getSimple();
    if (MapUtils.isEmpty(simple)) {
      return true;
    }

    for (Map.Entry<String, Set<String>> e : simple.entrySet()) {
      String key = e.getKey();
      boolean not = key.startsWith("!");
      if (not) {
        key = key.substring(1);
      }
      String value = in.getTagsOrDefault(key, "-");
      if (not) {
        if (e.getValue().contains(value)) {
          return false;
        }
      } else {
        if (!e.getValue().contains(value)) {
          return false;
        }
      }
    }

    return true;
  }
}
