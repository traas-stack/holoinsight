/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.core.service.bitmap;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@Data
public class ForeignKey implements Serializable {

  /**
   * 外键所属的table
   */
  private String table;
  /**
   * 外键所属的table 列
   */
  private String column;
  /**
   * 外键引用的table,外键值对应着引用table的primaryKey
   */
  private String refTable;


}
