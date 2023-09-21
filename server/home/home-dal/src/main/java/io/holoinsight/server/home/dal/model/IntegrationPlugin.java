/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class IntegrationPlugin {

  @TableId(type = IdType.AUTO)
  public Long id;

  public String tenant;

  public String workspace;

  public String name;

  public String product;

  public String type;

  public boolean status;

  public String json;

  public String config;

  public String collectRange;

  public String template;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;

  public String version;
}
