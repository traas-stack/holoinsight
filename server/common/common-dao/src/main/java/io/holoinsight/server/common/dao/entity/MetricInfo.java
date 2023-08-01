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
 * @version 1.0: MetricInfo.java, Date: 2023-04-24 Time: 20:19
 */
@Data
@TableName("metric_info")
public class MetricInfo {
  @TableId(type = IdType.AUTO)
  public Long id;

  public String tenant;

  public String workspace;

  public String organization;

  public String product;

  public String metricType;

  public String metric;

  public String metricTable;

  public String description;

  public String unit;

  public Integer period;
  public String statistic;

  public String tags;

  public String ref;
  public String extInfo;

  public boolean deleted;

  public String storageTenant;

  @TableField(value = "`gmt_create`")
  public Date gmtCreate;

  @TableField(value = "`gmt_modified`")
  public Date gmtModified;
}
