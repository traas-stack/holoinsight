/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author jsy1001de
 * @version 1.0: Rule.java, v 0.1 2022年03月31日 5:14 下午 jinsong.yjs Exp $
 */
@Data
public class Rule implements Serializable {

  private static final long serialVersionUID = -6789267340963441905L;

  /**
   * 分隔符切分对应的位置
   */
  public Integer pos;

  /**
   * 是否可空
   */
  public Boolean nullable;

  /**
   * 正则表达式对应字段
   */
  public String regexpName;

  /** ------------------左起右至 字段--------------------- */
  /**
   * 左起第几个
   */
  public int leftIndex;

  /**
   * 左起字符串
   */
  public String left;

  /**
   * 右至字符串
   */
  public String right;

  /**
   * 单词翻译转换
   */
  public Translate translate;

  /**
   * 默认值
   */
  public String defaultValue;

  /** ------------------JSON PATH 字段--------------------- */
  /**
   * 表达式
   */
  public String jsonPathSyntax;

  /** ------------------RefName--------------------- */
  /**
   * refName
   */
  public String refName;
}
