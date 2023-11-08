/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Agg task version info
 * <p>
 * created at 2023/11/1
 *
 * @author xzchaoo
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AggTaskVersion {
  private long id;
  private long version;

  public static AggTaskVersion of(AggTask t) {
    return new AggTaskVersion(t.getId(), t.getVersion());
  }

  public boolean hasSameVersion(AggTask t) {
    return id == t.getId() && version == t.getVersion();
  }

  public void updateTo(AggTask t) {
    id = t.getId();
    version = t.getVersion();
  }

  public boolean isEmpty() {
    return id == 0;
  }
}
