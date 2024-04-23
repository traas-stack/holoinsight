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
 * @author jsy1001de
 * @version 1.0: MetaDimData.java, Date: 2024-04-18 Time: 11:54
 */
@Data
@TableName("meta_dim_data")
public class MetaDimData {

  @TableId(type = IdType.AUTO)
  public Long id;

  private String tableName;

  private String uk;

  private String json;

  private String annotations;

  private Integer deleted;

  @TableField(value = "`gmt_create`")
  public Date gmtCreate;

  @TableField(value = "`gmt_modified`")
  public Date gmtModified;
}
