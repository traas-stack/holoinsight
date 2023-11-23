/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.output;

import java.util.Map;

import org.apache.commons.text.lookup.StringLookup;

import io.holoinsight.server.agg.v1.executor.executor.FixedSizeTags;

/**
 * <p>
 * created at 2023/10/25
 *
 * @author xzchaoo
 */
public class AggStringLookup implements StringLookup {

  private static final String UNKNOWN = "UNKNOWN";

  private String fieldName;
  private FixedSizeTags tags;
  private Map<String, String> partition;

  public void bind(String fieldName, FixedSizeTags tags, Map<String, String> partition) {
    this.fieldName = fieldName;
    this.tags = tags;
    this.partition = partition;
  }

  @Override
  public String lookup(String key) {
    if (fieldName != null && "field".equals(key)) {
      return fieldName;
    }

    if (tags != null && key.startsWith("tag.")) {
      return tags.getTagValue(key.substring("tag.".length()), UNKNOWN);
    }

    if (partition != null && key.startsWith("partition.")) {
      return partition.getOrDefault(key.substring("partition.".length()), UNKNOWN);
    }

    return UNKNOWN;
  }
}
