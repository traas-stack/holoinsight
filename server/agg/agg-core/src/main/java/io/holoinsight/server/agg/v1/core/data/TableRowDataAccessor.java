/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

import io.holoinsight.server.agg.v1.pb.AggProtos;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/10/17
 *
 * @author xzchaoo
 */
@Slf4j
public class TableRowDataAccessor implements DataAccessor {
  private Meta meta;
  private AggProtos.Table.Row row;

  private Map<String, String> tags;

  private String fieldName;

  private AggProtos.BasicField bf;

  public void replace(Meta meta, AggProtos.Table.Row row) {
    this.meta = meta;
    this.row = row;
    this.tags = null;
    this.bf = null;
  }

  private void ensureBf() {
    if (bf == null) {
      bf = row.getFieldValues(meta.fieldIndex.get(fieldName));
    }
  }

  @Override
  public long getTimestamp() {
    return row.getTimestamp();
  }

  @Override
  public Map<String, String> getTags() {
    if (tags == null) {
      tags = new HashMap<>();
      for (int i = 0; i < meta.header.getTagKeysCount(); i++) {
        String key = meta.header.getTagKeys(i);
        String value = row.getTagValues(i);
        tags.put(key, value);
      }
    }
    return tags;
  }

  @Override
  public String getTag(String name) {
    Integer index = meta.tagIndex.get(name);
    if (index == null) {
      return null;
    }
    return row.getTagValues(index);
  }

  @Override
  public Collection<String> getFieldNames() {
    return meta.fieldIndex.keySet();
  }

  @Override
  public double getFloat64Field(String name) {
    Integer index = meta.fieldIndex.get(name);
    if (index == null) {
      return 0;
    }
    AggProtos.BasicField bf = row.getFieldValues(index);
    return bf.getFloat64Value();
  }

  @Override
  public String getStringField(String name) {
    Integer index = meta.fieldIndex.get(name);
    if (bf == null) {
      return null;
    }
    AggProtos.BasicField bf = row.getFieldValues(index);
    return bf.getBytesValue().toStringUtf8();
  }

  @Override
  public void bindFieldName(String fieldName) {
    this.fieldName = fieldName;
    this.bf = null;
  }

  @Override
  public boolean isSingleValue() {
    return false;
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }

  @Override
  public int getCount() {
    ensureBf();
    return bf.getCount();
  }

  @Override
  public double getFloat64Field() {
    ensureBf();
    return bf.getFloat64Value();
  }

  @Override
  public String getStringField() {
    ensureBf();
    return bf.getBytesValue().toStringUtf8();
  }

  @Data
  public static class Meta {
    private final AggProtos.Table.Header header;
    private final Map<String, Integer> tagIndex;
    private final Map<String, Integer> fieldIndex;

    public Meta(AggProtos.Table.Header header) {
      this.header = header;
      tagIndex = Maps.newHashMapWithExpectedSize(header.getTagKeysCount());
      fieldIndex = Maps.newHashMapWithExpectedSize(header.getFieldKeysCount());
      for (int i = 0; i < header.getTagKeysCount(); i++) {
        tagIndex.put(header.getTagKeys(i), i);
      }
      for (int i = 0; i < header.getFieldKeysCount(); i++) {
        fieldIndex.put(header.getFieldKeys(i), i);
      }
    }
  }
}
