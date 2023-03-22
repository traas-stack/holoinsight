/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.listener;

import io.holoinsight.server.home.biz.plugin.PluginRepository;
import io.holoinsight.server.home.biz.plugin.core.AbstractIntegrationPlugin;
import io.holoinsight.server.home.biz.plugin.model.HostingPlugin;
import io.holoinsight.server.home.biz.service.GaeaCollectConfigService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

import static io.holoinsight.server.home.biz.service.impl.IntegrationPluginServiceImpl.checkActionType;
import static io.holoinsight.server.home.biz.service.impl.IntegrationPluginServiceImpl.isClassicPlugin;

/**
 * @author xiangwanpeng
 * @version 1.0: IntegrationPluginUpdateListener.java, v 0.1 2022年06月08日 8:40 下午 xiangwanpeng Exp $
 */
@Component
public class IntegrationPluginUpdateListener {

  @Autowired
  private GaeaCollectConfigService gaeaCollectConfigService;
  @Autowired
  private PluginRepository pluginRepository;

  @PostConstruct
  void register() {
    EventBusHolder.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onEvent(IntegrationPluginDTO integrationPluginDTO) {
    boolean needUpsertGaea = isClassicPlugin(integrationPluginDTO.getProduct())
        || checkActionType(integrationPluginDTO, null, this.pluginRepository);
    if (needUpsertGaea) {
      List<Long> upsert = upsertGaea(integrationPluginDTO);
      notify(upsert);
    } else if (this.pluginRepository.isHostingPlugin(integrationPluginDTO.type)) {
      HostingPlugin hostingPlugin = (HostingPlugin) this.pluginRepository
          .getTemplate(integrationPluginDTO.type, integrationPluginDTO.version);
      if (integrationPluginDTO.status) {
        hostingPlugin.apply(integrationPluginDTO);
      } else {
        hostingPlugin.disable(integrationPluginDTO);
      }

    }
  }

  private List<Long> upsertGaea(IntegrationPluginDTO integrationPluginDTO) {
    GaeaCollectConfigDTO gaeaCollectConfigDTO = new GaeaCollectConfigDTO();
    gaeaCollectConfigDTO.tenant = integrationPluginDTO.tenant;
    gaeaCollectConfigDTO.workspace = integrationPluginDTO.workspace;
    gaeaCollectConfigDTO.deleted = false;
    if (!integrationPluginDTO.status) {
      gaeaCollectConfigDTO.deleted = true;
    }
    List<Long> upsertList = new ArrayList<>();
    if (this.pluginRepository.contains(integrationPluginDTO.type)) {

      AbstractIntegrationPlugin plugin = (AbstractIntegrationPlugin) this.pluginRepository
          .getTemplate(integrationPluginDTO.type, integrationPluginDTO.version);

      List<AbstractIntegrationPlugin> abstractIntegrationPlugins =
          plugin.genPluginList(integrationPluginDTO);
      for (AbstractIntegrationPlugin integrationPlugin : abstractIntegrationPlugins) {
        gaeaCollectConfigDTO.collectRange = integrationPlugin.getGaeaCollectRange();
        gaeaCollectConfigDTO.type = integrationPlugin.collectPlugin;
        gaeaCollectConfigDTO.tableName = integrationPlugin.gaeaTableName;
        gaeaCollectConfigDTO.refId = "integration_" + integrationPluginDTO.getId();
        gaeaCollectConfigDTO.executorSelector = integrationPlugin.getExecutorSelector();
        gaeaCollectConfigDTO.version = 1L;
        gaeaCollectConfigDTO.json = integrationPlugin.generateCollectConfig();

        if (gaeaCollectConfigDTO.deleted) {
          Long deletedId = gaeaCollectConfigService.updateDeleted(gaeaCollectConfigDTO.tableName);
          if (deletedId != null) {
            upsertList.add(deletedId);
          }
        } else {
          GaeaCollectConfigDTO upsert = gaeaCollectConfigService.upsert(gaeaCollectConfigDTO);
          if (null != upsert) {
            upsertList.add(upsert.id);
          }
        }
      }
    }
    return upsertList;
  }

  private void notify(List<Long> upsertList) {

    // grpc 通知id更新
  }
}
