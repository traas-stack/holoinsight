/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 定义一个'字段'如何提取
 * <p>
 * created at 2022/3/21
 *
 * @author zzhb101
 */
@ToString
@Getter
@Setter
public class Elect {
  /**
   * 提取方式, leftRight/refName/refIndex/refMeta
   */
  private String type;
  private LeftRight leftRight;
  private RefName refName;
  private RefIndex refIndex;
  private RefMeta refMeta;

  /**
   * 对提取到的值做转换
   */
  private Transform transform;
  /**
   * 默认值, 如果提取不出字段, 或者转换结果为null时, 默认值就会生效
   */
  private String defaultValue;

  /**
   * 左起右至的提取方式
   */
  @ToString
  @Getter
  @Setter
  public static class LeftRight {
    private int leftIndex;
    /**
     * 如果为空则表示从行头开始配
     */
    private String left;
    /**
     * 如果为空则表示匹配到行尾
     */
    private String right;
  }

  /**
   * '引用一个已经通过其他方式定义的字段'的提取方式
   */
  @ToString
  @Getter
  @Setter
  public static class RefName {
    /**
     * 按名字引用
     */
    private String name;
  }

  @ToString
  @Getter
  @Setter
  public static class RefIndex {
    /**
     * 按名字引用
     */
    private int index;
  }

  /**
   * 引用本机元数据
   */
  @ToString
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  public static class RefMeta {
    /**
     * ip/host/app
     */
    private String name;
  }

  @ToString
  @Getter
  @Setter
  public static class Transform {
    private List<TransformItem> transforms;
  }


  @ToString
  @Getter
  @Setter
  public static class TransformItem {
    /**
     * 转换函数名称, 可以参考现有lego
     */
    private String func;
    /**
     * arg的内容要根据func来解释
     */
    private String arg;
  }

}
