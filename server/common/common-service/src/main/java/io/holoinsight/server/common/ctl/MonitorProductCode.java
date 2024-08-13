/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.ctl;

import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

@Getter
public enum MonitorProductCode {
  LOG("public_log_base_cn"), METRIC("public_index_spec_cn"), TRACE("public_link_spec_cn"), ALERT(
      "public_warn_spec_cn"), API("software_api_base_cn"), NOTIFY_SMS(
          "public_message_base_cn"), NOTIFY_VOICE("public_voice_base_cn");

  MonitorProductCode(String code) {
    this.code = code;
  }

  private String code;

  private static Map<String, MonitorProductCode> strToProductCode = Maps.newConcurrentMap();

  static {
    for (MonitorProductCode value : MonitorProductCode.values()) {
      strToProductCode.put(value.code, value);
    }
  }

  public static MonitorProductCode strToMonitorProductCode(String str) {
    return strToProductCode.get(str);
  }
}
