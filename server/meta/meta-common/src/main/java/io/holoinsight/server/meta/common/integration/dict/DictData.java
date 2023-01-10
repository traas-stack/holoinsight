/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.integration.dict;

import java.io.Serializable;

/**
 *
 * @author jsy1001de
 * @version 1.0: DictData.java, v 0.1 2022年03月14日 10:25 上午 jinsong.yjs Exp $
 */
public class DictData implements Serializable {

  /**
   * 数据优先级，取决于不同的dictLoader
   */
  private int priority;

  /**
   * 一级隔离空间
   */
  private String domain;

  /**
   * 二级隔离空间
   */
  private String subDomain;

  /**
   * 字典key
   */
  private String dictKey;

  /**
   * 描述该配置的作用
   */
  private String dictKeyDesc;

  /**
   * 该配置的值
   */
  private String dictValue;

  public DictData(int priority, String domain, String subDomain, String dictKey, String dictKeyDesc,
      String dictValue) {
    this.priority = priority;
    this.domain = domain;
    this.subDomain = subDomain;
    this.dictKey = dictKey;
    this.dictKeyDesc = dictKeyDesc;
    this.dictValue = dictValue;
  }

  public DictData(String domain, String subDomain, String dictKey, String dictKeyDesc,
      String dictValue) {
    this.domain = domain;
    this.subDomain = subDomain;
    this.dictKey = dictKey;
    this.dictKeyDesc = dictKeyDesc;
    this.dictValue = dictValue;
  }

  /**
   * Getter for property 'priority'.
   *
   * @return Value for property 'priority'.
   */
  public int getPriority() {
    return priority;
  }

  /**
   * Getter for property 'domain'.
   *
   * @return Value for property 'domain'.
   */
  public String getDomain() {
    return domain;
  }

  /**
   * Getter for property 'subDomain'.
   *
   * @return Value for property 'subDomain'.
   */
  public String getSubDomain() {
    return subDomain;
  }

  /**
   * Getter for property 'dictKey'.
   *
   * @return Value for property 'dictKey'.
   */
  public String getDictKey() {
    return dictKey;
  }

  /**
   * Getter for property 'dictKeyDesc'.
   *
   * @return Value for property 'dictKeyDesc'.
   */
  public String getDictKeyDesc() {
    return dictKeyDesc;
  }

  /**
   * Getter for property 'dictValue'.
   *
   * @return Value for property 'dictValue'.
   */
  public String getDictValue() {
    return dictValue;
  }

  /**
   * Setter for property 'priority'.
   *
   * @param priority Value to set for property 'priority'.
   */
  public void setPriority(int priority) {
    this.priority = priority;
  }
}
