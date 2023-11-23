/**
 * Alipay.com Inc. Copyright (c) 2004-2020 All Rights Reserved.
 */
package io.holoinsight.server.meta.core.service.bitmap.condition;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 一组复合查询条件,之间是or的关系
 *
 * @author wanpeng.xwp
 * @version : DimRangeCondition.java, v 0.1 2020年04月20日 14:53 wanpeng.xwp Exp $
 */
@Data
public class DimCondition implements Serializable {

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
