/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.stream;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
@Getter
public class HandshakeContext {
  /**
   * 可以携带任何属性, 注意它不是线程安全的. 一般在握手阶段产生一些元信息放进去之后就是只读的
   */
  private final Map<String, Object> attributes = new HashMap<>();
}
