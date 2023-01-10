/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
@TableName("agent_configuration")
public class AgentConfiguration {
  private String tenant;

  private String service;

  @Column(name = "app_id")
  private String appId;

  @Column(name = "env_id")
  private String envId;

  private String value;

  @Column(name = "gmt_create")
  private Date gmtCreate;

  @Column(name = "gmt_modified")
  private Date gmtModified;

}
