/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

import java.util.Date;

/**
 * @author masaimu
 * @version 2024-01-22 17:02:00
 */
@Data
public class AlertTemplateDTO {

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
  public NotificationTemplate templateConfig;
  public String tenant;
  public String workspace;
  public String creator;
  public String modifier;
  public String description;
}
