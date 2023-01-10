/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.transformer;

import io.holoinsight.server.home.dal.model.dto.IntegrationMetricsDTO;
import io.holoinsight.server.common.J;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * @author xiangwanpeng
 * @date 2022/4/14 5:35 下午
 */
public class MetricJsonMapper {
  public static String asString(IntegrationMetricsDTO metrics) {
    return J.toJson(metrics);
  }

  public static IntegrationMetricsDTO asObj(String str) {
    Type t = new TypeToken<IntegrationMetricsDTO>() {}.getType();
    return J.fromJson(str, t);
  }
}
