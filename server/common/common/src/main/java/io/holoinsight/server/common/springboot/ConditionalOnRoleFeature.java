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
 * created at 2022/11/30
 *
 * @author xzchaoo
 */
class ConditionalOnRoleFeature extends SpringBootCondition {
  /** {@inheritDoc} */
  @Override
  public ConditionOutcome getMatchOutcome(ConditionContext context,
      AnnotatedTypeMetadata metadata) {
    Map<String, Object> attributes =
        metadata.getAnnotationAttributes(ConditionalOnFeature.class.getName(), true);
    String[] anyFeature = (String[]) attributes.get("value");

    String features = context.getEnvironment().getProperty("holoinsight.features.active", "");
    Iterable<String> iter = Splitter.on(',').trimResults().omitEmptyStrings().split(features);

    for (String feature : anyFeature) {
      if (Iterables.contains(iter, feature)) {
        return ConditionOutcome.match("match '" + feature + "' feature");
      }
    }

    return ConditionOutcome.noMatch("no any features: " + Arrays.toString(anyFeature));
  }
}
