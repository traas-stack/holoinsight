/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.springboot;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Conditional;

/**
 * {@link ConditionalOnRole} that checks if the specified holoinsight role is activated. You can set
 * property 'holoinsight.roles.active=registry,meta' to activate 'registry' and 'meta' role.
 * <p>
 * created at 2022/11/25
 *
 * @author xzchaoo
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Conditional(ConditionalOnRoleCondition.class)
public @interface ConditionalOnRole {
  /**
   * Holoinsight role
   *
   * @return
   */
  String[] value();
}
