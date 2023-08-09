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
import java.util.Arrays;
import java.util.List;

import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_ORGANIZATION;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_TENANT;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.GLOBAL_WORKSPACE;
import static io.holoinsight.server.home.task.MetricCrawlerConstant.NUMBER_UNIT;

/**
 * @author masaimu
 * @version 2023-08-07 20:02:00
 */
@Service
@Slf4j
@MetricCrawler(code = "io.holoinsight.plugin.PgPlugin")
public class PgMetricCrawlerBuilder extends AbstractMetricCrawlerBuilder {
  @Override
  protected List<MetricInfo> getMetricInfoList(String metricType, List<String> tags,
      MetricInfo metricInfoTemplate) {
    List<MetricInfo> metricInfoList = new ArrayList<>();

    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Connections", "stat_activity_count", "pg_stat_activity_count", "pg stat activity count",
        NUMBER_UNIT, 60,
        Arrays.asList("cluster", "datname", "instance", "job", "role", "state", "usename", "ip")));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Connections", "settings_max_connections", "pg_settings_max_connections",
        "pg settings max connections", NUMBER_UNIT, 60, tags));
    metricInfoList.add(
        genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg", "Connections",
            "connection_stats_max_idle_in_txn_time", "ccp_connection_stats_max_idle_in_txn_time",
            "pg connection stats max idle in txn time", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Connections", "transaction_wraparound_percent_towards_wraparound",
        "ccp_transaction_wraparound_percent_towards_wraparound",
        "transaction wraparound percent towards wraparound", NUMBER_UNIT, 60, tags));

    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Cache_Hit_Ratio", "stat_database_blks_hit", "ccp_stat_database_blks_hit",
        "stat_database_blks_hit", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Cache_Hit_Ratio", "stat_database_blks_read", "ccp_stat_database_blks_read",
        "stat_database_blks_read", NUMBER_UNIT, 60, tags));

    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Transactions", "stat_database_xact_commit", "ccp_stat_database_xact_commit",
        "stat_database_xact_commit", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Transactions", "stat_database_xact_rollback", "ccp_stat_database_xact_rollback",
        "stat_database_xact_rollback", NUMBER_UNIT, 60, tags));

    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "QPS", "pg_stat_statements_total_calls_count", "ccp_pg_stat_statements_total_calls_count",
        "pg_stat_statements_total_calls_count", NUMBER_UNIT, 60, tags));

    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "database_size", "database_size_bytes", "ccp_database_size_bytes", "database_size_bytes",
        NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Query_Duration", "pg_stat_statements_total_mean_exec_time_ms",
        "ccp_pg_stat_statements_total_mean_exec_time_ms",
        "pg_stat_statements_total_mean_exec_time_ms", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Query_Duration", "pg_stat_statements_top_max_exec_time_ms",
        "ccp_pg_stat_statements_top_max_exec_time_ms", "pg_stat_statements_top_max_exec_time_ms",
        NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Row_activity", "stat_database_tup_fetched", "ccp_stat_database_tup_fetched",
        "stat_database_tup_fetched", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Row_activity", "stat_database_tup_inserted", "ccp_stat_database_tup_inserted",
        "stat_database_tup_inserted", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Row_activity", "stat_database_tup_updated", "ccp_stat_database_tup_updated",
        "stat_database_tup_updated", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Row_activity", "stat_database_tup_deleted", "ccp_stat_database_tup_deleted",
        "stat_database_tup_deleted", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Row_activity", "stat_database_tup_returned", "ccp_stat_database_tup_returned",
        "stat_database_tup_returned", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "WAL", "wal_activity_total_size_bytes", "ccp_wal_activity_total_size_bytes",
        "wal_activity_total_size_bytes", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Locks", "stat_database_deadlocks", "ccp_stat_database_deadlocks",
        "stat_database_deadlocks", NUMBER_UNIT, 60, tags));

    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Locks", "stat_database_conflicts", "ccp_stat_database_conflicts",
        "stat_database_conflicts", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Transactions", "stat_database_xact_commit", "ccp_stat_database_xact_commit",
        "stat_database_xact_commit", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Transactions", "stat_database_xact_rollback", "ccp_stat_database_xact_rollback",
        "stat_database_xact_rollback", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "CPU", "nodemx_cpuacct_usage", "ccp_nodemx_cpuacct_usage", "nodemx_cpuacct_usage",
        NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "CPU", "nodemx_cpuacct_usage_ts", "ccp_nodemx_cpuacct_usage_ts", "nodemx_cpuacct_usage_ts",
        NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "CPU", "nodemx_cpucfs_quota_us", "ccp_nodemx_cpucfs_quota_us", "nodemx_cpucfs_quota_us",
        NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "CPU", "nodemx_cpucfs_period_us", "ccp_nodemx_cpucfs_period_us", "nodemx_cpucfs_period_us",
        NUMBER_UNIT, 60, tags));

    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "CPU", "nodemx_process_count", "ccp_nodemx_process_count", "nodemx_process_count",
        NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "CPU", "nodemx_cpustat_throttled_time", "ccp_nodemx_cpustat_throttled_time",
        "nodemx_cpustat_throttled_time", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "CPU", "nodemx_cpustat_snap_ts", "ccp_nodemx_cpustat_snap_ts", "nodemx_cpustat_snap_ts",
        NUMBER_UNIT, 60, tags));
    metricInfoList
        .add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg", "MEM",
            "nodemx_mem_limit", "ccp_nodemx_mem_limit", "nodemx_mem_limit", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "MEM", "nodemx_mem_usage_in_bytes", "ccp_nodemx_mem_usage_in_bytes",
        "nodemx_mem_usage_in_bytes", NUMBER_UNIT, 60, tags));
    metricInfoList
        .add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg", "MEM",
            "nodemx_mem_limit", "ccp_nodemx_mem_limit", "nodemx_mem_limit", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "MEM", "nodemx_mem_usage_in_bytes", "ccp_nodemx_mem_usage_in_bytes",
        "nodemx_mem_usage_in_bytes", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Network", "nodemx_network_rx_bytes", "ccp_nodemx_network_rx_bytes",
        "nodemx_network_rx_bytes", NUMBER_UNIT, 60, tags));
    metricInfoList.add(genMetricInfo(GLOBAL_TENANT, GLOBAL_WORKSPACE, GLOBAL_ORGANIZATION, "pg",
        "Network", "nodemx_network_tx_bytes", "ccp_nodemx_network_tx_bytes",
        "nodemx_network_tx_bytes", NUMBER_UNIT, 60, tags));


    return metricInfoList;
  }
}
