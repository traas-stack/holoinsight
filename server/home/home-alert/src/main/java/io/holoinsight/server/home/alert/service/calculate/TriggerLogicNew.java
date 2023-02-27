/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import io.holoinsight.server.home.facade.emuns.CompareOperationEnum;
import io.holoinsight.server.home.facade.trigger.CompareParam;

import java.io.Serializable;

/**
 * @author wangsiyuan
 * @date 2022/3/10 5:50 下午
 */
public class TriggerLogicNew implements Serializable {

  public static boolean compareValue(CompareParam compareParam, Double value) {
    CompareOperationEnum cmp = compareParam.getCmp();
    Double cmpValue = compareParam.getCmpValue();
    if (cmp == CompareOperationEnum.GT) {
      return value != null && value > cmpValue;
    } else if (cmp == CompareOperationEnum.GTE) {
      return value != null && value >= cmpValue;
    } else if (cmp == CompareOperationEnum.LT) {
      return value != null && value < cmpValue;
    } else if (cmp == CompareOperationEnum.LTE) {
      return value != null && value <= cmpValue;
    } else if (cmp == CompareOperationEnum.EQ) {
      return cmpValue.equals(value);
    } else if (cmp == CompareOperationEnum.NEQ) {
      return !cmpValue.equals(value);
    } else if (cmp == CompareOperationEnum.NULL) {
      return value == null;
    } else {
      return false;
    }
  }
}
