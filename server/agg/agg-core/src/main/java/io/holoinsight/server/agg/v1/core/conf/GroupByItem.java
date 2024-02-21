/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;

import io.holoinsight.server.agg.v1.core.data.DataAccessor;
import lombok.Data;

/**
 * <p>
 * created at 2023/9/20
 *
 * @author xzchaoo
 */
@Data
public class GroupByItem {
  public static final String DEFAULT_VALUE = "-";

  /**
   * tag to group by
   */
  @Nonnull
  private String tag;

  /**
   * default value for tag if it does not exist
   */
  @Nullable
  private String defaultValue = DEFAULT_VALUE;

  @Nullable
  private String as;

  /**
   * Generate group tag through joining metadata
   */
  @Nullable
  private JoinMeta joinMeta;

  public GroupByItem() {}

  GroupByItem(String tag, String defaultValue, String as) {
    this.tag = tag;
    this.defaultValue = defaultValue;
    this.as = as;
  }

  public static GroupByItem of(String tag) {
    Preconditions.checkArgument(StringUtils.isNotEmpty(tag), "tag");

    return new GroupByItem(tag, DEFAULT_VALUE, tag);
  }

  public String get(DataAccessor da) {
    String v = da.getTag(tag);
    return StringUtils.isEmpty(v) ? defaultValue : v;
  }

  public String get(Map<String, String> tags) {
    String v = tags.get(tag);
    return StringUtils.isEmpty(v) ? defaultValue : v;
  }

  public String getAsStringFromObject(Map<String, Object> obj) {
    Object v = obj.get(tag);
    if (v == null) {
      return defaultValue;
    }
    if (v instanceof String) {
      if (StringUtils.isEmpty((String) v)) {
        return defaultValue;
      }
    }
    return v.toString();
  }

}
