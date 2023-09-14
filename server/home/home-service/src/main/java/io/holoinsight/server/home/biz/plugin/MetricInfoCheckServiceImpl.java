/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
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
