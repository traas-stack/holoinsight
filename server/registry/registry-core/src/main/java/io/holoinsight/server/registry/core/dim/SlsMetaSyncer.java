/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.dim;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;

import com.aliyun.openservices.log.Client;
import com.aliyun.openservices.log.common.Consts;
import com.aliyun.openservices.log.common.Shard;
import com.aliyun.openservices.log.exception.LogException;
import com.aliyun.openservices.log.http.client.ClientConfiguration;
import com.aliyun.openservices.log.http.comm.DefaultServiceClient;
import com.aliyun.openservices.log.response.ListShardResponse;
import com.google.common.collect.Maps;
import com.google.common.eventbus.Subscribe;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import io.holoinsight.server.common.NetUtils;
import io.holoinsight.server.common.event.EventBusHolder;
import io.holoinsight.server.common.event.ModuleInitEvent;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.common.util.ConstModel;
import io.holoinsight.server.meta.facade.service.DataClientService;
import io.holoinsight.server.registry.core.master.MasterJson;
import io.holoinsight.server.registry.core.master.MasterListener;
import io.holoinsight.server.registry.core.master.MasterService;
import io.holoinsight.server.registry.core.template.CollectRange;
import io.holoinsight.server.registry.core.template.CollectTemplate;
import io.holoinsight.server.registry.core.template.TemplateStorage;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/8/23
 *
 * @author xzchaoo
 */
@Slf4j
@DependsOn("metaGrpcServer")
public class SlsMetaSyncer implements InitializingBean, DisposableBean {
  private final Lock lock = new ReentrantLock();
  @Autowired
  private DataClientService dataClientService;
  @Autowired
  private CommonThreadPools commonThreadPools;
  private DefaultServiceClient serviceClient;
  @Autowired
  private TemplateStorage templateStorage;
  @Autowired
  private SlsConfig slsConfig;
  @Autowired
  private MasterService masterService;
  private MasterService.RegisterRecord registerRecord;

  @Override
  public void afterPropertiesSet() throws Exception {
    ClientConfiguration clientConfig = new ClientConfiguration();
    clientConfig.setMaxConnections(Consts.HTTP_CONNECT_MAX_COUNT);
    clientConfig.setConnectionTimeout(Consts.HTTP_CONNECT_TIME_OUT);
    clientConfig.setSocketTimeout(Consts.HTTP_SEND_TIME_OUT);
    serviceClient = new DefaultServiceClient(clientConfig);

    registerRecord = masterService.register("registry", "SlsMetaSyncer", NetUtils.getLocalIp(), "",
        new MasterListener() {
          @Override
          public void onChange(MasterJson oldMj, MasterJson newMj) {

          }

          @Override
          public void onEnter(MasterJson mj) {}

          @Override
          public void onLeave(MasterJson mj) {}
        });

    EventBusHolder.INSTANCE.register(this);
  }

  @Subscribe
  public void onEvent(ModuleInitEvent event) {
    if ("registry".equals(event.getSource())) {
      commonThreadPools.getIo().execute(this::syncOnce);
    }
  }

  @Override
  public void destroy() throws Exception {
    EventBusHolder.INSTANCE.unregister(this);
    serviceClient.shutdown();
  }

  private void syncOnce() {
    lock.lock();
    try {
      if (registerRecord.isMaster()) {
        long begin = System.currentTimeMillis();
        try {
          syncOnce0();
          long cost = System.currentTimeMillis() - begin;
          log.info("[sls] [meta] sync once cost=[{}]", cost);
        } catch (Exception e) {
          log.info("[sls] [meta] sync once, error", e);
        }
      }

      try {
        commonThreadPools.getScheduler().schedule(this::syncOnce,
            slsConfig.getBasic().getSyncInterval(), TimeUnit.SECONDS);
      } catch (Exception e) {
        log.error("[sls] [meta] scheduler error", e);
      }
    } finally {
      lock.unlock();
    }
  }

