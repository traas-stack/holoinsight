/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class DashboardDTO {

  private Boolean overwrite;
  private String message;
  private Long folderId;
  private Map<String, Object> dashboard;
  private Map<String, Object> meta;

  public String tenant;
  public String workspace;
  public String type;
  public String modifier;
}
