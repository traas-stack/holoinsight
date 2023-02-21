/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: MarketplacePluginDTO.java, v 0.1 2022年10月13日 上午11:30 jinsong.yjs Exp $
 */
@Data
public class MarketplacePluginDTO {
  public Long id;

  public String tenant;

  public String workspace;

  public String name;

  public String product;

  public String type;

  public String version;

  /**
   * 数据范围
   */
  public String dataRange;

  public String json;

  public Boolean status;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;
}
