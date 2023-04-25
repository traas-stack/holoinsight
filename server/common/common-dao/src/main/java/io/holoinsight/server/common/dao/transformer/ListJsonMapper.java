/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.transformer;

import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/4/14 5:35 下午
 */
public class ListJsonMapper {
  public static String asString(List<String> list) {
    return J.toJson(list);
  }

  public static List<String> asObj(String list) {
    Type t = new TypeToken<List<String>>() {}.getType();
    return J.fromJson(list, t);
  }
}
