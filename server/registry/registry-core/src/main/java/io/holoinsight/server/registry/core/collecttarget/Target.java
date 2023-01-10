/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.collecttarget;

import java.util.Map;

import lombok.Data;

/**
 * 一个采集目标
 * <p>
 * created at 2022/3/2
 *
 * @author zzhb101
 */
@Data
public class Target {
  @Deprecated
  public static final int DIM = 1;
  public static final int DIM2 = 2;
  public static final int CENTRAL = 3;
  /**
   * TODO 1. 此处有必要使用字符串吗? 为了通用, 比如某些数据库可能用的类似uuid TODO 2. 一定要加上 "${type}:" 的前缀吗?
   * 其实只要保证不同类型同时使用不会冲突就行 上述2个考虑是为了防止内存里的字符串太多
   * <p>
   * 一个采集目标可以用一个id来唯一索引, id具有 "${type}:${id2}" 格式 例如 "dim:123"
   */
  private String id;
  /**
   * 为了节省内存 在内部type使用枚举, 但出了reg它应该是一个字符串
   */
  private int type;

  /**
   * 底层的维度实现
   */
  private Object inner;
  private long version;
  private long gmtModified;

  public static Target of2(Map<String, Object> dim2) {
    Target t = new Target();
    t.type = DIM2;
    t.id = "dim2:" + dim2.get("_uk");
    t.inner = dim2;
    // TODO
    // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    // t.gmtModified = sdf.parse((String) dim2.get("_gmtModified")).getTime();
    // t.version = t.gmtModified;
    return t;
  }

  public static Target ofCentral(String tenant) {
    Target t = new Target();
    t.type = CENTRAL;
    t.id = "CENTRAL:" + tenant;
    return t;
  }

  public <T> T unwrap() {
    return (T) inner;
  }
}
