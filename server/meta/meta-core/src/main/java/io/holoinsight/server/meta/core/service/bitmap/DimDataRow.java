/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package io.holoinsight.server.meta.core.service.bitmap;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Map;

/**
 * 表示一行维度数据 (供cache使用)
 */
@Getter
@Setter
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class DimDataRow implements Serializable {

  private static final long serialVersionUID = 5919391884128700188L;

  private String dimTable;

  private long id;

  private String uk;

  private Map<String, Object> values;

  private Boolean deleted;

  private long gmtModified;

}
