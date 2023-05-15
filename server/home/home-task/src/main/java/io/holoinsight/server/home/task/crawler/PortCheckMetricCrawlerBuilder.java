/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.crawler;

import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.home.dal.model.dto.IntegrationProductDTO;
import io.holoinsight.server.home.task.AbstractMetricCrawlerBuilder;
import io.holoinsight.server.home.task.MetricCrawler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_ORGANIZATION;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_TENANT;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_WORKSPACE;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.NUMBER_UNIT;

/**
 * @author jsy1001de
 * @version 1.0: PortCheckMetricCrawlerBuilder.java, Date: 2023-05-10 Time: 18:14
 */
@Service
@Slf4j
@MetricCrawler(code = "io.holoinsight.plugin.PortCheckPlugin")
public class PortCheckMetricCrawlerBuilder extends AbstractMetricCrawlerBuilder {
  @Override
  public List<MetricInfo> buildEntity(IntegrationProductDTO integrationProduct) {
    List<MetricInfo> metricInfoList = new ArrayList<>();
    List<MetricInfoModel> metricInfoModel = getMetricInfoModel(integrationProduct.getName());
    if (CollectionUtils.isEmpty(metricInfoModel))
      return metricInfoList;
    List<String> tags = metricInfoModel.get(0).getTags();
    if (!CollectionUtils.isEmpty(metricInfoModel.get(0).getMetricInfoList())) {
      return metricInfoModel.get(0).getMetricInfoList();
    }

    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
        "portcheck", "up", "portcheck_up", "up count", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
        "portcheck", "down", "portcheck_down", "down count", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
        "portcheck", "cost", "portcheck_cost", "cost time(ms)", NUMBER_UNIT, 60, tags));

    return metricInfoList;
  }
}
