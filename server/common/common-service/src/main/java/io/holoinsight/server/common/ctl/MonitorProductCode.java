/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.ctl;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

/**
 * @author zanghaibo
 * @time 2023-05-31 6:56 上午
 */
@Getter
public enum MonitorProductCode {
  LOG("public_log_base_cn"), METRIC("public_index_spec_cn"), TRACE("public_link_spec_cn"), ALERT(
      "public_warn_spec_cn");

  MonitorProductCode(String code) {
    this.code = code;
  }

  private String code;

  private static Map<String, MonitorProductCode> strToProductCode = Maps.newConcurrentMap();

  static {
    strToProductCode.put("public_log_base_cn", LOG);
    strToProductCode.put("public_index_spec_cn", METRIC);
    strToProductCode.put("public_link_spec_cn", TRACE);
    strToProductCode.put("public_warn_spec_cn", ALERT);
  }

  public static MonitorProductCode strToMonitorProductCode(String str) {
    return strToProductCode.get(str);
  }
}
