/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.collecttarget;

import lombok.Data;

/**
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
@Data
public final class CollectTargetKey {
  private final long templateId;
  // TODO dimid不再是long了 要适配其他case
  private final String dimId;

  public static CollectTargetKey ofDim(long templateId, long dimId) {
    return new CollectTargetKey(templateId, Long.toString(dimId));
  }

  public static CollectTargetKey of(long templateId, Target target) {
    return new CollectTargetKey(templateId, target.getId());
  }
}
