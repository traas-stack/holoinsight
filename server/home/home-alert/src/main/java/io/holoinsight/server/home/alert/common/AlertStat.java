/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;

import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.model.data.InspectConfig;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-01-31 17:11:00
 */
public class AlertStat {
  public static Map<String, Long> statCount(ComputeTaskPackage computeTaskPackage) {
    Map<String, Long> map = new HashMap<>();

    if (CollectionUtils.isEmpty(computeTaskPackage.getInspectConfigs())) {
      return map;
    }

    for (InspectConfig inspectConfig : computeTaskPackage.getInspectConfigs()) {
      map.compute(inspectConfig.getTenant(), (k, v) -> {
        if (v == null) {
          return 1L;
        } else {
          return v + 1;
        }
      });
    }
    return map;
  }
}
