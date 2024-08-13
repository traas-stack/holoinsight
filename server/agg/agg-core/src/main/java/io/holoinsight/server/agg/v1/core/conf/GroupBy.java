/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2023/9/21
 *
 * @author xzchaoo
 */
@Data
public class GroupBy {
  public static final int DEFAULT_KEY_LIMIT = 1000;
  private List<GroupByItem> items = new ArrayList<>();
  private int keyLimit = DEFAULT_KEY_LIMIT;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private transient String[] groupTagKeys;

  public static GroupBy create(List<GroupByItem> groupBys) {
    GroupBy gbs = new GroupBy();
    gbs.setItems(groupBys);
    return gbs;
  }

  public boolean isEmpty() {
    return CollectionUtils.isEmpty(items);
  }

  public String[] getGroupTagKeys() {
    String[] keys = groupTagKeys;
    if (keys == null) {
      keys = new String[items.size()];
      for (int i = 0; i < items.size(); i++) {
        keys[i] = items.get(i).getAs();
      }
      this.groupTagKeys = keys;
    }
    return keys;
  }

  public void fillTagValuesFromTags(String[] values, Map<String, String> tags) {
    Preconditions.checkArgument(values.length == items.size());
    for (int i = 0; i < items.size(); i++) {
      values[i] = items.get(i).get(tags);
    }
  }

  public void fillTagValuesFromObject(String[] values, Map<String, Object> obj) {
    Preconditions.checkArgument(values.length == items.size());
    for (int i = 0; i < items.size(); i++) {
      values[i] = items.get(i).getAsStringFromObject(obj);
    }
  }

  public void fixDefaultValue() {
    for (GroupByItem item : items) {
      Preconditions.checkArgument(StringUtils.isNotEmpty(item.getTag()));
      if (StringUtils.isEmpty(item.getAs())) {
        item.setAs(item.getTag());
      }
    }
  }

}
