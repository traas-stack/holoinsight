/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayTemplateDTO.java, v 0.1 2022年12月06日 上午10:29 jinsong.yjs Exp $
 */
@Data
public class DisplayTemplateDTO {
  public Long id;
  public Long refId;
  public String name;
  public String tenant;
  public String type;
  public Map<String, Object> config;

  public String creator;
  public String modifier;
  public Date gmtCreate;
  public Date gmtModified;
}
