/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service.config;

import io.holoinsight.server.common.FacadeTemplate;
import io.holoinsight.server.common.FacadeTemplateImpl;
import io.holoinsight.server.common.grpc.SlowGrpcProperties;
import io.holoinsight.server.common.service.AggTaskV1Service;
import io.holoinsight.server.common.service.AlarmHistoryDetailService;
import io.holoinsight.server.common.service.AlarmHistoryService;
import io.holoinsight.server.common.service.AlarmMetricService;
import io.holoinsight.server.common.service.AlertBlockService;
import io.holoinsight.server.common.service.AlertDingDingRobotService;
import io.holoinsight.server.common.service.AlertGroupService;
import io.holoinsight.server.common.service.AlertNotifyRecordService;
import io.holoinsight.server.common.service.AlertSubscribeService;
import io.holoinsight.server.common.service.AlertWebhookService;
import io.holoinsight.server.common.service.AlertmanagerWebhookService;
import io.holoinsight.server.common.service.ApiKeyService;
import io.holoinsight.server.common.service.ClusterService;
import io.holoinsight.server.common.service.ClusterTaskService;
import io.holoinsight.server.common.service.DashboardService;
import io.holoinsight.server.common.service.DisplayMenuService;
import io.holoinsight.server.common.service.DisplayTemplateService;
import io.holoinsight.server.common.service.FolderService;
import io.holoinsight.server.common.service.IntegrationGeneratedService;
import io.holoinsight.server.common.service.IntegrationPluginService;
import io.holoinsight.server.common.service.IntegrationProductService;
import io.holoinsight.server.common.service.MarketplacePluginService;
import io.holoinsight.server.common.service.MarketplaceProductService;
import io.holoinsight.server.common.service.MetaDataDictValueService;
import io.holoinsight.server.common.service.MetaDictValueService;
import io.holoinsight.server.common.service.MetaDimDataService;
import io.holoinsight.server.common.service.MetaTableService;
import io.holoinsight.server.common.service.MetricInfoService;
import io.holoinsight.server.common.service.MonitorInstanceService;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.common.service.TenantOpsService;
import io.holoinsight.server.common.service.TenantService;
import io.holoinsight.server.common.service.TimedEventService;
import io.holoinsight.server.common.service.TraceAgentConfPropService;
import io.holoinsight.server.common.service.TraceAgentConfigurationService;
import io.holoinsight.server.common.service.UserFavoriteService;
import io.holoinsight.server.common.service.UserOpLogService;
import io.holoinsight.server.common.service.UserinfoService;
import io.holoinsight.server.common.service.WorkspaceService;
import io.holoinsight.server.common.service.impl.AggTaskV1ServiceImpl;
import io.holoinsight.server.common.service.impl.AlarmHistoryDetailServiceImpl;
import io.holoinsight.server.common.service.impl.AlarmHistoryServiceImpl;
import io.holoinsight.server.common.service.impl.AlarmMetricServiceImpl;
import io.holoinsight.server.common.service.impl.AlertBlockServiceImpl;
import io.holoinsight.server.common.service.impl.AlertDingDingRobotServiceImpl;
import io.holoinsight.server.common.service.impl.AlertGroupServiceImpl;
import io.holoinsight.server.common.service.impl.AlertNotifyRecordServiceImpl;
import io.holoinsight.server.common.service.impl.AlertSubscribeServiceImpl;
import io.holoinsight.server.common.service.impl.AlertWebhookServiceImpl;
import io.holoinsight.server.common.service.impl.AlertmanagerWebhookServiceImpl;
import io.holoinsight.server.common.service.impl.ApiKeyServiceImpl;
import io.holoinsight.server.common.service.impl.ClusterServiceImpl;
import io.holoinsight.server.common.service.impl.ClusterTaskServiceImpl;
import io.holoinsight.server.common.service.impl.DashboardServiceImpl;
import io.holoinsight.server.common.service.impl.DisplayMenuServiceImpl;
import io.holoinsight.server.common.service.impl.DisplayTemplateServiceImpl;
import io.holoinsight.server.common.service.impl.FolderServiceImpl;
import io.holoinsight.server.common.service.impl.IntegrationGeneratedServiceImpl;
import io.holoinsight.server.common.service.impl.IntegrationPluginServiceImpl;
import io.holoinsight.server.common.service.impl.IntegrationProductServiceImpl;
import io.holoinsight.server.common.service.impl.MarketplacePluginServiceImpl;
import io.holoinsight.server.common.service.impl.MarketplaceProductServiceImpl;
import io.holoinsight.server.common.service.impl.MetaDataDictValueServiceImpl;
import io.holoinsight.server.common.service.impl.MetaDimDataServiceImpl;
import io.holoinsight.server.common.service.impl.MetaTableServiceImpl;
import io.holoinsight.server.common.service.impl.MetricInfoServiceImpl;
import io.holoinsight.server.common.service.impl.MonitorInstanceServiceImpl;
import io.holoinsight.server.common.service.impl.TenantOpsServiceImpl;
import io.holoinsight.server.common.service.impl.TenantServiceImpl;
import io.holoinsight.server.common.service.impl.TimedEventServiceImpl;
import io.holoinsight.server.common.service.impl.TraceAgentConfPropServiceImpl;
import io.holoinsight.server.common.service.impl.TraceAgentConfigurationServiceImpl;
import io.holoinsight.server.common.service.impl.UserFavoriteServiceImpl;
import io.holoinsight.server.common.service.impl.UserOpLogServiceImpl;
import io.holoinsight.server.common.service.impl.UserinfoServiceImpl;
import io.holoinsight.server.common.service.impl.WorkspaceServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author jsy1001de
 * @version 1.0: ServiceAutoConfiguration.java, v 0.1 2023年02月27日 下午2:20 jinsong.yjs Exp $
 */
