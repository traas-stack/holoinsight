/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: MarketplaceProductDTO.java, v 0.1 2022年10月13日 上午11:32 jinsong.yjs Exp $
 */
@Data
public class MarketplaceProductDTO {
  public Long id;

  public String name;

  public String profile;

  public String overview;

  public String configuration;

  public String price;

  public String feature;

  public Boolean status;

  public String type;

  public String version;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;
}
