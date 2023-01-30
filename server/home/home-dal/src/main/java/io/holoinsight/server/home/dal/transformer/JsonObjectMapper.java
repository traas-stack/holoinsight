/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.transformer;

import io.holoinsight.server.common.J;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

/**
 *
 * @author xiangwanpeng
 * @version 1.0: JsonObjectMapper.java, v 0.1 2022年06月09日 3:32 下午 xiangwanpeng Exp $
 */
@Component
public class JsonObjectMapper {
  public static String asString(JsonObject jsonObject) {
    return J.toJson(jsonObject);
  }

  public static JsonObject asObj(String json) {
    return J.get().fromJson(json, JsonObject.class);
  }
}
