/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.storage.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jiwliu
 * @version : ModelDefine.java, v 0.1 2022年10月11日 20:18 xiangwanpeng Exp $
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelAnnotation {
  String name();

  /**
   * unit: day
   * 
   * @return
   */
  long ttl() default 0;
}
