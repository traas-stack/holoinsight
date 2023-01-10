/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.springboot;

import java.util.Arrays;
import java.util.Map;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

/**
 * <p>
 * created at 2022/11/25
 *
 * @author xzchaoo
 * @see ConditionalOnRole
 */
class ConditionalOnRoleCondition extends SpringBootCondition {
  /** {@inheritDoc} */
  @Override
  public ConditionOutcome getMatchOutcome(ConditionContext context,
      AnnotatedTypeMetadata metadata) {
    Map<String, Object> attributes =
        metadata.getAnnotationAttributes(ConditionalOnRole.class.getName(), true);
    String[] anyRole = (String[]) attributes.get("value");

    String roles = context.getEnvironment().getProperty("holoinsight.roles.active", "");
    Iterable<String> iter = Splitter.on(',').trimResults().omitEmptyStrings().split(roles);

    for (String role : anyRole) {
      if (Iterables.contains(iter, role)) {
        return ConditionOutcome.match("match '" + role + "' role");
      }
    }

    return ConditionOutcome.noMatch("no any roles: " + Arrays.toString(anyRole));
  }
}
