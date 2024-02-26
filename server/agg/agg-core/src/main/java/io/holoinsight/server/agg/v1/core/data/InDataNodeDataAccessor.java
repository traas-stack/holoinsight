/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.data;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import io.holoinsight.server.agg.v1.pb.AggProtos;

/**
 * <p>
 * created at 2023/10/17
 *
 * @author xzchaoo
 */
public class InDataNodeDataAccessor implements DataAccessor {
  private AggProtos.InDataNode in;
  private String fieldName;
  private AggProtos.BasicField bf;

  public void replace(AggProtos.InDataNode in) {
    this.in = in;
    this.fieldName = null;
    this.bf = null;
  }

  @Override
  public long getTimestamp() {
    return in.getTimestamp();
  }

  @Override
  public Map<String, String> getTags() {
    return in.getTagsMap();
  }

  @Override
  public String getTag(String name) {
    return in.getTagsOrDefault(name, null);
  }

  @Override
  public Collection<String> getFieldNames() {
    if (isSingleValue()) {
      return Collections.emptyList();
    }
    return in.getFieldsMap().keySet();
  }

  @Override
  public double getFloat64Field(String name) {
    if (isSingleValue()) {
      return in.getFloat64Value();
    }

    AggProtos.BasicField bf = in.getFieldsOrDefault(name, null);
    if (bf == null) {
      return 0;
    }
    return bf.getFloat64Value();
  }

  @Override
  public String getStringField(String name) {
    if (isSingleValue()) {
      return in.getBytesValue().toStringUtf8();
    }
    AggProtos.BasicField bf = in.getFieldsOrDefault(name, null);
    if (bf == null) {
      return null;
    }
    return bf.getBytesValue().toStringUtf8();
  }

  @Override
  public void bindFieldName(String fieldName) {
    if (!isSingleValue()) {
      this.fieldName = fieldName;
      this.bf = null;
    }
  }

  @Override
  public boolean isSingleValue() {
    return in.getType() <= 1;
  }

  @Override
  public String getFieldName() {
    return fieldName;
  }

  @Override
  public int getCount() {
    if (isSingleValue()) {
      return in.getCount();
    }
    ensureBf();
    return bf.getCount();
  }

  @Override
  public double getFloat64Field() {
    if (isSingleValue()) {
      return in.getFloat64Value();
    }
    ensureBf();
    return bf.getFloat64Value();
  }

  @Override
  public String getStringField() {
    if (isSingleValue()) {
      return in.getBytesValue().toStringUtf8();
    }
    ensureBf();
    return bf.getBytesValue().toStringUtf8();
  }

  private void ensureBf() {
    if (bf == null) {
      bf = in.getFieldsOrDefault(fieldName, null);
      // if bf is null, we have to treat it as a default value
      if (bf == null) {
        bf = AggProtos.BasicField.getDefaultInstance();
      }
    }
  }
}
