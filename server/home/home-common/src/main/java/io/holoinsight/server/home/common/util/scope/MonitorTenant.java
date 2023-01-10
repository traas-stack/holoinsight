/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorTenant.java, v 0.1 2022年06月01日 10:31 上午 jinsong.yjs Exp $
 */
@Data
@AllArgsConstructor
public class MonitorTenant {
  String code;
  String name;
}
