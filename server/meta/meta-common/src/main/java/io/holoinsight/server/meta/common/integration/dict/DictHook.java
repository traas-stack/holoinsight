/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.integration.dict;

/**
 *
 * @author jsy1001de
 * @version 1.0: DictHook.java, v 0.1 2022年03月14日 10:25 上午 jinsong.yjs Exp $
 */
public interface DictHook {

  /**
   * dict有变化时触发回调操作
   *
   * @param origin 原始值
   * @param update 更新值
   */
  void update(String origin, String update);
}
