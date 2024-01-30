/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.listener;

import io.holoinsight.server.home.biz.common.GaeaConvertUtil;
import io.holoinsight.server.home.biz.service.GaeaCollectConfigService;
import io.holoinsight.server.home.biz.service.TenantInitService;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.dal.model.dto.GaeaCollectConfigDTO;
import io.holoinsight.server.home.dal.model.dto.OpenmetricsScraperDTO;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import io.holoinsight.server.registry.model.OpenmetricsScraperTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class OpenmetricsScraperUpdateListener {

  @Autowired
  private GaeaCollectConfigService gaeaCollectConfigService;

  @Autowired
  private TenantInitService tenantInitService;

  @PostConstruct
  void register() {
    EventBusHolder.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onEvent(OpenmetricsScraperDTO openmetricsScraperDTO) {
    upsert(openmetricsScraperDTO);
  }

  private void upsert(OpenmetricsScraperDTO openmetricsScraperDTO) {

    OpenmetricsScraperTask task = new OpenmetricsScraperTask();
    task.setSchema(openmetricsScraperDTO.getSchema());
    task.setMetricsPath(openmetricsScraperDTO.getMetricsPath());
    task.setScrapeInterval(openmetricsScraperDTO.getScrapeInterval());
    task.setScrapeTimeout(openmetricsScraperDTO.getScrapeTimeout());
    task.setScrapePort(openmetricsScraperDTO.getPort());
    task.setRelabelConfigs(openmetricsScraperDTO.getRelabelConfigs());
    task.setMetricRelabelConfigs(openmetricsScraperDTO.getMetricRelabelConfigs());

    // List<String> targets = new ArrayList<>();
    //// 暂时 mock 一下
    // targets.add(openmetricsScraperDTO.getIp() + ":" + openmetricsScraperDTO.getPort());
    // task.setTargets(targets);

    GaeaCollectConfigDTO gaeaCollectConfigDTO = new GaeaCollectConfigDTO();
    gaeaCollectConfigDTO.tenant =
        tenantInitService.getTsdbTenant(openmetricsScraperDTO.getTenant());
    gaeaCollectConfigDTO.workspace = openmetricsScraperDTO.getWorkspace();
    gaeaCollectConfigDTO.deleted = false;
    gaeaCollectConfigDTO.type = openmetricsScraperDTO.getClass().getName();
    gaeaCollectConfigDTO.refId = "openmetrics_" + openmetricsScraperDTO.getId();
    gaeaCollectConfigDTO.executorSelector = new HashMap<>();
    gaeaCollectConfigDTO.json = task;
    gaeaCollectConfigDTO.tableName = "openmetrics_" + openmetricsScraperDTO.getId();
    gaeaCollectConfigDTO.collectRange =
        GaeaConvertUtil.convertCollectRange(openmetricsScraperDTO.getCollectRanges());
    gaeaCollectConfigDTO.executorSelector = convertExecutorSelector();

    gaeaCollectConfigService.upsert(gaeaCollectConfigDTO);
  }

  private Map<String, Object> convertExecutorSelector() {

    Map<String, Object> executorSelector = new HashMap<>();
    executorSelector.put("type", "sidecar");
    executorSelector.put("sidecar", new HashMap<>());

    return executorSelector;
  }

}
