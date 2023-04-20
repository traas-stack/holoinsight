/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.bootstrap;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import io.holoinsight.server.test.it.AlertCalculateIT;
import io.holoinsight.server.test.it.AlertDingDingRobotIT;
import io.holoinsight.server.test.it.AlertGroupIT;
import io.holoinsight.server.test.it.AlertRuleIT;
import io.holoinsight.server.test.it.AlertWebhookIT;
import io.holoinsight.server.test.it.CeresdbPqlMonitoringIT;
import io.holoinsight.server.test.it.DashboardIT;
import io.holoinsight.server.test.it.IntegrationPluginIT;
import io.holoinsight.server.test.it.MetricMonitoringIT;
import io.holoinsight.server.test.it.TaskIT;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

import io.holoinsight.server.test.it.AgentVMIT;
import io.holoinsight.server.test.it.AppMonitoringIT;
import io.holoinsight.server.test.it.AuthIT;
import io.holoinsight.server.test.it.LogMonitoringFolderIT;
import io.holoinsight.server.test.it.LogMonitoringIT;
import io.holoinsight.server.test.it.LogMonitoring_1_log_IT;
import io.holoinsight.server.test.it.MetaVMIT;
import io.holoinsight.server.test.it.OpenMetricsScraperIT;
import io.holoinsight.server.test.it.apm.ApmAppMetricStatIT;
import io.holoinsight.server.test.it.apm.ApmCallLinkDetailIT;
import io.holoinsight.server.test.it.apm.ApmCallLinkIT;
import io.holoinsight.server.test.it.apm.ApmTopologyIT;

/**
 * <p>
 * created at 2023/3/18
 *
 * @author xzchaoo
 */
public class ITSets {
  /**
   * Select all ITs.
   *
   * @return
   */
  protected static LauncherDiscoveryRequest allITs() {
    return LauncherDiscoveryRequestBuilder.request() //
        .selectors(selectClass(AuthIT.class)) //
        .selectors(selectClass(LogMonitoringFolderIT.class)) //
        .selectors(selectClass(MetaVMIT.class)) //
        .selectors(selectClass(AppMonitoringIT.class)) //
        .selectors(selectClass(AgentVMIT.class)) //
        .selectors(selectClass(LogMonitoringIT.class)) //
        .selectors(selectClass(AlertRuleIT.class)) //
        .selectors(selectClass(AlertGroupIT.class)) //
        .selectors(selectClass(AlertCalculateIT.class)) //
        .selectors(selectClass(IntegrationPluginIT.class)) //
        .selectors(selectClass(OpenMetricsScraperIT.class)) //
        .selectors(selectClass(AlertWebhookIT.class)) //
        .selectors(selectClass(DashboardIT.class)) //
        .selectors(selectClass(AlertDingDingRobotIT.class)) //
        .selectors(selectClass(LogMonitoring_1_log_IT.class)) //
        .selectors(selectClass(CeresdbPqlMonitoringIT.class)) //
        .selectors(selectClass(MetricMonitoringIT.class)) //
        .selectors(selectClass(ApmCallLinkIT.class)) //
        .selectors(selectClass(ApmTopologyIT.class)) //
        .selectors(selectClass(ApmCallLinkDetailIT.class)) //
        .selectors(selectClass(ApmAppMetricStatIT.class)) //
        .selectors(selectClass(TaskIT.class)) //
        .build(); //
  }
}