  private void syncOnce0() throws LogException {
    Map<String, SlsLogstore> logstores = new HashMap<>();

    for (CollectTemplate t : templateStorage.readonlyLoop()) {
      CollectRange cr = t.getCollectRange();
      CollectRange.Cloudmonitor cm = cr.getCloudmonitor();
      if (!CollectRange.CLOUDMONITOR.equals(cr.getType()) || cm == null) {
        continue;
      }
      if (!slsConfig.getBasic().getSlsDimTable().equals(cm.getTable())) {
        continue;
      }
      for (QueryExample qe : cm.getRanges()) {
        Map<String, Object> params = qe.getParams();

        String endpoint = ((List<String>) params.get("endpoint")).get(0);
        if (StringUtils.isEmpty(endpoint)) {
          endpoint = slsConfig.getBasic().getEndpoint();
        }
        String project = ((List<String>) params.get("project")).get(0);
        String logstore = ((List<String>) params.get("logstore")).get(0);
        if (StringUtils.isAnyEmpty(endpoint, project, logstore)) {
          log.warn("[sls] [meta] skip invalid condition {}", qe.getParams());
          continue;
        }

        String key = String.format("%s/%s/%s", endpoint, project, logstore);
        if (!logstores.containsKey(key)) {
          String ak = ((List<String>) params.get("ak")).get(0);
          String sk = ((List<String>) params.get("sk")).get(0);
          logstores.put(key, new SlsLogstore(endpoint, project, logstore, ak, sk));
        }
      }
    }

    Map<String, Map<String, Object>> oldDims = new HashMap<>();
    // List<String> deleteDims = new ArrayList<>();
    List<Map<String, Object>> upsertDims = new ArrayList<>();

    for (Map<String, Object> dim : dataClientService
        .queryAll(slsConfig.getBasic().getSlsDimTable())) {
      String uk = (String) dim.get("_uk");
      if (uk == null) {
        continue;
      }
      oldDims.put(uk, dim);
    }

    for (SlsLogstore logstore : logstores.values()) {
      String endpoint = logstore.getEndpoint();
      String ak = logstore.getAk();
      String sk = logstore.getSk();
      if (StringUtils.isAllEmpty(ak, sk)) {
        ak = slsConfig.getBasic().getAk();
        sk = slsConfig.getBasic().getSk();
      }

      try {
        syncShards(oldDims, upsertDims, logstore, endpoint, ak, sk);
      } catch (Exception e) {
        log.error("[sls] [meta] list shards error, logstore=[{}]", logstore, e);
      }
    }

    log.info("[sls] [meta] update old=[{}] upsert=[{}]", oldDims.size(), upsertDims);
    if (upsertDims.size() > 0) {
      dataClientService.insertOrUpdate(slsConfig.getBasic().getSlsDimTable(), upsertDims);
    }

  }

  private void syncShards(Map<String, Map<String, Object>> oldDims,
      List<Map<String, Object>> upsertDims, SlsLogstore logstore, String endpoint, String ak,
      String sk) throws LogException {

    ListShardResponse resp = new Client(endpoint, ak, sk, serviceClient)
        .ListShard(logstore.getProject(), logstore.getLogstore());

    for (Shard shard : resp.GetShards()) {
      Map<String, Object> map = Maps.newHashMapWithExpectedSize(8);
      Hasher ukHasher = Hashing.md5().newHasher();
      // uk = [endpoint, project, logstore, shardId]

      map.put("endpoint", logstore.getEndpoint());
      ukHasher.putString(logstore.getEndpoint(), StandardCharsets.UTF_8);

      map.put("project", logstore.getProject());
      ukHasher.putString(logstore.getProject(), StandardCharsets.UTF_8);

      map.put("logstore", logstore.getLogstore());
      ukHasher.putString(logstore.getLogstore(), StandardCharsets.UTF_8);

      map.put("shardId", shard.getShardId());
      ukHasher.putInt(shard.getShardId());

      if (logstore.getAk() != null) {
        map.put("ak", logstore.getAk());
      }
      if (logstore.getSk() != null) {
        map.put("sk", logstore.getSk());
      }

      map.put("_type", "sls_shard");
      map.put("status", shard.getStatus());

      String uk = ukHasher.hash().toString();
      map.put(ConstModel.default_pk, uk);

      Map<String, Object> old = oldDims.get(uk);
      if (old == null || !shard.getStatus().equals(old.get("status"))) {
        upsertDims.add(map);
      }
    }
  }
}
