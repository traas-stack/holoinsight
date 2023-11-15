/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.common;

import io.holoinsight.server.home.biz.plugin.config.MetaLabel;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: GaeaConvertUtil.java, v 0.1 2022年11月16日 下午5:50 jinsong.yjs Exp $
 */
public class GaeaConvertUtil {

  public static GaeaCollectRange convertCollectRange(CloudMonitorRange cloudMonitorRange) {
    GaeaCollectRange gaeaCollectRange = new GaeaCollectRange();
    gaeaCollectRange.setType("cloudmonitor");
    gaeaCollectRange.setCloudmonitor(cloudMonitorRange);
    return gaeaCollectRange;
  }

  public static GaeaCollectRange convertCentralCollectRange(CloudMonitorRange cloudMonitorRange) {
    GaeaCollectRange gaeaCollectRange = new GaeaCollectRange();
    gaeaCollectRange.setType("central");
    gaeaCollectRange.setCloudmonitor(cloudMonitorRange);
    return gaeaCollectRange;
  }

  public static GaeaCollectRange convertAppsCollectRange(String tableName, List<String> appList) {
    GaeaCollectRange gaeaCollectRange = new GaeaCollectRange();
    gaeaCollectRange.setType("cloudmonitor");
    CloudMonitorRange cloudMonitorRange = new CloudMonitorRange();
    cloudMonitorRange.setTable(tableName);
    Map<String, List<String>> conditionMap = new HashMap<>();
    conditionMap.put("app", appList);
    cloudMonitorRange.setCondition(Collections.singletonList(conditionMap));
    gaeaCollectRange.setCloudmonitor(cloudMonitorRange);
    return gaeaCollectRange;
  }

  public static CloudMonitorRange convertIpsCollectRange(String tableName, List<String> ipList) {
    CloudMonitorRange cloudMonitorRange = new CloudMonitorRange();
    cloudMonitorRange.setTable(tableName);
    Map<String, List<String>> conditionMap = new HashMap<>();
    conditionMap.put("ip", ipList);
    cloudMonitorRange.setCondition(Collections.singletonList(conditionMap));
    return cloudMonitorRange;
  }

  public static CloudMonitorRange convertCloudMonitorRange(String tableName, MetaLabel metaLabel,
      List<String> range) {
    CloudMonitorRange cloudMonitorRange = new CloudMonitorRange();
    cloudMonitorRange.setTable(tableName);
    Map<String, List<String>> conditionMap = new HashMap<>();
    if (metaLabel == MetaLabel.allApp) {
      cloudMonitorRange.setCondition(Collections.singletonList(conditionMap));
    } else if (!CollectionUtils.isEmpty(range)) {
      conditionMap.put("app", range);
      cloudMonitorRange.setCondition(Collections.singletonList(conditionMap));
    }

    return cloudMonitorRange;
  }

  public static CloudMonitorRange convertCloudMonitorRange(String tableName,
      Map<String, List<String>> conditionMap) {
    CloudMonitorRange cloudMonitorRange = new CloudMonitorRange();
    cloudMonitorRange.setTable(tableName);
    cloudMonitorRange.setCondition(Collections.singletonList(conditionMap));
    return cloudMonitorRange;
  }

  public static Map<String, Object> getCenterExecutorSelector() {

    Map<String, Object> executorSelector = new HashMap<>();
    executorSelector.put("type", "central");
    executorSelector.put("central", new HashMap<>());
    return executorSelector;
  }

  public static Map<String, Object> getLocalExecutorSelector() {

    Map<String, Object> executorSelector = new HashMap<>();
    executorSelector.put("type", "sidecar");
    executorSelector.put("sidecar", new HashMap<>());

    return executorSelector;
  }
}
