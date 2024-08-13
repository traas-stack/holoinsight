/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service.bitmap.condition;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 一个查询条件
 *
 * @author xiangwanpeng
 * @version : AndCondition.java, v 0.1 2020年04月20日 15:03 xiangwanpeng Exp $
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AndCondition implements Serializable {

  private static final long serialVersionUID = 6624765328413370360L;

  // 取全量
  private boolean all = false;

  // 取反
  private boolean not = false;

  // regex
  private boolean regex = false;

  // 维度path, 不包含源表名
  private String[] express;

  // 合法值集合
  private List<Object> valueRange;
}
