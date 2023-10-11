/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.data;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * <p>
 * created at 2023/9/24
 *
 * @author xzchaoo
 */
public class PartitionKeysUtils {

  public static String encode(LinkedHashMap<String, String> m) {
    return encode(new StringBuilder(), m);
  }

  public static String encode(StringBuilder reuse, LinkedHashMap<String, String> m) {
    if (reuse == null) {
      reuse = new StringBuilder();
    }
    reuse.setLength(0);
    for (Map.Entry<String, String> e : m.entrySet()) {
      reuse.append(e.getKey()).append('=').append(e.getValue()).append(',');
    }
    if (reuse.length() > 0) {
      reuse.setLength(reuse.length() - 1);
    }
    return reuse.toString();
  }

  public static Map<String, String> decode(String str) {
    String[] ss1 = str.split(",");
    Map<String, String> m = Maps.newHashMapWithExpectedSize(ss1.length);
    for (int i = 0; i < ss1.length; i++) {
      String[] ss2 = ss1[i].split("=");
      if (ss2.length != 2) {
        throw new IllegalArgumentException("invalid string: " + str);
      }
      m.put(ss2[0], ss2[1]);
    }
    return m;
  }
}
