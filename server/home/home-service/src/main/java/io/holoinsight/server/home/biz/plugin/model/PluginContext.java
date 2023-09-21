/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author masaimu
 * @version 2022-10-31 17:12:00
 */
public class PluginContext {

  private AlertNotifyRecordDTO alertNotifyRecord;

  public CountDownLatch latch;

  private Map<String, Object> context = new HashMap<>();

  public Object get(String key) {
    return context.get(key);
  }

  public Object getOrDefault(String key, Object defaultValue) {
    return context.getOrDefault(key, defaultValue);
  }

  public void put(String key, Object value) {
    this.context.put(key, value);
  }

  public AlertNotifyRecordDTO getAlertNotifyRecord() {
    return alertNotifyRecord;
  }

  public void setAlertNotifyRecord(AlertNotifyRecordDTO alertNotifyRecord) {
    this.alertNotifyRecord = alertNotifyRecord;
  }
}
