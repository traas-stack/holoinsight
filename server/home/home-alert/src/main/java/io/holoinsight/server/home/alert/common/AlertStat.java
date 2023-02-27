/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;

import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.facade.InspectConfig;
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
      if (!inspectConfig.getStatus()) {
        continue;
      }
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

  public static Map<String, Map<String, Long>> statRuleTypeCount(
      ComputeTaskPackage computeTaskPackage) {
    Map<String, Map<String, Long>> map = new HashMap<>();

    if (CollectionUtils.isEmpty(computeTaskPackage.getInspectConfigs())) {
      return map;
    }

    for (InspectConfig inspectConfig : computeTaskPackage.getInspectConfigs()) {
      if (!inspectConfig.getStatus()) {
        continue;
      }
      map.compute(inspectConfig.getTenant(), (k, v) -> {
        String ruleType = inspectConfig.getRuleType();
        if (v == null) {
          Map<String, Long> typeMap = new HashMap();
          typeMap.put(ruleType, 1L);
          return typeMap;
        } else {
          v.compute(ruleType, (kk, vv) -> {
            if (vv == null) {
              return 1L;
            } else {
              return vv + 1;
            }
          });
          return v;
        }
      });
    }
    return map;
  }
}
