/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.measure;

/**
 * @author masaimu
 * @version 2023-03-05 21:11:00
 */
public class HoloInsightRequestException extends Exception {

  public int sc;

  public HoloInsightRequestException(String message) {
    super(message);
  }

  public HoloInsightRequestException(String message, int sc) {
    super(message);
    this.sc = sc;
  }

}
