/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Maps;
import com.xzchaoo.commons.basic.Ack;
import com.xzchaoo.commons.basic.Acks;
import com.xzchaoo.commons.batchprocessor.BatchProcessorConfig;
import com.xzchaoo.commons.batchprocessor.DrainLoopBatchProcessor;
import com.xzchaoo.commons.batchprocessor.Flusher;
import com.xzchaoo.commons.stat.StatAccumulator;
import com.xzchaoo.commons.stat.StringsKey;

import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.NetUtils;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.common.dao.entity.GaeaAgentDO;
import io.holoinsight.server.common.dao.entity.GaeaAgentDOExample;
import io.holoinsight.server.common.dao.mapper.GaeaAgentDOMapper;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.model.MetaType;
import io.holoinsight.server.meta.facade.service.AgentHeartBeatService;
import io.holoinsight.server.meta.facade.service.DataClientService;
import io.holoinsight.server.common.event.EventBusHolder;
import io.holoinsight.server.common.MetricsUtils;
import io.holoinsight.server.registry.grpc.agent.AgentK8sInfo;
import io.holoinsight.server.registry.grpc.agent.RegisterAgentRequest;

/**
 * <p>
 * created at 2022/3/9
 *
 * @author zzhb101
 */
@Service
public class AgentService {

  private static final Logger LOGGER = LoggerFactory.getLogger("AGENT");
  private static final StatAccumulator<StringsKey> AGENT_HEARTBEAT_STAT =
      MetricsUtils.SM.create("agent.heartbeat");

  @Autowired
  private GaeaAgentDOMapper mapper;
  @Autowired
  private CommonThreadPools commonThreadPools;
  private DrainLoopBatchProcessor<String> bp;
  @Autowired
  private AgentStorage agentStorage;
  @Autowired
  private AgentHeartBeatService agentHeartBeatService;
  @Autowired
  private AgentConfig agentConfig;
  @Autowired
  private DataClientService dataClientService;

  @PostConstruct
  public void init() {
    BatchProcessorConfig config = new BatchProcessorConfig();
    config.setName("AgentService-Heartbeat");
    config.setBatchSize(256);
    config.setFlushInterval(Duration.ofSeconds(5));
    config.setCopyBatchForFlush(true);

    // noinspection Convert2Lambda
    bp = new DrainLoopBatchProcessor<>(config, new Flusher<String>() {
      @Override
      public void flush(List<String> agentIds, Ack ack) {
        Acks.execute(ack, commonThreadPools.getIo(), () -> flushHeartbeats(agentIds));
      }
    }, commonThreadPools.getScheduler());

    bp.start();
  }

  private void flushHeartbeats(List<String> agentIds) {
    GaeaAgentDO record = new GaeaAgentDO();
    record.setGmtModified(new Date());

    GaeaAgentDOExample example = GaeaAgentDOExample.newAndCreateCriteria() //
        .andAgentIdIn(agentIds) //
        .example(); //

    long begin = System.currentTimeMillis();
    int count = mapper.updateByExampleSelective(record, example, GaeaAgentDO.Column.gmtModified);
    long end = System.currentTimeMillis();

    AGENT_HEARTBEAT_STAT.add(StringsKey.EMPTY, new long[] {1, count, end - begin});
  }

  @PreDestroy
  public void stop() {
    bp.stop();
  }

  public void registerAgent(AuthInfo a, RegisterAgentRequest request) {
    registerAgent0(a, request);
  }

  private void notifyProd(AuthInfo a, String agentId) {
    if (!agentConfig.getMeta().isEnabled()) {
      return;
    }
    Agent agent = agentStorage.get(agentId);
    if (agent != null && (agent.getJson().getMode().equals(Agent.MODE_SIDECAR))) {
      long begin = System.currentTimeMillis();
      String table = a.getTenant() + "_server";
      Map<String, Object> row = Maps.newHashMapWithExpectedSize(1);
      try {
        row.put("agentId", agentId);
        String ip;
        String hostname;
        if (agent.getJson().getK8s() != null) {
          ip = agent.getJson().getK8s().getHostIP();
          hostname = agent.getJson().getK8s().getNodeHostname();
        } else {
          ip = agent.getJson().getIp();
          hostname = agent.getJson().getHostname();
          row.put("app", agent.getJson().getApp());
        }
        row.put("name", hostname);
        agentHeartBeatService.agentInsertOrUpdate(table, ip, hostname, row);
      } catch (Throwable e) {
        LOGGER.error("[prod] [agentInsertOrUpdate] error", e);
      } finally {
        long end = System.currentTimeMillis();
        LOGGER.info("[prod] [agentInsertOrUpdate] update [{}] row={} cost=[{}]", table, row,
            end - begin);
      }
    }
  }

