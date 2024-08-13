/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.dao.entity.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: MetricInfoDTO.java, Date: 2023-04-24 Time: 20:19
 */
@Data
public class MetricInfoDTO {
  public Long id;

  public String tenant;

  public String workspace;

  public String organization;

  public String product;

  public String metricType;

  public String metric;

  public String metricTable;

  public String description;

  public String unit;

  public Integer period;
  public String statistic;

  public List<String> tags;

  public boolean deleted;

  public String ref;
  public Map<String, Object> extInfo;

  public String storageTenant;

  public Date gmtCreate;

  public Date gmtModified;
}
