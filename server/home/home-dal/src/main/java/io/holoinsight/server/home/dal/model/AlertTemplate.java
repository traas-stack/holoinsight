/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author masaimu
 * @version 2024-01-22 16:48:00
 */
@Data
@Table(name = "alert_template")
public class AlertTemplate {

  @TableId(type = IdType.INPUT)
  public String uuid;

  /**
   * 创建时间
   */
  @Column(name = "gmt_create")
  public Date gmtCreate;

  /**
   * 修改时间
   */
  @Column(name = "gmt_modified")
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
