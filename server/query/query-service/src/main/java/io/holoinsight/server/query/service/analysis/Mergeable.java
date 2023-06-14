/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.analysis;

public interface Mergeable {
  void merge(Mergeable other);
}
