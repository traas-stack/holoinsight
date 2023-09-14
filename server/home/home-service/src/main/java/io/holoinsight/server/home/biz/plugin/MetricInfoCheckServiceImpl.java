/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin;

/**
 * @author masaimu
 * @version 2023-09-14 10:45:00
 */
public class MetricInfoCheckServiceImpl implements MetricInfoCheckService {
  @Override
  public boolean needWorkspace(String product) {
    return false;
  }
}
