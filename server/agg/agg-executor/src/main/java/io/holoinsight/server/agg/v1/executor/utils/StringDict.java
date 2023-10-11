/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;

import lombok.Getter;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
public class StringDict {
  private Map<String, Integer> index = new HashMap<>();
  @Getter
  private List<String> list = new ArrayList<>();

  public int add(String str) {
    Integer index = this.index.get(str);
    if (index != null) {
      return index;
    }
    index = list.size();
    list.add(str);
    this.index.put(str, index);
    return index;
  }

  public String get(int index) {
    Preconditions.checkArgument(index < list.size());
    return list.get(index);
  }
}
