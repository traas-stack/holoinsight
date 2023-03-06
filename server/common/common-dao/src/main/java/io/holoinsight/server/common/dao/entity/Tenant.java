/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: Tenant.java, v 0.1 2022年02月21日 8:37 下午 jinsong.yjs Exp $
 */
@Data
@TableName("tenant")
public class Tenant {
  @TableId(type = IdType.AUTO)
  public Long id;
  public String name;

  public String code;

  @TableField(value = "`desc`")
  public String desc;

  public String md5;

  public String product;

  @TableField(value = "`gmt_create`")
  public Date gmtCreate;

  @TableField(value = "`gmt_modified`")
  public Date gmtModified;

}
