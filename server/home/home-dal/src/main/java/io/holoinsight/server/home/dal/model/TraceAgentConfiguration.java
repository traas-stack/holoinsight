/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

@Data
@TableName("trace_agent_configuration")
public class TraceAgentConfiguration {
  private String tenant;
  private String service;
  private String type;
  private String language;
  private String value;
  private String creator;
  private String modifier;

  @Column(name = "gmt_create")
  private Date gmtCreate;

  @Column(name = "gmt_modified")
  private Date gmtModified;

}
