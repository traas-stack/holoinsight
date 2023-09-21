/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.crawler;

import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;

import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: TenantMetricCrawlerTaskJobArgs.java, Date: 2023-05-10 Time: 16:14
 */
public class TenantMetricCrawlerTaskJobArgs {

  public String tenant;
  public String workspace;

  public IntegrationProductDTO product;

  public List<MetricInfo> metricInfoList;

  public TenantMetricCrawlerTaskJobArgs(String tenant, String workspace,
      IntegrationProductDTO product, List<MetricInfo> metricInfoList) {
    this.tenant = tenant;
    this.workspace = workspace;
    this.product = product;
    this.metricInfoList = metricInfoList;
  }
}
