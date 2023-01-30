/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author xiangwanpeng
 * @version : IntegrationProductDTO.java, v 0.1 2022年06月08日 16:41 xiangwanpeng Exp $
 */
@Data
public class IntegrationProduct {

  @TableId(type = IdType.AUTO)
  public Long id;

  private String name;

  private String profile;

  private String overview;

  private String configuration;

  private String metrics;

  private String type;

  public String form;

  public String template;

  public boolean status;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;

  public String version;
}
