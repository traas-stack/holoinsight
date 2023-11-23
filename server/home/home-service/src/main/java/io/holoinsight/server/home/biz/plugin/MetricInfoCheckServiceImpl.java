/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin;

import io.holoinsight.server.common.dao.entity.dto.MetricInfoDTO;
import io.holoinsight.server.common.service.MetricInfoService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author masaimu
 * @version 2023-09-14 10:45:00
 */
public class MetricInfoCheckServiceImpl implements MetricInfoCheckService {

  @Resource
  private MetricInfoService metricInfoService;

  @Override
  public List<MetricInfoDTO> queryMetricInfoByMetricType(String tenant, String workspace,
      String product) {
    return metricInfoService.queryListByTenantProduct(null, null, product);
  }
}
