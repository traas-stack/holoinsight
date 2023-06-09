/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Where条件, 每个Where实例下只会有一个字段是非null的, 从而构成一个where树. Where的任意一个子条件如果填错(比如正则表达式错误),
 * 那么where'总是丢弃'掉当前匹配的数据
 * <p>
 * created at 2022/3/21
 *
 * @author zzhb101
 */
@ToString
@Getter
@Setter
public class Where {
  private List<Where> and;
  private List<Where> or;
  private Where not;
  private In in;
  private NotIn notIn;
  private Contains contains;
  private ContainsAny containsAny;
  private Regexp regexp;
  private NumberOp numberOp;

  @ToString
  @Getter
  @Setter
  public static class In {
    /**
     * 定义字段如何提取
     */
    private Elect elect;
    /**
     * 提取出的字段必须in这个数组
     */
    private List<String> values;
  }

  @ToString
  @Getter
  @Setter
  public static class NotIn {
    private Elect elect;
    private List<String> values;
  }

  @ToString
  @Getter
  @Setter
  public static class Contains {
    private Elect elect;
    private String value;
  }

  @ToString
  @Getter
  @Setter
  public static class ContainsAny {
    private Elect elect;
    private List<String> values;
  }

  @ToString
  @Getter
  @Setter
  public static class Regexp {
    private Elect elect;
    /**
     * 正则表达式
     */
    private String expression;

    private Boolean catchGroups;
  }

  @ToString
  @Getter
  @Setter
  public static class NumberOp {
    /**
     * 定义字段如何提取
     */
    private Elect elect;
    private Double gt;
    private Double gte;
    private Double lt;
    private Double lte;
  }
}
