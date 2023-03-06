/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: Workspace.java, v 0.1 2023年03月03日 下午5:51 jsy1001de Exp $
 */
@Data
public class Workspace {
  @TableId(type = IdType.AUTO)
  public Long id;

  public String tenant;

  public String name;

  @TableField(value = "`desc`")
  public String desc;

  public String config;

  public Date gmtCreate;

  public Date gmtModified;
}
