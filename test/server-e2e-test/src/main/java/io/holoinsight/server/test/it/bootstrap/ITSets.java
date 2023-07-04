/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it.bootstrap;

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

import io.holoinsight.server.test.it.*;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;

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
        .selectors(selectClass(LogMonitoringMultilineIT.class)) //
        .selectors(selectClass(LogMonitoringAnalysisIT.class)) //
        .selectors(selectClass(AlertLogAnalysisIT.class)) //
        .selectors(selectClass(CustomPluginFacadeIT.class)) //
        .selectors(selectClass(FolderFacadeIT.class)) //
        .selectors(selectClass(AlertTemplateFacadeIT.class)) //
        .selectors(selectClass(AlarmBlockFacadeIT.class)) //
        .selectors(selectClass(AlarmMetricFacadeIT.class)) //
        .selectors(selectClass(AlarmSubscribeFacadeIT.class)) //
        .selectors(selectClass(AlertManagerWebhookIT.class)) //
        .selectors(selectClass(ApiKeyFacadeIT.class)) //
        .selectors(selectClass(DefaultTenantFacadeIT.class)) //
        .selectors(selectClass(DisplayMenuFacadeIT.class)) //
        .selectors(selectClass(DisplayTemplateFacadeIT.class)) //
        .selectors(selectClass(InitFacadeIT.class)) //
        .selectors(selectClass(IntegrationPluginFacadeIT.class)) //
        .selectors(selectClass(IntegrationGeneratedFacadeIT.class)) //
        .selectors(selectClass(IntegrationProductFacadeIT.class)) //
        .selectors(selectClass(UserFavoriteFacadeIT.class)) //
        .selectors(selectClass(UserinfoVerificationFacadeIT.class)) //
        .build(); //
  }
}
