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

import static io.holoinsight.server.home.task.MetricCrawlerConstant.*;

/**
 * @author jsy1001de
 * @version : SystemMetricCrawlerBuilder.java, Date: 2023-05-10 Time: 16:53
 */
@Service
@Slf4j
@MetricCrawler(code = "io.holoinsight.plugin.SystemPlugin")
public class SystemMetricCrawlerBuilder extends AbstractMetricCrawlerBuilder {

  @Override
  protected List<MetricInfo> getMetricInfoList(String metric, List<String> tags,
      MetricInfo metricInfoTemplate) {
    List<MetricInfo> metricInfoList = new ArrayList<>();
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "CPU", "cpu_util", "k8s_pod_cpu_util", "cpu 使用率", PERCENT_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "CPU", "cpu_sys", "k8s_pod_cpu_sys", "cpu sys 使用率", PERCENT_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "CPU", "cpu_user", "k8s_pod_cpu_user", "cpu user 使用率", PERCENT_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "CPU", "cpu_inuse_cores", "k8s_pod_cpu_inuse_cores", "cpu 使用核数", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "CPU", "cpu_total_cores", "k8s_pod_cpu_total_cores", "cpu 总核数", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "MEMORY", "mem_util", "k8s_pod_mem_util", "mem 使用率", PERCENT_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "MEMORY", "mem_used", "k8s_pod_mem_used", "mem 使用量", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "MEMORY", "mem_total", "k8s_pod_mem_total", "mem 总量", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "MEMORY", "mem_rss", "k8s_pod_mem_rss", "mem rss量", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "MEMORY", "mem_cache", "k8s_pod_mem_cache", "mem cache量", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "LOAD", "load_load1", "k8s_pod_load_load1", "load1量", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "LOAD", "load_load5", "k8s_pod_load_load5", "load5量", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "LOAD", "load_load15", "k8s_pod_load_load15", "load15量", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "OOM", "oom", "k8s_pod_oom", "oom次数", NUMBER_UNIT, 60, tags));
    metricInfoList.add(
        genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system", "TRAFFIC",
            "traffic_bytin", "k8s_pod_traffic_bytin", "物理主机物理网卡rx方向的流量大小量", BYTES_UNIT, 60, tags));
    metricInfoList.add(
        genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system", "TRAFFIC",
            "traffic_bytout", "k8s_pod_traffic_bytout", "物理主机物理网卡tx方向的流量大小", BYTES_UNIT, 60, tags));
    metricInfoList
        .add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system", "TCP",
            "tcp_established", "k8s_pod_tcp_established", "tcp 连接被重置的每秒次数", BYTES_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system",
        "OOM", "oom", "k8s_pod_oom", "oom 数量", NUMBER_UNIT, 60, tags));

    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "CPU", "cpu_util", "k8s_pod_cpu_util_5s", "cpu 使用率", PERCENT_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "CPU", "cpu_sys_5s", "k8s_pod_cpu_sys_5s", "cpu sys 使用率", PERCENT_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "CPU", "cpu_user_5s", "k8s_pod_cpu_user_5s", "cpu user 使用率", PERCENT_UNIT, 5, tags));
    // metricInfoList
    // .add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system", "CPU",
    // "cpu_inuse_cores_5s", "k8s_pod_cpu_inuse_cores_5s", "cpu 使用核数", NUMBER_UNIT, 5, tags));
    // metricInfoList
    // .add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "system", "CPU",
    // "cpu_total_cores_5s", "k8s_pod_cpu_total_cores_5s", "cpu 总核数", NUMBER_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "MEMORY", "mem_uti_5s", "k8s_pod_mem_util_5s", "mem 使用率", PERCENT_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "MEMORY", "mem_used_5s", "k8s_pod_mem_used_5s", "mem 使用量", BYTES_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "MEMORY", "mem_total_5s", "k8s_pod_mem_total_5s", "mem 总量", BYTES_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "MEMORY", "mem_rss_5s", "k8s_pod_mem_rss_5s", "mem rss量", BYTES_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "MEMORY", "mem_cache_5s", "k8s_pod_mem_cache_5s", "mem cache量", BYTES_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "LOAD", "load_load1_5s", "k8s_pod_load_load1_5s", "load1量", NUMBER_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "LOAD", "load_load5_5s", "k8s_pod_load_load5_5s", "load5量", NUMBER_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "LOAD", "load_load15_5s", "k8s_pod_load_load15_5s", "load15量", NUMBER_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "OOM", "oom", "k8s_pod_oom_5s", "oom次数", NUMBER_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "TRAFFIC", "traffic_bytin_5s", "k8s_pod_traffic_bytin_5s", "物理主机物理网卡rx方向的流量大小量", BYTES_UNIT,
    // 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "TRAFFIC", "traffic_bytout_5s", "k8s_pod_traffic_bytout_5s", "物理主机物理网卡tx方向的流量大小",
    // BYTES_UNIT, 5, tags));
    // metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION,
    // "system",
    // "TCP", "tcp_established_5s", "k8s_pod_tcp_established_5s", "tcp 连接被重置的每秒次数", BYTES_UNIT, 5,
    // tags));

    return metricInfoList;
  }
}
