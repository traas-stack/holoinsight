/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: MarketplaceProduct.java, v 0.1 2022年10月13日 上午11:27 jinsong.yjs Exp $
 */
@Data
public class MarketplaceProduct {

  @TableId(type = IdType.AUTO)
  public Long id;

  private String name;

  private String profile;

  private String overview;

  private String configuration;

  private String price;

  public String feature;

  private String type;

  private String version;

  public boolean status;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;
}
