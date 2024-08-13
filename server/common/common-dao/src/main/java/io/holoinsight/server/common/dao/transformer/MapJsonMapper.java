/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.transformer;

import java.lang.reflect.Type;
import java.util.Map;

import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;

/**
 *
 * @author jsy1001de
 * @version 1.0: MapJsonMapper.java, v 0.1 2022年03月31日 8:52 下午 jinsong.yjs Exp $
 */
public class MapJsonMapper {
  public static String asString(Map<String, Object> map) {
    if (map != null) {
      return J.toJson(map);
    }
    return null;
  }

  public static Map<String, Object> asObj(String map) {
    if (map != null) {
      Type t = new TypeToken<Map<String, Object>>() {}.getType();
      return J.fromJson(map, t);
    }
    return null;
  }
}
