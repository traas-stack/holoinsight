/*
 * Alipay.com Inc. Copyright (c) 2004-2018 All Rights Reserved.
 */

package io.holoinsight.server.home.facade.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author masaimu
 * @version 2023-12-06 17:10:00
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UpdateCheck {
  CheckCategory[] value();
}
