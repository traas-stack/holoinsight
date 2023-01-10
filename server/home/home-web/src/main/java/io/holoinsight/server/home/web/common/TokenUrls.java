/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 对外api url，通过token权限进行校验
 * 
 * @author jsy1001de
 * @version 1.0: TokenScope.java, v 0.1 2020年07月23日 5:42 下午 jinsong.yjs Exp $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TokenUrls {
  String[] value();
}
