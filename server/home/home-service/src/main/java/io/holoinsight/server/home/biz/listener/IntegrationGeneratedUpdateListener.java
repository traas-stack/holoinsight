/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.listener;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.plugin.config.BasePluginConfig;
import io.holoinsight.server.home.biz.plugin.config.MetaLabel;
import io.holoinsight.server.home.biz.plugin.config.PortCheckPluginConfig;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.dal.model.dto.CloudMonitorRange;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO.GaeaCollectRange;
import io.holoinsight.server.home.dal.model.dto.IntegrationGeneratedDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: IntegrationGeneratedUpdateListener.java, Date: 2023-07-26 Time: 10:57
 */
@Component
@Slf4j
public class IntegrationGeneratedUpdateListener {

  @Autowired
  private IntegrationPluginUpdateListener integrationPluginUpdateListener;

  @Autowired
  private TenantInitService tenantInitService;

  @PostConstruct
  void register() {
    EventBusHolder.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onEvent(IntegrationGeneratedDTO integrationGeneratedDTO) {
    log.info("[integration_generated][{}][{}] convert start", integrationGeneratedDTO.getProduct(),
        integrationGeneratedDTO.getId());

    try {
      if (CollectionUtils.isEmpty(integrationGeneratedDTO.config))
        return;
      IntegrationPluginDTO integrationPluginDTO = convertIntegrationPlugin(integrationGeneratedDTO);

      if (StringUtils.isBlank(integrationPluginDTO.json)
          || StringUtils.isBlank(integrationPluginDTO.type))
        return;
      List<Long> upsert = integrationPluginUpdateListener.upsertGaea(integrationPluginDTO);
      notify(upsert);
    } catch (Throwable t) {
      log.error("[integration_plugin][{}][{}] convert error, {}",
          integrationGeneratedDTO.getProduct(), integrationGeneratedDTO.getId(), t.getMessage(), t);
    }
    log.info("[integration_generated][{}][{}] convert end", integrationGeneratedDTO.getProduct(),
        integrationGeneratedDTO.getId());
  }

  private IntegrationPluginDTO convertIntegrationPlugin(IntegrationGeneratedDTO generatedDTO) {
    IntegrationPluginDTO integrationPluginDTO = new IntegrationPluginDTO();

    integrationPluginDTO.id = generatedDTO.id;
    integrationPluginDTO.tenant = generatedDTO.tenant;
    integrationPluginDTO.workspace = generatedDTO.workspace;
    integrationPluginDTO.product = generatedDTO.getProduct();
    integrationPluginDTO.name = generatedDTO.tenant + "_"
        + (StringUtils.isNotBlank(generatedDTO.getWorkspace()) ? generatedDTO.getWorkspace() : "_")
        + "_" + generatedDTO.getProduct() + "_" + generatedDTO.getItem() + "_"
        + generatedDTO.getName();
    integrationPluginDTO.status = true;
    if (!CollectionUtils.isEmpty(generatedDTO.getConfig())
        && generatedDTO.getConfig().containsKey("status")) {
      integrationPluginDTO.status =
          generatedDTO.getConfig().get("status").toString().equalsIgnoreCase("ONLINE");
    }
    integrationPluginDTO.collectRange = new HashMap<>();

    GaeaCollectRange gaeaCollectRange = convertAppRange(generatedDTO);
    integrationPluginDTO.collectRange = J.toMap(J.toJson(gaeaCollectRange));

    if (!CollectionUtils.isEmpty(generatedDTO.getConfig())) {
      switch (generatedDTO.getProduct().toLowerCase()) {
        case "portcheck":
          integrationPluginDTO.type = "io.holoinsight.plugin.PortCheckPlugin";
          integrationPluginDTO.json = convertPortCheck(generatedDTO);
          break;
        case "jvm":
          integrationPluginDTO.type = "io.holoinsight.plugin.JvmPlugin";
          integrationPluginDTO.json = convertJvm(generatedDTO);
          break;
        case "springboot":
          integrationPluginDTO.type = "io.holoinsight.plugin.SpringBootPlugin";
          integrationPluginDTO.json = convertPluginJson(generatedDTO);
          break;
        case "logpattern":
          integrationPluginDTO.type = "io.holoinsight.server.plugin.LogPatternPlugin";
          integrationPluginDTO.json = convertPluginJson(generatedDTO);
          break;
        case "multilog":
          integrationPluginDTO.type = "io.holoinsight.server.plugin.MultiLogPlugin";
          integrationPluginDTO.json = convertPluginJson(generatedDTO);
          break;
      }
    }

    integrationPluginDTO.version = "1";

    return integrationPluginDTO;
  }

  private GaeaCollectRange convertAppRange(IntegrationGeneratedDTO generatedDTO) {
    CloudMonitorRange cloudMonitorRange = tenantInitService.getCollectMonitorRange(
        tenantInitService.getTenantServerTable(generatedDTO.getTenant()), generatedDTO.getTenant(),
        generatedDTO.getWorkspace(), Collections.singletonList(generatedDTO.getName()),
        MetaLabel.partApp);
    return GaeaConvertUtil.convertCentralCollectRange(cloudMonitorRange);
  }

  private String convertPortCheck(IntegrationGeneratedDTO generatedDTO) {
    Map<String, Object> config = generatedDTO.getConfig();
    if (!config.containsKey("confs"))
      return null;

    List<PortCheckPluginConfig> confs = J.fromJson(J.toJson(config.get("confs")),
        new TypeToken<List<PortCheckPluginConfig>>() {}.getType());
    if (CollectionUtils.isEmpty(confs))
      return null;

    List<Integer> ports = new ArrayList<>();
    confs.forEach(portCheckPluginConfig -> {
      ports.add(portCheckPluginConfig.getPort());
    });

    List<PortCheckPluginConfig> portCheckPluginConfigs = new ArrayList<>();
    PortCheckPluginConfig portCheckPluginConfig = new PortCheckPluginConfig();
    portCheckPluginConfig.ports = ports;
    portCheckPluginConfig.metaLabel = MetaLabel.partApp;
    portCheckPluginConfig.range = new ArrayList<>();
    portCheckPluginConfig.networkMode = "AGENT";
    portCheckPluginConfig.range.add(generatedDTO.getName());
    portCheckPluginConfigs.add(portCheckPluginConfig);

    Map<String, Object> map = new HashMap<>();
    map.put("confs", portCheckPluginConfigs);
    return J.toJson(map);
  }

  private String convertJvm(IntegrationGeneratedDTO generatedDTO) {

    Map<String, Object> map = new HashMap<>();
    List<BasePluginConfig> basePluginConfigs = new ArrayList<>();
    BasePluginConfig basePluginConfig = new BasePluginConfig();
    basePluginConfig.metaLabel = MetaLabel.partApp;
    basePluginConfig.range = new ArrayList<>();
    basePluginConfig.range.add(generatedDTO.getName());
    basePluginConfigs.add(basePluginConfig);
    map.put("confs", basePluginConfigs);
    return J.toJson(map);
  }

  private String convertPluginJson(IntegrationGeneratedDTO generatedDTO) {

    Map<String, Object> config = generatedDTO.getConfig();
    if (!config.containsKey("confs"))
      return null;
    return J.toJson(config);
  }

  private void notify(List<Long> upsertList) {
    // grpc 通知id更新

  }
}
