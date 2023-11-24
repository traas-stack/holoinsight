/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service.bitmap.condition;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 一组复合查询条件,之间是or的关系
 *
 */
@Data
public class MetaCondition implements Serializable {

  private static final long serialVersionUID = 1743255346822672294L;

  private List<OrCondition> orConditions = new ArrayList<>();

  public OrCondition or() {
    OrCondition orCondition = new OrCondition();
    orConditions.add(orCondition);
    return orCondition;
  }

  public void or(OrCondition orCondition) {
    orConditions.add(orCondition);
  }

}
