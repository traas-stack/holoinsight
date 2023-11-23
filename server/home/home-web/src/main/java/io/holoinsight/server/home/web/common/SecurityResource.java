/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */
package io.holoinsight.server.home.web.common;

import io.holoinsight.server.home.facade.utils.SecurityMethodCategory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author masaimu
 * @version 2023-11-22 16:33:00
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SecurityResource {
  // create, update, select, delete
  SecurityMethodCategory value();

  String mapper();
}
