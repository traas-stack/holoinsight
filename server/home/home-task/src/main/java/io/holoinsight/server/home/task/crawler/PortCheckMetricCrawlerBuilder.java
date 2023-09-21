/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.crawler;

import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.home.task.AbstractMetricCrawlerBuilder;
import io.holoinsight.server.home.task.MetricCrawler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
  protected List<MetricInfo> getMetricInfoList(String metric, List<String> tags,
      MetricInfo metricInfoTemplate) {
    List<MetricInfo> metricInfoList = new ArrayList<>();

    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
        "portcheck", "check", "up", "portcheck_up", "up count", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
        "portcheck", "check", "down", "portcheck_down", "down count", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
        "portcheck", "check", "cost", "portcheck_cost", "cost time(ms)", NUMBER_UNIT, 60, tags));

    return metricInfoList;
  }
}
