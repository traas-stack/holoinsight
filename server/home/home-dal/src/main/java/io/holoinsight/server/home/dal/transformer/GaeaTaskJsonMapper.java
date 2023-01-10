/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.transformer;

import io.holoinsight.server.common.J;
import io.holoinsight.server.registry.model.integration.GaeaTask;

/**
 *
 * @author jsy1001de
 * @version 1.0: GaeaCollectConfigJsonMapper.java, v 0.1 2022年03月31日 8:52 下午 jinsong.yjs Exp $
 */
public class GaeaTaskJsonMapper {
  public static String asString(GaeaTask json) {
    return J.toJson(json);
  }

  public static <T extends GaeaTask> T asObj(String json, Class<T> cls) {
    return J.fromJson(json, cls);
  }
}
