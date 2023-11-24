/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.core.service.bitmap;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * 表示一行维度数据 (供cache使用)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class MetaDataRow implements Serializable {

  private static final long serialVersionUID = 5919391884128700188L;

  private String tableName;

  private long id;

  private String uk;

  private Map<String, Object> values;

  private Boolean deleted;

  private long gmtModified;

}
