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
 * @version 1.0: IntegrationGenerated.java, v 0.1 2022年12月14日 上午11:43 jinsong.yjs Exp $
 */
@Data
public class IntegrationGenerated {
  @TableId(type = IdType.AUTO)
  public Long id;

  public String tenant;

  public String workspace;

  public String name;

  public String product;

  public String item;

  public String config;

  public boolean deleted;

  public boolean custom;

  public String creator;

  public String modifier;

  public Date gmtCreate;

  public Date gmtModified;
}
