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

import static io.holoinsight.server.home.task.MetricCrawlerConstant.BYTES_UNIT;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_ORGANIZATION;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_TENANT;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_WORKSPACE;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.NUMBER_UNIT;

/**
 * @author jsy1001de
 * @version 1.0: JvmMetricCrawlerBuilder.java, Date: 2023-05-10 Time: 18:14
 */
@Service
@Slf4j
@MetricCrawler(code = "io.holoinsight.plugin.JvmPlugin")
public class JvmMetricCrawlerBuilder extends AbstractMetricCrawlerBuilder {
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

    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "ygc_count", "jvm_ygc_count", "ygc count", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "ygc_time", "jvm_ygc_time", "ygc time", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "fgc_count", "jvm_fgc_count", "fgc count", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "fgc_time", "jvm_fgc_time", "fgc time", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "eden_used", "jvm_eden_used", "eden used", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "eden_capacity", "jvm_eden_capacity", "eden capacity", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "old_used", "jvm_old_used", "old used", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "old_capacity", "jvm_old_capacity", "old capacity", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "meta_used", "jvm_meta_used", "metaspace used", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "meta_capacity", "jvm_meta_capacity", "metaspace capacity", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "perm_used", "jvm_perm_used", "perm used", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "perm_capacity", "jvm_perm_capacity", "perm capacity", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "safepoints", "jvm_safepoints", "safepoints count", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "safepoint_time", "jvm_safepoint_time", "safepoints time", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "safepoint_sync_time", "jvm_safepoint_sync_time", "safepoint sync time", NUMBER_UNIT, 60,
        tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "thread_started", "jvm_thread_started", "线程被启动过的数量", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "thread_live", "jvm_thread_live", "存活的线程数量", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "thread_daemon", "jvm_thread_daemon", "Daemon线程数量", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "jvm",
        "thread_live_peak", "jvm_thread_live_peak", "活的线程的峰值数量", NUMBER_UNIT, 60, tags));

    return metricInfoList;
  }
}
