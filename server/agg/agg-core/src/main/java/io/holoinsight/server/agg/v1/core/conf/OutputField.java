/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

import lombok.AccessLevel;
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
  public static final String DEFAULT = "";
  public static final String PERCENTILE = "PERCENTILE";

  @Nonnull
  private String name;
  /**
   * expression for field
   */
  @Nonnull
  private String expression;

  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  private transient Expression compiledExpression;

  /**
   * Final data type
   * <ul>
   * <li>null or "" or "DEFAULT": data of number and string types are supported, and data of other
   * types will be uniformly converted to string.</li>
   * <li>PERCENTILE: percentile requires the output result to be null, or `Map<String, Double>`
   * where Key is the string format of the percentile, such as "99" "95"</li>
   * </ul>
   */
  private String type = DEFAULT;

  public OutputField() {}

  public OutputField(String name, String expression) {
    this.name = name;
    this.expression = expression;
  }

  public static OutputField create(String name, String expr) {
    return new OutputField(name, expr);
  }

  @JsonIgnore
  public Expression getCompiledExpression() {
    if (compiledExpression == null) {
      // TODO compile error?
      compiledExpression = AviatorEvaluator.getInstance().compile(expression, false);
    }
    return compiledExpression;
  }
}
