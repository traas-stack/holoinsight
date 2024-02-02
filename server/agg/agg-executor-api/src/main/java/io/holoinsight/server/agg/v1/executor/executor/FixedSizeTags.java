/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <p>
 * created at 2023/10/2
 *
 * @author xzchaoo
 */
@NoArgsConstructor
public final class FixedSizeTags implements Cloneable {
  public static final FixedSizeTags EMPTY = new FixedSizeTags(new String[0], new String[0], 0);

  @Getter
  @Setter
  private String[] keys;

  @Getter
  @Setter
  private String[] values;

  private transient int hash;

  private transient Map<String, String> map;

  public FixedSizeTags(String[] keys, String[] values) {
    this(keys, values, 0);
  }

  public FixedSizeTags(String[] keys) {
    this(keys, new String[keys.length], 0);
  }

  FixedSizeTags(String[] keys, String[] values, int hash) {
    Preconditions.checkNotNull(keys);
    if (values != null) {
      Preconditions.checkArgument(keys.length == values.length);
    } else {
      Preconditions.checkArgument(hash == 0);
    }
    this.keys = keys;
    this.values = values;
    this.hash = hash;
  }

  public Map<String, String> asMap() {
    Map<String, String> map = this.map;
    if (map == null) {
      map = Maps.newHashMapWithExpectedSize(keys.length);
      for (int i = 0; i < keys.length; i++) {
        map.put(keys[i], values[i]);
      }
    }
    return map;
  }

  public void replaceValues(String[] values) {
    Preconditions.checkArgument(keys.length == values.length);

    this.values = values;
    this.hash = 0;
    this.map = null;
  }

  @Override
  public int hashCode() {
    if (hash != 0) {
      return hash;
    }
    hash = Arrays.hashCode(values);
    return hash;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FixedSizeTags that = (FixedSizeTags) o;
    return Arrays.equals(values, that.values);
  }

  @Override
  public FixedSizeTags clone() {
    if (this == EMPTY) {
      return EMPTY;
    }
    String[] copy = Arrays.copyOf(values, values.length);
    return new FixedSizeTags(keys, copy, hash);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < keys.length; i++) {
      sb.append(keys[i]).append('=').append(values[i]).append(',');
    }
    if (sb.length() > 0) {
      sb.setLength(sb.length() - 1);
    }
    return sb.toString();
  }

  public void clearCache() {
    this.hash = 0;
    this.map = null;
  }

  public int size() {
    return keys.length;
  }

  public String getTagValue(String key, String defaultValue) {
    if (map != null) {
      String v = map.get(key);
      if (StringUtils.isEmpty(v)) {
        return defaultValue;
      }
      return v;
    }

    for (int i = 0; i < keys.length; i++) {
      if (keys[i].equals(key)) {
        if (StringUtils.isNotEmpty(values[i])) {
          return values[i];
        } else {
          return defaultValue;
        }
      }
    }

    return defaultValue;
  }

  @VisibleForTesting
  public FixedSizeTags with(String key, String value) {
    String[] keys = Arrays.copyOf(this.keys, this.keys.length + 1);
    String[] values = Arrays.copyOf(this.values, this.keys.length + 1);
    keys[keys.length - 1] = key;
    values[values.length - 1] = value;
    return new FixedSizeTags(keys, values);
  }
}
