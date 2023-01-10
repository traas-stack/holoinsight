/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.access.model;

import lombok.ToString;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorTokenData.java, v 0.1 2022年06月10日 4:25 下午 jinsong.yjs Exp $
 */
@ToString
public class MonitorTokenData {
  public String accessId;
  public String accessKey;
  public String tenant;
  public long time;

  public MonitorTokenData() {}

  public MonitorTokenData setAccessId(String accessId) {
    this.accessId = accessId;
    return this;
  }

  public MonitorTokenData setTime(long time) {
    this.time = time;
    return this;
  }

  public MonitorTokenData setTenant(String tenant) {
    this.tenant = tenant;
    return this;
  }

  public MonitorTokenData setAccessKey(String accessKey) {
    this.accessKey = accessKey;
    return this;
  }
}
