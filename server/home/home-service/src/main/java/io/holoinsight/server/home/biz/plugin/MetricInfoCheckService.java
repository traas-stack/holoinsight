/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin;

/**
 * @author masaimu
 * @version 2023-09-14 10:37:00
 */
public interface MetricInfoCheckService {

  boolean needWorkspace(String product);
}
