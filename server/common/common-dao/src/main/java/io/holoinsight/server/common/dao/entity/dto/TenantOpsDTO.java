/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 *
 * @author xzchaoo
 * @version 1.0: TenantOps.java, v 0.1 2022年06月21日 3:07 下午 jinsong.yjs Exp $
 */
@Data
public class TenantOpsDTO {
  public Long id;
  public String tenant;
  public TenantOpsStorage storage;

  public Date gmtCreate;
  public Date gmtModified;
}
