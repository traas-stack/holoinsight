/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

/**
 * @author jsy1001de
 * @version 1.0: AlarmMetric.java, Date: 2023-06-08 Time: 21:47
 */
@Data
public class AlarmMetric {
  @TableId(type = IdType.AUTO)
  public Long id;
  public Long ruleId;
  public String ruleType;
  public String metricTable;
  public String tenant;
  public String workspace;
  public String config;

  public boolean deleted;

  @TableField(value = "`gmt_create`")
  public Date gmtCreate;

  @TableField(value = "`gmt_modified`")
  public Date gmtModified;
}
