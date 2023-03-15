/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

/**
 * @author masaimu
 * @version 2023-03-13 21:06:00
 */
public class HoloInsightCriticalException extends RuntimeException {

  public String name;
  public long timestamp;

  public HoloInsightCriticalException(String message, String name, long timestamp) {
    super(message);
    this.name = name;
    this.timestamp = timestamp;
  }

  public HoloInsightCriticalException(String message, String name) {
    super(message);
    this.name = name;
    this.timestamp = System.currentTimeMillis();
  }
}
