/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.interceptor;

import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorScopeAuth.java, v 0.1 2022年03月14日 5:06 下午 jinsong.yjs Exp $
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MonitorScopeAuth {
  AuthTargetType targetType();

  PowerConstants needPower() default PowerConstants.NO_AUTH;
}
