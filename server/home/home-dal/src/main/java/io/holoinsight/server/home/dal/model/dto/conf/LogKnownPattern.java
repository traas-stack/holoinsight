/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.dal.model.dto.conf;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author jsy1001de
 * @version 1.0: LogPattern.java, Date: 2023-04-17 Time: 12:20
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LogKnownPattern extends Filter {

  // pattern name
  public String eventName;
  // max Count of log Snapshots apply to all known&unknown patterns stat
  public Integer maxSnapshots = 3;
}
