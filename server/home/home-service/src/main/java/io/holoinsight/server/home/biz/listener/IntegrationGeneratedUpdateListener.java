/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.listener;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.plugin.config.MetaLabel;
import io.holoinsight.server.home.biz.plugin.config.PortCheckPluginConfig;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.dal.model.dto.IntegrationGeneratedDTO;
import io.holoinsight.server.home.dal.model.dto.IntegrationPluginDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: IntegrationGeneratedUpdateListener.java, Date: 2023-07-26 Time: 10:57
 */
@Component
public class IntegrationGeneratedUpdateListener {

  @Autowired
  private IntegrationPluginUpdateListener integrationPluginUpdateListener;

  @PostConstruct
  void register() {
    EventBusHolder.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onEvent(IntegrationGeneratedDTO integrationGeneratedDTO) {
    if (CollectionUtils.isEmpty(integrationGeneratedDTO.config))
      return;
    IntegrationPluginDTO integrationPluginDTO = convertIntegrationPlugin(integrationGeneratedDTO);

    if (StringUtils.isBlank(integrationPluginDTO.json)
        || StringUtils.isBlank(integrationPluginDTO.type))
      return;
    List<Long> upsert = integrationPluginUpdateListener.upsertGaea(integrationPluginDTO);
    notify(upsert);
  }

  private IntegrationPluginDTO convertIntegrationPlugin(IntegrationGeneratedDTO generatedDTO) {
    IntegrationPluginDTO integrationPluginDTO = new IntegrationPluginDTO();

    integrationPluginDTO.id = generatedDTO.id;
    integrationPluginDTO.tenant = generatedDTO.tenant;
    integrationPluginDTO.workspace = generatedDTO.workspace;
    integrationPluginDTO.product = generatedDTO.getProduct();
    integrationPluginDTO.name = generatedDTO.id + "_" + generatedDTO.getProduct() + "_"
        + generatedDTO.getItem() + "_" + generatedDTO.getName();
    integrationPluginDTO.status = !CollectionUtils.isEmpty(generatedDTO.getConfig());

    switch (generatedDTO.getProduct().toLowerCase()) {
      case "portcheck":
        integrationPluginDTO.type = "io.holoinsight.plugin.PortCheckPlugin";
        integrationPluginDTO.json = convertPortCheck(generatedDTO);
        break;
      case "logpattern":
        integrationPluginDTO.type = "io.holoinsight.server.plugin.LogPatternPlugin";
        integrationPluginDTO.json = convertLogPattern(generatedDTO);
        break;
    }

    integrationPluginDTO.collectRange = new HashMap<>();
    integrationPluginDTO.version = "1";

    return integrationPluginDTO;
  }

  private String convertLogPattern(IntegrationGeneratedDTO generatedDTO) {
    Map<String, Object> config = generatedDTO.getConfig();
    if (!config.containsKey("confs"))
      return null;
    // List<LogPluginConfig> == confs
    return J.toJson(config);
  }

  private String convertPortCheck(IntegrationGeneratedDTO generatedDTO) {
    Map<String, Object> config = generatedDTO.getConfig();
    if (!config.containsKey("ports"))
      return null;

    List<String> ports = (List<String>) config.get("ports");
    Map<String, Object> map = new HashMap<>();

    List<PortCheckPluginConfig> portCheckPluginConfigs = new ArrayList<>();
    ports.forEach(port -> {
      PortCheckPluginConfig portCheckPluginConfig = new PortCheckPluginConfig();
      portCheckPluginConfig.port = Integer.parseInt(port);
      portCheckPluginConfig.metaLabel = MetaLabel.partApp;
      portCheckPluginConfig.range = new ArrayList<>();
      portCheckPluginConfig.range.add(generatedDTO.getName());
      portCheckPluginConfigs.add(portCheckPluginConfig);
    });
    map.put("confs", portCheckPluginConfigs);
    return J.toJson(map);
  }

  private void notify(List<Long> upsertList) {
    // grpc 通知id更新

  }
}
