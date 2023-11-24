/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service.bitmap.condition;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 一组复合查询条件, 之间是and的关系
 *
 * @author xiangwanpeng
 * @version : OredCondition.java, v 0.1 2020年04月20日 15:03 xiangwanpeng Exp $
 */
@Data
public class OrCondition implements Serializable {

  private static final long serialVersionUID = 8065876283511437436L;

  private List<AndCondition> andConditions = new ArrayList<>();

  public AndCondition and() {
    AndCondition andCondition = new AndCondition();
    andConditions.add(andCondition);
    return andCondition;
  }

  public void and(AndCondition andCondition) {
    andConditions.add(andCondition);
  }
}
