/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.transformer;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 *
 * @author jsy1001de
 * @version 1.0: CustomPluginConfMapper.java, v 0.1 2022年03月14日 8:46 下午 jinsong.yjs Exp $
 */
public class CustomPluginConfMapper {
  public static String asString(CustomPluginConf conf) {
    return J.toJson(conf);
  }

  public static CustomPluginConf asObj(String conf) {
    Type t = new TypeToken<CustomPluginConf>() {}.getType();
    return J.fromJson(conf, t);
  }
}
