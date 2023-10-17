/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.data;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2023/9/23
 *
 * @author xzchaoo
 */
@Data
public final class AggTaskKey {
  private final String tenant;
  private final String aggId;
  private final String partition;

  @Setter(AccessLevel.NONE)
  @Getter(AccessLevel.NONE)
  @EqualsAndHashCode.Exclude
  private transient Map<String, String> partitionInfo;

  public AggTaskKey(String tenant, String aggId) {
    this(tenant, aggId, "");
  }

  public AggTaskKey(String tenant, String aggId, String partition) {
    this.tenant = Objects.requireNonNull(tenant);
    this.aggId = Objects.requireNonNull(aggId);
    this.partition = Objects.requireNonNull(partition);
  }

  @Override
  public String toString() {
    return tenant + ":" + aggId + ":" + partition;
  }

  public Map<String, String> getPartitionInfo() {
    if (partitionInfo == null) {
      if (StringUtils.isEmpty(partition)) {
        partitionInfo = Collections.emptyMap();
      } else {
        partitionInfo = PartitionKeysUtils.decode(partition);
      }
    }
    return partitionInfo;
  }
}
