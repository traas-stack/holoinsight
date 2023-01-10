/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.transformer;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.DisplayMenuConfig;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayMenuConfigMapper.java, v 0.1 2022年03月31日 8:52 下午 jinsong.yjs Exp $
 */
public class DisplayMenuConfigMapper {
  public static String asString(List<DisplayMenuConfig> config) {
    return J.toJson(config);
  }

  public static List<DisplayMenuConfig> asObj(String config) {
    Type t = new TypeToken<List<DisplayMenuConfig>>() {}.getType();
    return J.fromJson(config, t);
  }
}
