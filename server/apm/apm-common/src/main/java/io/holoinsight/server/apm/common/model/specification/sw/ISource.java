/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification.sw;

public interface ISource {
  long getTimeBucket();

  void setTimeBucket(long timeBucket);

  String getEntityId();

  default void prepare() {}
}
