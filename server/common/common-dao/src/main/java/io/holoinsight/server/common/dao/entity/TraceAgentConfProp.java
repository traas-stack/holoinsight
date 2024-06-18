/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("trace_agent_configuration_properties")
public class TraceAgentConfProp {
  private String type;
  private String language;
  private String propKey;
  private String name;
  private String cName;
  private String description;
  private String cDescription;
  private String checkExpression;
}
