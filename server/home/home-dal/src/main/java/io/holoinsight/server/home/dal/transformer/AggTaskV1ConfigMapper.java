/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.dal.transformer;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.common.J;

import java.lang.reflect.Type;

/**
 * @author jsy1001de
 * @version 1.0: AggTaskV1ConfigMapper.java, Date: 2023-12-06 Time: 15:44
 */
public class AggTaskV1ConfigMapper {
  public static String asString(AggTask json) {
    return J.toJson(json);
  }

  public static AggTask asObj(String json) {
    Type t = new TypeToken<AggTask>() {}.getType();
    return J.fromJson(json, t);
  }
}
