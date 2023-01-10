/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;


import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: CloudMonitorRange.java, v 0.1 2022年04月13日 2:20 下午 jinsong.yjs Exp $
 */
@Data
public class CloudMonitorRange {

  public String table;
  public Boolean all;
  public List<Map<String, List<String>>> condition;

  public boolean isEqual(CloudMonitorRange cloudmonitor) {
    if (cloudmonitor == null) {
      return false;
    }
    if (!StringUtils.equals(this.table, cloudmonitor.table)) {
      return false;
    }
    sortConditionValues();
    cloudmonitor.sortConditionValues();
    return CollectionUtils.containsAny(condition, cloudmonitor.condition)
        && CollectionUtils.containsAny(cloudmonitor.condition, condition);
  }

  private void sortConditionValues() {
    if (condition == null) {
      return;
    }
    for (Map<String, List<String>> entry : condition) {
      for (List<String> list : entry.values()) {
        Collections.sort(list);
      }
    }
  }
}
