/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @author jiwliu
 */
@Data
@TableName("flyway_schema_history")
public class FlywaySchemaHistory {
  private Integer installedRank;

  private String version;

  private String description;

  private String type;

  private String script;

  private Integer checksum;

  private String installedBy;

  private Date installedOn;

  private Integer executionTime;

  private Boolean success;

}
