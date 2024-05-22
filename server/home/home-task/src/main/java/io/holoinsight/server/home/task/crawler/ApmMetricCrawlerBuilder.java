/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.task.crawler;

import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.home.task.AbstractMetricCrawlerBuilder;
import io.holoinsight.server.home.task.MetricCrawler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.holoinsight.server.common.model.MetricCrawlerConstant.GLOBAL_ORGANIZATION;
import static io.holoinsight.server.common.model.MetricCrawlerConstant.GLOBAL_TENANT;
import static io.holoinsight.server.common.model.MetricCrawlerConstant.GLOBAL_WORKSPACE;
import static io.holoinsight.server.common.model.MetricCrawlerConstant.MS_UNIT;
import static io.holoinsight.server.common.model.MetricCrawlerConstant.NUMBER_UNIT;

/**
 * @author jsy1001de
 * @version 1.0: ApmMetricCrawlerBuilder.java, Date: 2023-05-10 Time: 18:14
 */
@Service
@Slf4j
@MetricCrawler(code = "io.holoinsight.plugin.ApmPlugin")
public class ApmMetricCrawlerBuilder extends AbstractMetricCrawlerBuilder {

  @Override
  protected List<MetricInfo> getMetricInfoList(String metric, List<String> tags,
      MetricInfo metricInfoTemplate) {
    List<MetricInfo> metricInfoList = new ArrayList<>();
    List<String> newTags;
    switch (metric) {
      case "Endpoint":
        newTags =
            CollectionUtils.isEmpty(tags) ? Arrays.asList("endpointName", "serviceName") : tags;
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Endpoint", "endpoint_cpm", "apm_endpoint_cpm", "endpoint calls per minute",
            NUMBER_UNIT, 60, newTags));
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Endpoint", "endpoint_cpm_fail", "apm_endpoint_cpm_fail",
            "endpoint calls fail per minute", NUMBER_UNIT, 60, newTags));
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Endpoint", "endpoint_resp_time", "apm_endpoint_resp_time",
            "endpoint response time per minute(ms)", MS_UNIT, 60, newTags));
        break;

      case "Service":
        newTags = CollectionUtils.isEmpty(tags) ? Collections.singletonList("serviceName") : tags;
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Service", "service_cpm", "apm_service_cpm", "service calls per minute",
            NUMBER_UNIT, 60, newTags));
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Service", "service_cpm_fail", "apm_service_cpm_fail",
            "service calls fail per minute", NUMBER_UNIT, 60, newTags));
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Service", "service_resp_time", "apm_service_resp_time",
            "service response time per minute(ms)", MS_UNIT, 60, newTags));
        break;

      case "Instance":
        newTags =
            CollectionUtils.isEmpty(tags) ? Arrays.asList("serviceName", "serviceInstanceName")
                : tags;
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Instance", "service_instance_cpm", "apm_service_instance_cpm",
            "service instance calls per minute", NUMBER_UNIT, 60, newTags));
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Instance", "service_instance_cpm_fail", "apm_service_instance_cpm_fail",
            "service instance calls fail per minute", NUMBER_UNIT, 60, newTags));
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Instance", "service_instance_resp_time", "apm_service_instance_resp_time",
            "service instance response time per minute(ms)", MS_UNIT, 60, newTags));
        break;

      case "Component":
        newTags = CollectionUtils.isEmpty(tags)
            ? Arrays.asList("source_service_name", "dest_service_name", "component", "type")
            : tags;
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Component", "component_cpm", "apm_component_cpm", "call component per minute",
            NUMBER_UNIT, 60, newTags));
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Component", "component_cpm_fail", "apm_component_cpm_fail",
            "call component fail per minute", NUMBER_UNIT, 60, newTags));
        metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
            "apm", "Component", "component_resp_time", "apm_component_resp_time",
            "call component response time per minute(ms)", MS_UNIT, 60, newTags));
        break;
    }
    return metricInfoList;
  }
}
