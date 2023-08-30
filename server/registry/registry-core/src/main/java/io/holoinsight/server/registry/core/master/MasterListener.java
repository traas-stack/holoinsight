/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.master;

/**
 * <p>
 * created at 2022/3/12
 *
 * @author zzhb101
 */
public interface MasterListener {
  void onChange(MasterJson oldMj, MasterJson newMj);

  void onEnter(MasterJson mj);

  void onLeave(MasterJson mj);
}