  private void notifyProd(AuthInfo a, RegisterAgentRequest request, MetaType metaType) {
    if (!agentConfig.getMeta().isEnabled()) {
      return;
    }

    long begin = System.currentTimeMillis();
    Map<String, Object> row = Maps.newHashMapWithExpectedSize(1);
    row.put("agentId", request.getAgentId());

    if (request.getMode().equals(Agent.MODE_SIDECAR)) {
      row.put("app", request.getApp());
    }

    String workspace = request.getWorkspace();
    String cluster = request.getCluster();
    if (StringUtils.isEmpty(workspace)) {
      workspace = "default";
    }
    if (StringUtils.isEmpty(cluster)) {
      cluster = "default";
    }
    row.put("workspace", workspace);
    row.put("cluster", cluster);
    String hostIP = request.getK8S().getHostIp();
    if (StringUtils.isEmpty(hostIP)) {
      hostIP = request.getIp();
    }
    row.put("hostIP", hostIP);

    String ip = request.getIp();
    String name = request.getHostname();
    String hostname = request.getHostname();

    if (metaType == MetaType.AGENT) {
      HashMap<Object, Object> m = new HashMap<>();
      m.put("namespace", request.getK8S().getNamespace());
      m.put("hostIP", request.getK8S().getHostIp());
      m.put("pod", request.getK8S().getPod());
      m.put("nodeHostname", request.getK8S().getNodeHostname());
      name = request.getK8S().getNodeHostname();
      hostname = request.getK8S().getNodeHostname();
      ip = request.getK8S().getHostIp();
      row.put("k8s", m);
    }
    if (metaType == MetaType.VM && request.hasK8S()) {
      name = request.getK8S().getNodeHostname();
      hostname = request.getK8S().getNodeHostname();
      ip = request.getK8S().getHostIp();
    }

    String table = a.getTenant() + "_server";
    row.put("name", name);
    row.put("ip", ip);
    row.put("hostname", hostname);
    try {
      agentHeartBeatService.agentInsertOrUpdate(table, metaType, Collections.singletonList(row));
      long end = System.currentTimeMillis();
      LOGGER.info("[prod] [agentInsertOrUpdate] success row={} cost=[{}]", row, end - begin);
    } catch (Throwable e) {
      long end = System.currentTimeMillis();
      LOGGER.error("[prod] [agentInsertOrUpdate] error row={} cost=[{}]", row, end - begin, e);
      throw e;
    }
  }

