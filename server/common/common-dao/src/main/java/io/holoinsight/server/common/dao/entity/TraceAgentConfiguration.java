/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

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

  private Date gmtCreate;

  private Date gmtModified;

}
