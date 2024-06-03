/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.dao.transformer;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MonitorInstanceCfg;

import java.lang.reflect.Type;

/**
 * @author jsy1001de
 * @version 1.0: MonitorInstanceCfgMapper.java, Date: 2024-06-03 Time: 11:07
 */
public class MonitorInstanceCfgMapper {
  public static String asString(MonitorInstanceCfg map) {
    if (map != null) {
      return J.toJson(map);
    }
    return null;
  }

  public static MonitorInstanceCfg asObj(String map) {
    if (map != null) {
      Type t = new TypeToken<MonitorInstanceCfg>() {}.getType();
      return J.fromJson(map, t);
    }
    return null;
  }
}