  private void registerAgent0(AuthInfo ai, RegisterAgentRequest request) {
    if (request.getMode().equals(Agent.MODE_SIDECAR)) {
      notifyProd(ai, request, MetaType.VM);
    }

    String agentId = request.getAgentId();
    for (int i = 0; i < 3; i++) {
      Agent agent = agentStorage.get(agentId);
      boolean insert = agent == null;
      if (agent == null) {
        GaeaAgentDO gado =
            mapper.selectOneByExampleWithBLOBs(GaeaAgentDOExample.newAndCreateCriteria() //
                .andAgentIdEqualTo(agentId) //
                .example());
        if (gado != null) {
          insert = false;
        }
      }
      if (insert) {
        // TODO 代码重复

        GaeaAgentDO ga = new GaeaAgentDO();
        setAgentToDO(ai, request, ga);

        try {
          // 顺便 立即加入到内存里
          mapper.insert(ga);
          // 理论上要再广播一下整个集群, 否则 agent 第一次注册上来之后我们不认识它
          EventBusHolder.INSTANCE.post(new AgentSyncer.AgentInsertEvent(ga));
          return;
        } catch (DuplicateKeyException e) {
          // 临时失败 重试一下就好了
        }
      } else {
        GaeaAgentDO ga =
            mapper.selectOneByExampleWithBLOBs(GaeaAgentDOExample.newAndCreateCriteria() //
                .andAgentIdEqualTo(agentId) //
                .example());//

        AgentJson newState = buildAgentJson(request);
        if (agent != null) {
          newState.setConnectionInfo(agent.getJson().getConnectionInfo());
        }

        AgentJson dbState = JsonUtils.fromJson(ga.getJson(), AgentJson.class);

        if (!dbState.equals(newState)) {
          ga.setTenant(ai.getTenant());
          ga.setStatus(0);
          // 检查是否有实质变化, 如果没有实质变化则转成心跳
          ga.setJson(JsonUtils.toJson(newState));
          ga.setGmtModified(new Date());
          mapper.updateByPrimaryKeySelective(ga, //
              GaeaAgentDO.Column.gmtModified, //
              GaeaAgentDO.Column.tenant, //
              GaeaAgentDO.Column.status, //
              GaeaAgentDO.Column.json); //
          EventBusHolder.INSTANCE.post(new AgentSyncer.AgentUpdateEvent(ga));
          return;
        } else {
          updateAgentHeartbeat(ai, agentId);
          return;
        }
      }
    }
  }

  private void setAgentToDO(AuthInfo ai, RegisterAgentRequest request, GaeaAgentDO ga) {
    Date now = new Date();
    ga.setGmtCreate(now);
    ga.setGmtModified(now);
    ga.setTenant(ai.getTenant());
    ga.setAgentId(request.getAgentId());
    ga.setStatus(0); //

    AgentJson json = buildAgentJson(request);

    ga.setJson(JsonUtils.toJson(json));
  }

  private AgentJson buildAgentJson(RegisterAgentRequest request) {
    AgentJson json = new AgentJson();
    json.setIp(request.getIp());
    json.setHostname(request.getHostname());
    json.setOs(request.getOs());
    json.setArch(request.getArch());
    json.getLabels().putAll(request.getLabelsMap());
    json.setVersion(request.getAgentVersion());
    json.setApp(request.getApp());

    if (request.hasK8S()) {
      json.setK8s(convertToModel(request.getK8S()));
    }
    json.setMode(request.getMode());

    // ConnectionInfo ci = new ConnectionInfo();
    // ci.setRegistry(NetUtils.getLocalIp());
    // json.setConnectionInfo(ci);

    return json;
  }

  public void updateAgentHeartbeat(AuthInfo a, String agentId) {
    notifyProd(a, agentId);
    Agent agent = agentStorage.get(agentId);
    if (agent != null) {
      agentStorage.putConnecting(agentId, NetUtils.getLocalIp());
      bp.tryAdd(agentId);
    }
  }

  /**
   * 标记删除
   */
  @Transactional
  public synchronized void markDeleteExpiredAgents() {
    // 删除过期 agents 数据
    Date expiredDate =
        new Date(System.currentTimeMillis() - agentConfig.getMaintain().getExpire().toMillis());
    GaeaAgentDOExample example = GaeaAgentDOExample.newAndCreateCriteria() //
        .andGmtModifiedLessThan(expiredDate) //
        .andStatusEqualTo(0) //
        .example(); //

    // 标记删除
    GaeaAgentDO record = new GaeaAgentDO();
    record.setGmtModified(new Date());
    record.setStatus(1);
    int count = mapper.updateByExampleSelective(record, example, //
        GaeaAgentDO.Column.status, GaeaAgentDO.Column.gmtModified);
    LOGGER.info("mark delete expired agents, count=[{}]", count);

    markDeleteExpiredAgentsForDaemonAgent();
  }

