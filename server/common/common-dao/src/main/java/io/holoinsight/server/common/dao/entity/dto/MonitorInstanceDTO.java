/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.dao.entity.dto;

import io.holoinsight.server.common.dao.entity.MonitorInstanceCfg;
import lombok.Data;

import java.util.Date;

/**
 * @author jsy1001de
 * @version 1.0: MonitorInstanceDTO.java, Date: 2024-06-03 Time: 11:04
 */
@Data
public class MonitorInstanceDTO {

  private Long id;

  /**
   * 创建时间
   */
  private Date gmtCreate;

  /**
   * 修改时间
   */
  private Date gmtModified;

  /**
   * 实例标识
   */
  private String instance;

  private String instanceName;
  private String instanceType;
  private String instanceInfo;

  /**
   * 实例类型
   */
  private String type;

  /**
   * 租户
   */
  private String tenant;

  /**
   * 租户名称
   */
  private String tenantName;

  /**
   * 工作空间
   */
  private String workspace;

  /**
   * 工作空间名称
   */
  private String workspaceName;

  /**
   * 计量状态
   */
  private int meterState;

  /**
   * 计费状态
   */
  private int billingState;

  /**
   * 扩展配置
   */
  private MonitorInstanceCfg config;

  /**
   * 软删除标记
   */
  private boolean deleted;
}
