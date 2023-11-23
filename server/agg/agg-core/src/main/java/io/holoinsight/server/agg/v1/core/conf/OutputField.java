/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import javax.annotation.Nonnull;

import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2023/9/22
 *
 * @author xzchaoo
 */
@Data
public class OutputField {
  @Nonnull
  private String name;
  /**
   * expression for field
   */
  @Nonnull
  private String expression;

  @Getter
  @Setter
  private transient Expression compiledExpression;

  public OutputField() {}

  public OutputField(String name, String expression) {
    this.name = name;
    this.expression = expression;
  }

  public static OutputField create(String name, String expr) {
    return new OutputField(name, expr);
  }

  public Expression getCompiledExpression() {
    if (compiledExpression == null) {
      // TODO compile error?
      compiledExpression = AviatorEvaluator.getInstance().compile(expression, false);
    }
    return compiledExpression;
  }
}