  private void markDeleteExpiredAgentsForDaemonAgent() {
    // TODO daemonset 过期更快一些
    List<String> expiredAgentIds = new ArrayList<>();
    Date expiredDate = new Date(System.currentTimeMillis() - 5 * 60 * 1000L);
    for (Agent a : agentStorage.readonlyList()) {
      String mode = a.getJson().getMode();
      if (Agent.MODE_DAEMONSET.equals(mode) || Agent.MODE_CENTRAL.equals(mode)
          || Agent.MODE_CLUSTERAGENT.equals(mode)) {
        if (a.getLastHeartbeat().before(expiredDate)) {
          expiredAgentIds.add(a.getId());
        }
      }
    }

    if (expiredAgentIds.size() > 0) {
      GaeaAgentDO record = new GaeaAgentDO();
      record.setGmtModified(new Date());
      record.setStatus(1);
      GaeaAgentDOExample example =
          GaeaAgentDOExample.newAndCreateCriteria().andAgentIdIn(expiredAgentIds).example();
      int count = mapper.updateByExampleSelective(record, example, //
          GaeaAgentDO.Column.status, GaeaAgentDO.Column.gmtModified);
      LOGGER.info("mark delete expired daemonset agents, count=[{}], agentIds={}", count,
          expiredAgentIds);

    }
  }

  /**
   * 彻底删除
   */
  public void deleteDeletedAgents() {
    // 删除过期agents数据
    Date date =
        new Date(System.currentTimeMillis() - agentConfig.getMaintain().getExpire().toMillis());
    GaeaAgentDOExample example = GaeaAgentDOExample.newAndCreateCriteria() //
        .andGmtModifiedLessThan(date) //
        .andStatusEqualTo(1) //
        .example(); //

    int count = mapper.deleteByExample(example);
    LOGGER.info("delete expired agents, count=[{}]", count);
  }

  public Object getDebugInfo(String a) {
    Agent agent = agentStorage.get(a);
    if (agent == null) {
      return null;
    }
    Map<String, Object> map = Maps.newLinkedHashMapWithExpectedSize(2);
    map.put("agent", agent);
    QueryExample qe = new QueryExample();
    qe.getParams().put("agentId", a);
    map.put("dims", dataClientService.queryByExample(agent.getTenant() + "_server", qe));
    String owner = agent.getJson().internalGetConnectingRegistry();
    map.put("owner", owner);
    return map;
  }

  /**
   * 通知集群 agentId 正在和当前 registry 建连
   *
   * @param agentId
   */
  public void notifyBiStream(String agentId) {
    notifyBiStream(agentId, 0);
  }

  private void notifyBiStream(String agentId, int retry) {
    Agent agent = agentStorage.get(agentId);
    if (agent == null) {
      // maybe retry will success
      if (retry >= 10) {
        LOGGER.error("update BiStream but agent not in storage, agent=[{}]", agentId);
        return;
      }
      LOGGER.warn("update BiStream but agent not in storage, will retry, agent=[{}]", agentId);
      commonThreadPools.getScheduler().schedule(() -> {
        notifyBiStream(agentId, retry + 1);
      }, 6, TimeUnit.SECONDS);
      return;
    }
    ConnectionInfo ci = agent.getJson().getConnectionInfo();
    if (ci != null && ci.getRegistry().equals(NetUtils.getLocalIp())) {
      return;
    }

    ci = new ConnectionInfo();
    ci.setRegistry(NetUtils.getLocalIp());
    agent.getJson().setConnectionInfo(ci);

    GaeaAgentDO ga = new GaeaAgentDO();
    ga.setAgentId(agentId);
    // 检查是否有实质变化, 如果没有实质变化则转成心跳

    ga.setJson(JsonUtils.toJson(agent.getJson()));

    ga.setGmtModified(new Date());

    commonThreadPools.getIo().execute(() -> {
      mapper.updateByExampleSelective(ga, //
          GaeaAgentDOExample.newAndCreateCriteria().andAgentIdEqualTo(agentId).example(), //
          GaeaAgentDO.Column.gmtModified, //
          GaeaAgentDO.Column.json); //
    });
  }

  private static AgentK8sConfig convertToModel(AgentK8sInfo pb) {
    AgentK8sConfig c = new AgentK8sConfig();
    c.setHostIP(pb.getHostIp());
    c.setNamespace(pb.getNamespace());
    c.setPod(pb.getPod());
    c.setNodeHostname(pb.getNodeHostname());
    return c;
  }
}
