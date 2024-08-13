/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.data;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * An abstraction of reading data
 * <p>
 * created at 2023/10/17
 *
 * @author xzchaoo
 */
public interface DataAccessor {
  long getTimestamp();

  Map<String, String> getTags();

  default String getTagOrDefault(String name, String defaultValue) {
    String v = getTag(name);
    if (StringUtils.isEmpty(v)) {
      return defaultValue;
    }
    return v;
  }

  String getTag(String name);

  Collection<String> getFieldNames();

  double getFloat64Field(String name);

  String getStringField(String name);

  void bindFieldName(String fieldName);

  boolean isSingleValue();

  @Deprecated
  String getFieldName();

  int getCount();

  double getFloat64Field();

  String getStringField();
}
