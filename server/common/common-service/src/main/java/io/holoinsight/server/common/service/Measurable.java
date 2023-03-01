/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

/**
 * Measure the data
 * 
 * @author masaimu
 * @version 2023-03-10 16:38:00
 */
public interface Measurable {

  /**
   * @return data size
   */
  long measure();
}
