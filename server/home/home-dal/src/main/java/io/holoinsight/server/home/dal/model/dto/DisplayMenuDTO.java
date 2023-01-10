/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayMenuDTO.java, v 0.1 2022年12月06日 上午10:48 jinsong.yjs Exp $
 */
@Data
public class DisplayMenuDTO {
  private Long id;
  private Long refId;
  private String tenant;
  private String type;
  private List<DisplayMenuConfig> config;

  private String creator;
  private String modifier;
  private Date gmtCreate;
  private Date gmtModified;
}
