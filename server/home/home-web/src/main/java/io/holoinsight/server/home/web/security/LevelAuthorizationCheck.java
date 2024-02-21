/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.security;

import io.holoinsight.server.home.facade.utils.ParaCheckUtil;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;

/**
 * @author masaimu
 * @version 2023-12-28 19:06:00
 */
public interface LevelAuthorizationCheck {

  public boolean check(LevelAuthorizationMetaData levelAuthMetaData,
      MethodInvocation methodInvocation);

  default boolean checkSqlField(String sqlField) {
    if (StringUtils.isNotEmpty(sqlField)) {
      return ParaCheckUtil.sqlFieldCheck(sqlField);
    }
    return true;
  }

  default boolean checkSqlName(String sqlName) {
    if (StringUtils.isNotEmpty(sqlName)) {
      return ParaCheckUtil.sqlNameCheck(sqlName);
    }
    return true;
  }


}
