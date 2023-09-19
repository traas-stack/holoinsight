/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin;

import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;

import java.util.Collections;
import java.util.List;

/**
 * @author masaimu
 * @version 2023-09-14 10:45:00
 */
public class MetricInfoCheckServiceImpl implements MetricInfoCheckService {
  @Override
  public boolean needWorkspace(String product) {
    return false;
  }

  @Override
  public List<MetricInfoDTO> queryMetricInfoByMetricType(String product) {
    return Collections.emptyList();
  }
}
