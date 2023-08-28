/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.event;

import java.util.EventObject;

/**
 * <p>
 * created at 2023/8/23
 *
 * @author xzchaoo
 */
public class ModuleInitEvent extends EventObject {
  public ModuleInitEvent(Object source) {
    super(source);
  }
}
