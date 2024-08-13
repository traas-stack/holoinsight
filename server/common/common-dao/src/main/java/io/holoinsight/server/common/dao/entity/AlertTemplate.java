/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author masaimu
 * @version 2024-01-22 16:48:00
 */
@Data
@TableName("alert_template")
public class AlertTemplate {

  @TableId(type = IdType.INPUT)
  public String uuid;

  /**
   * 创建时间
   */
  public Date gmtCreate;

  /**
   * 修改时间
   */
  public Date gmtModified;

  public String templateName;
  public String sceneType;
  public boolean beDefault;
  public String channelType;
  public String templateConfig;
  public String tenant;
  public String workspace;
  public String creator;
  public String modifier;
  public String description;

}
