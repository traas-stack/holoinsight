/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

import io.holoinsight.server.registry.core.utils.Dict;
import com.fasterxml.jackson.annotation.JsonRawValue;

/**
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@Getter
@Setter
public class CollectTemplate {
  private long id;
  private String tableName;

  private ExecutorSelector executorSelector;
  private String tenant;
  /**
   * 将采集范围转成dimservice的表达方式
   */
  private CollectRange collectRange;

  private String version;
  private String type;
  @JsonRawValue
  private String json;

  private Date gmtModified;

  public void reuseStrings() {
    tenant = Dict.get(tenant);
    type = Dict.get(type);
    executorSelector.reuseStrings();
    collectRange.reuseStrings();
  }
}
