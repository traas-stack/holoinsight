/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.xzchaoo.commons.stat.Measurement;
import com.xzchaoo.commons.stat.StringsKey;

import io.holoinsight.server.common.MetricsUtils;
import io.holoinsight.server.common.auth.ApikeyService;
import io.holoinsight.server.common.event.EventBusHolder;
import io.holoinsight.server.common.event.ModuleInitEvent;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.meta.facade.service.DataClientService;
import io.holoinsight.server.registry.core.agent.Agent;
import io.holoinsight.server.registry.core.agent.AgentStorage;
import io.holoinsight.server.registry.core.agent.AgentSyncer;
import io.holoinsight.server.registry.core.agent.CentralAgentService;
import io.holoinsight.server.registry.core.collecttarget.CollectTargetStorage;
import io.holoinsight.server.registry.core.grpc.RegistryServerForAgent;
import io.holoinsight.server.registry.core.template.CollectTemplate;
import io.holoinsight.server.registry.core.template.CollectTemplateSyncer;
import io.holoinsight.server.registry.core.template.TemplateStorage;
import io.prometheus.client.Gauge;
import reactor.core.publisher.Mono;

/**
 * <p>
 * created at 2022/3/1
 *
 * @author zzhb101
 */
@Component
@Order(100)
public class InitRunner implements ApplicationRunner {
  private static final Logger LOGGER = LoggerFactory.getLogger(InitRunner.class);
  @Autowired
  private CollectTemplateSyncer collectConfigSyncer;
  @Autowired
  private RegistryServerForAgent registryServerForAgent;
  @Autowired
  private AgentSyncer agentSyncer;
  @Autowired
  private AgentStorage agentStorage;
  @Autowired
  private TemplateStorage templateStorage;
  @Autowired
  private CentralAgentService centralAgentService;
  @Autowired
  private ApikeyService apikeyService;
  @Autowired
  private CommonThreadPools commonThreadPools;
  @Autowired
  private CollectTargetStorage collectTargetStorage;
  @Autowired
  private DataClientService dataClientService;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    long begin = System.currentTimeMillis();
    warmUpDataClientService(dataClientService);

    // TODO 分成2个阶段
    // 1. 初始化独立加载元数据 dims,templates

    Mono<Void> agentMono = agentSyncer.initLoad();
    Mono<Void> templateMono = collectConfigSyncer.initLoadTemplates();

    Mono.when(agentMono, templateMono) //
        // 2. 初始化那些需要交叉元数据的组件 targets
        .then(Mono.defer(() -> collectConfigSyncer.initApplyTemplates())) //
        .block(); //

    agentSyncer.startSync();
    collectConfigSyncer.startSync();

    // 允许对外提供服务
    registryServerForAgent.start();

    long end = System.currentTimeMillis();
    LOGGER.info("biz init cost=[{}]", end - begin);
    startStat();
    EventBusHolder.INSTANCE.post(new ModuleInitEvent("registry"));
  }

  private void warmUpDataClientService(DataClientService dataClientService) {
    for (int i = 0; i < 10; i++) {
      try {
        // TODO 不知道为什么前几次很容易失败, 因此这里预热一下, 最终要让prod去修
        dataClientService.queryAll("anything");
        break;
      } catch (Throwable ignored) {
        // This catch statement is intentionally empty
      }
    }
  }

  private void startStat() {
    MetricsUtils.SM.func(StringsKey.of("agent.count"), () -> {
      List<Measurement> ms = new ArrayList<>();
      Map<StringsKey, int[]> count = new HashMap<>();
      for (Agent a : agentStorage.readonlyLoop()) {
        String mode = a.getJson().getMode();
        if (StringUtils.isEmpty("mode")) {
          mode = "unknown";
        }
        StringsKey key = StringsKey.of(a.getTenant(), mode);
        count.computeIfAbsent(key, i -> new int[] {0})[0]++;
      }
      count.forEach((k, v) -> {
        ms.add(new Measurement(k, new long[] {v[0]}));
      });
      return ms;
    });

    // 中心化集群个数
    MetricsUtils.SM.gauge(StringsKey.of("agent.central.count"), centralAgentService, x -> { //
      return x.getState().getClusters().size();
    });
    MetricsUtils.SM.gauge(StringsKey.of("apikey.count"), apikeyService,
        x -> x.getApikeyMap().size());

    Gauge templateCount = Gauge.build("registry_template_count", "registry_template_count")
        .labelNames("tenant").register();
    Gauge agentCount = Gauge.build("registry_agent_count", "registry_template_count")
        .labelNames("tenant").register();
    Gauge targetCount = Gauge.build("registry_target_count", "registry_target_count")
        .labelNames("tenant").register();
    Gauge apikeyCount = Gauge.build("registry_apikey_count", "registry_apikey_count").register();

    Gauge agentConnectingCount =
        Gauge.build("registry_agent_connecting_count", "registry_agent_connecting_count") //
            .labelNames("tenant") //
            .register(); //

    commonThreadPools.getScheduler().scheduleWithFixedDelay(() -> {
      templateStorage.getTenantCountMap()
          .forEach((tenant, count) -> templateCount.labels(tenant).set(count));
      agentStorage.getTenantCountMap()
          .forEach((tenant, count) -> agentCount.labels(tenant).set(count));

      Map<String, Integer> targetTenantCountMap = new HashMap<>();
      for (CollectTemplate t : templateStorage.readonlyLoop()) {
        int count = collectTargetStorage.countByTemplate(t.getId());
        if (count > 0) {
          targetTenantCountMap.put(t.getTenant(),
              targetTenantCountMap.getOrDefault(t.getTenant(), 0) + count);
        }
      }
      targetTenantCountMap.forEach((tenant, count) -> targetCount.labels(tenant).set(count));

      apikeyCount.set(apikeyService.getApikeyMap().size());
      {
        agentStorage.getConnectionTenantCountMap()
            .forEach((tenant, count) -> agentConnectingCount.labels(tenant).set(count));
      }

    }, 5, 5, TimeUnit.SECONDS);
  }
}