@EnableConfigurationProperties(SlowGrpcProperties.class)
@Configuration
public class CommonServiceAutoConfiguration {
  @Bean
  @ConditionalOnMissingBean
  public MetaDictValueService metaDictValueService() {
    return new MetaDictValueService();
  }

  @Bean
  @ConditionalOnMissingBean
  public MetaDataDictValueService metaDataDictValueService() {
    return new MetaDataDictValueServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public SuperCacheService superCacheService() {
    return new SuperCacheService();
  }

  @Bean
  @ConditionalOnMissingBean
  public TenantService tenantService() {
    return new TenantServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public WorkspaceService workspaceService() {
    return new WorkspaceServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public MetricInfoService metricInfoService() {
    return new MetricInfoServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public MonitorInstanceService monitorInstanceService() {
    return new MonitorInstanceServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public ClusterService clusterService() {
    return new ClusterServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public FacadeTemplate facadeTemplate() {
    return new FacadeTemplateImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AggTaskV1Service aggTaskV1Service() {
    return new AggTaskV1ServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AlarmHistoryDetailService alarmHistoryDetailService() {
    return new AlarmHistoryDetailServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AlarmHistoryService alarmHistoryService() {
    return new AlarmHistoryServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AlarmMetricService alarmMetricService() {
    return new AlarmMetricServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AlertBlockService alertBlockService() {
    return new AlertBlockServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AlertDingDingRobotService alertDingDingRobotService() {
    return new AlertDingDingRobotServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AlertGroupService alertGroupService() {
    return new AlertGroupServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AlertmanagerWebhookService alertmanagerWebhookService() {
    return new AlertmanagerWebhookServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AlertNotifyRecordService alertNotifyRecordService() {
    return new AlertNotifyRecordServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AlertSubscribeService alertSubscribeService() {
    return new AlertSubscribeServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public AlertWebhookService alertWebhookService() {
    return new AlertWebhookServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public UserOpLogService userOpLogService() {
    return new UserOpLogServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public UserFavoriteService userFavoriteService() {
    return new UserFavoriteServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public UserinfoService userinfoService() {
    return new UserinfoServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public TimedEventService timedEventService() {
    return new TimedEventServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public ApiKeyService apiKeyService() {
    return new ApiKeyServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public ClusterTaskService clusterTaskService() {
    return new ClusterTaskServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public MetaDimDataService metaDimDataService() {
    return new MetaDimDataServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public DashboardService dashboardService() {
    return new DashboardServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public DisplayMenuService displayMenuService() {
    return new DisplayMenuServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public DisplayTemplateService displayTemplateService() {
    return new DisplayTemplateServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public FolderService folderService() {
    return new FolderServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public IntegrationGeneratedService integrationGeneratedService() {
    return new IntegrationGeneratedServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public IntegrationPluginService integrationPluginService() {
    return new IntegrationPluginServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public IntegrationProductService integrationProductService() {
    return new IntegrationProductServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public MarketplacePluginService marketplacePluginService() {
    return new MarketplacePluginServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public MarketplaceProductService marketplaceProductService() {
    return new MarketplaceProductServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public TraceAgentConfigurationService traceAgentConfigurationService() {
    return new TraceAgentConfigurationServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public TraceAgentConfPropService traceAgentConfPropService() {
    return new TraceAgentConfPropServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public MetaTableService metaTableService() {
    return new MetaTableServiceImpl();
  }

  @Bean
  @ConditionalOnMissingBean
  public TenantOpsService tenantOpsService() {
    return new TenantOpsServiceImpl();
  }
}
