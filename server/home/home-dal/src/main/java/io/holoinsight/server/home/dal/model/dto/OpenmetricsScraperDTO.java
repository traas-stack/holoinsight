/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Date;

@Data
public class OpenmetricsScraperDTO {

  private Long id;
  private String name;
  private String tenant;
  private String workspace;
  private String metricsPath;
  private String schema;
  private String port;
  private String scrapeInterval;
  private String scrapeTimeout;

  private CloudMonitorRange collectRanges;


  /**
   * 创建时间
   */
  private Date gmtCreate;

  /**
   * 修改时间
   */
  private Date gmtModified;

  /**
   * 创建者
   */
  private String creator;

  /**
   * 修改者
   */
  private String modifier;

}
