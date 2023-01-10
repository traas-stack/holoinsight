/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto.meta;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 固定列表
 * 
 * @author jsy1001de
 * @version 1.0: TenantServerDTO.java, v 0.1 2022年03月16日 10:31 上午 jinsong.yjs Exp $
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TenantServerDTO extends BaseMetaDTO {
  private static final long serialVersionUID = -5196084906018697773L;

  /**
   * IP
   */
  public String ip;
  /**
   * HostName
   */
  public String hostname;
}
