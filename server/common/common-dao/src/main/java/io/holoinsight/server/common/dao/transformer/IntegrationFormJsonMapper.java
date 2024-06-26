/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.transformer;

import io.holoinsight.server.common.dao.entity.dto.IntegrationFormDTO;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author xiangwanpeng
 * @date 2022/4/14 5:35 下午
 */
public class IntegrationFormJsonMapper {
  public static String asString(IntegrationFormDTO form) {
    return J.toJson(form);
  }

  public static IntegrationFormDTO asObj(String str) {
    Type t = new TypeToken<IntegrationFormDTO>() {}.getType();
    return J.fromJson(str, t);
  }
}
