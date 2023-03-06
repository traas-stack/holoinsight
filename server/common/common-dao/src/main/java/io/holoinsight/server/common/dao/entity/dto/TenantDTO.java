/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity.dto;

import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: TenantDTO.java, v 0.1 2022年03月14日 7:53 下午 jinsong.yjs Exp $
 */
@Data
public class TenantDTO {

  public Long id;
  public String name;
  public String code;
  public String json;
  public String desc;
  public String md5;
  public String product;

  public Date gmtCreate;
  public Date gmtModified;
}
