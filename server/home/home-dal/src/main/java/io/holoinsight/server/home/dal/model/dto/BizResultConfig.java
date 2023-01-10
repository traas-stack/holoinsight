/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class BizResultConfig implements Serializable {

  private static final long serialVersionUID = -4474941839733988335L;
  /**
   * 字段名
   */
  private String fieldName;


  /**
   * 操作类型
   */
  private String operateType;


  /**
   * 字段值
   */
  private String fieldValue;


  /**
   * 字段类型
   */
  private String fieldType;

}
