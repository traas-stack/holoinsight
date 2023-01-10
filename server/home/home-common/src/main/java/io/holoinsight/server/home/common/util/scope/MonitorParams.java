/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zanghaibo
 * @time 2022-10-20 9:16 上午
 */

@Data
public class MonitorParams {

  private Map<String, String> params = new HashMap<>();

  public void put(String k, String v) {
    params.put(k, v);
  }

  public void remove(String k) {
    params.remove(k);
  }

  public String get(String k) {
    return params.get(k);
  }

  public Boolean contains(String k) {
    return params.containsKey(k);
  }
}
