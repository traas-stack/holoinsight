/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.persistence.Column;

@Data
@TableName("trace_agent_configuration_properties")
public class TraceAgentConfProp {
  private String type;
  private String language;
  @Column(name = "prop_key")
  private String propKey;
  private String name;
  @Column(name = "c_name")
  private String cName;
  private String description;
  @Column(name = "c_description")
  private String cDescription;
  @Column(name = "check_expression")
  private String checkExpression;


}
