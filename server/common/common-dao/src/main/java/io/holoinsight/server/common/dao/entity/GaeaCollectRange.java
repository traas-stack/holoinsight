/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.dao.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author jsy1001de
 * @version 1.0: GaeaCollectRange.java, Date: 2024-06-14 Time: 14:20
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GaeaCollectRange implements Serializable {
  private static final long serialVersionUID = -2140563386879600142L;

  public String type;
  public CloudMonitorRange cloudmonitor;

  public boolean isEqual(GaeaCollectRange originalRecord) {
    if ((originalRecord == null)
        || (this.cloudmonitor == null && originalRecord.cloudmonitor != null)
        || (this.cloudmonitor != null && originalRecord.cloudmonitor == null)) {
      return false;
    }
    return this.cloudmonitor.isEqual(originalRecord.cloudmonitor);
  }
}
