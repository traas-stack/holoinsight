/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.transformer;

import java.lang.reflect.Type;

import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.dao.entity.GaeaCollectRange;

/**
 *
 * @author jsy1001de
 * @version 1.0: GaeaCollectRangeMapper.java, v 0.1 2022年03月31日 8:52 下午 jinsong.yjs Exp $
 */
public class GaeaCollectRangeMapper {
  public static String asString(GaeaCollectRange collectRange) {
    return J.toJson(collectRange);
  }

  public static GaeaCollectRange asObj(String collectRange) {
    Type t = new TypeToken<GaeaCollectRange>() {}.getType();
    return J.fromJson(collectRange, t);
  }
}
