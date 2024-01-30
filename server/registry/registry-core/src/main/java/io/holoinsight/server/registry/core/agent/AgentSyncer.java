/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.PreDestroy;

import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.dao.entity.GaeaAgentDO;
import io.holoinsight.server.common.dao.entity.GaeaAgentDOExample;
import io.holoinsight.server.common.dao.mapper.GaeaAgentDOMapper;
import io.holoinsight.server.registry.core.utils.BoundedSchedulers;
import io.holoinsight.server.registry.core.utils.Dict;
import io.holoinsight.server.common.event.EventBusHolder;
import lombok.Data;
import reactor.core.publisher.Mono;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.xzchaoo.commons.basic.concurrent.DynamicScheduledExecutorService;
import com.xzchaoo.commons.basic.concurrent.OneThreadFactory;
import com.xzchaoo.commons.basic.dispose.Disposable;

/**
 * <p>
 * created at 2022/3/9
 *
 * @author zzhb101
 */
@Component
public class AgentSyncer {
  private static final int STOP_TIMEOUT_SECONDS = 3;

  private static final Logger LOGGER = LoggerFactory.getLogger("AGENT");
  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  @Autowired
  private GaeaAgentDOMapper mapper;
  @Autowired
  private AgentStorage agentStorage;
  private Disposable syncDisposable;

  private Date begin;
  @Autowired
  private AgentConfig agentConfig;
  private ScheduledThreadPoolExecutor scheduler;
  private Disposable fullSyncDisposable;

  // TODO 做成事件
  @Autowired
  private CentralAgentService centralAgentService;
  @Autowired
  private DaemonsetAgentService daemonsetAgentService;

  private Lock lock = new ReentrantLock();

  public Mono<Void> initLoad() {
    return Mono.<Void>fromRunnable(this::initLoad0).subscribeOn(BoundedSchedulers.BOUNDED);
  }

  public void startSync() {
    scheduler = new ScheduledThreadPoolExecutor(1, new OneThreadFactory("agent-syncer"));
    scheduler.setRemoveOnCancelPolicy(true);

    syncDisposable = DynamicScheduledExecutorService.wrap(scheduler).dynamic(this::syncOnce, //
        agentConfig.getSync().getInterval().plus(agentConfig.getSync().getDelay()), //
        () -> agentConfig.getSync().getInterval()); //

    fullSyncDisposable = DynamicScheduledExecutorService.wrap(scheduler).dynamic(this::fullSync, //
        agentConfig.getSync().getFullInterval(), //
        () -> agentConfig.getSync().getFullInterval());

    EventBusHolder.INSTANCE.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onAgentInsert(AgentInsertEvent e) {
    lock.lock();
    try {
      Agent a = toAgent(e.ga);
      agentStorage.put(a);
    } finally {
      lock.unlock();
    }
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onAgentUpdate(AgentUpdateEvent e) {
    lock.lock();
    try {
      Agent a = toAgent(e.ga);
      agentStorage.put(a);
    } finally {
      lock.unlock();
    }
  }

  /**
   * 全量同步
   */
  private void fullSync() {
    lock.lock();
    try {
      fullSync0();
    } finally {
      lock.unlock();
    }
  }

  private void fullSync0() {
    // TODO 实现全量同步
    // TODO 如何感知删除?

    long begin = System.currentTimeMillis();
    GaeaAgentDOExample example = GaeaAgentDOExample.newAndCreateCriteria() //
        .andStatusEqualTo(0) //
        .example(); //

    List<GaeaAgentDO> gas = mapper.selectByExampleWithBLOBs(example);
    Set<String> keeps = Sets.newHashSetWithExpectedSize(gas.size());
    int add = 0;
    for (GaeaAgentDO ga : gas) {
      keeps.add(ga.getAgentId());
      try {
        if (agentStorage.get(ga.getAgentId()) == null) {
          ++add;
        }
        Agent a = toAgent(ga);
        agentStorage.put(a);
      } catch (Throwable e) {
        LOGGER.error("parse agent error agentId=[{}]", ga.getAgentId(), e);
      }
    }
    int del = agentStorage.keep(keeps);
    long end = System.currentTimeMillis();

    fireChange();

    LOGGER.info("[sync] [full] add=[{}] del=[{}] cost=[{}]", add, del, end - begin);
  }

  @PreDestroy
  public void preDestroy() {
    Disposable d = this.syncDisposable;
    this.syncDisposable = null;
    if (d != null) {
      d.dispose();
    }

    d = fullSyncDisposable;
    this.fullSyncDisposable = null;
    if (d != null) {
      d.dispose();
    }

    ScheduledThreadPoolExecutor scheduler = this.scheduler;
    if (scheduler != null) {
      scheduler.shutdownNow();
      try {
        if (!scheduler.awaitTermination(STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
          LOGGER.warn("agent syncer scheduler stop timeout");
        }
        // else 正常结束
      } catch (InterruptedException e) {
        LOGGER.warn("thread interrupted when waiting scheduler to stop");
      }
    }
  }

  private void syncOnce() {
    long nowMs = System.currentTimeMillis();
    AgentConfig.Sync sync = agentConfig.getSync();
    // 解释一下end:
    Date end = new Date(nowMs + sync.getDelay().toMillis());
    Date end2 = new Date(nowMs - sync.getDelay().toMillis());
    lock.lock();
    try {
      syncOnce0(begin, end);
      fireChange();
      begin = end2;
    } catch (Throwable e) {
      LOGGER.error("sync error", e);
    } finally {
      lock.unlock();
    }
  }

  private void syncOnce0(Date begin, Date end) {
    GaeaAgentDOExample example = GaeaAgentDOExample.newAndCreateCriteria() //
        .andGmtModifiedGreaterThanOrEqualTo(begin) //
        .andGmtModifiedLessThan(end) //
        .example(); //

    long dbBegin = System.currentTimeMillis();
    List<GaeaAgentDO> gas = mapper.selectByExampleWithBLOBs(example);
    long dbEnd = System.currentTimeMillis();
    if (gas.isEmpty()) {
      synchronized (SDF) {
        LOGGER.info("delta sync success, range=[{}, {}) empty, dbCost=[{}]", //
            SDF.format(begin), //
            SDF.format(end), //
            dbEnd - dbBegin);
      }
      return;
    }

    int add = 0, update = 0, del = 0;

    for (GaeaAgentDO ga : gas) {
      if (ga.getStatus() == 1) {
        if (agentStorage.delete(ga.getAgentId()) != null) {
          ++del;
        }
      }
      try {
        Agent a = toAgent(ga);
        // TODO 检查一下有没有实质性变化
        if (agentStorage.get(ga.getAgentId()) != null) {
          ++update;
        } else {
          ++add;
        }
        agentStorage.put(a);
      } catch (Throwable e) {
        LOGGER.error("parse agent error agentId=[{}]", ga.getAgentId(), e);
      }
    }

    synchronized (SDF) {
      LOGGER.info("delta sync success, range=[{}, {}) add=[{}] update=[{}] del=[{}] dbCost=[{}]",
          SDF.format(begin), //
          SDF.format(end), add, update, del, dbEnd - dbBegin);
    }
  }

  private void initLoad0() {
    begin = new Date();

    long begin = System.currentTimeMillis();
    GaeaAgentDOExample example = GaeaAgentDOExample.newAndCreateCriteria() //
        .andStatusEqualTo(0) //
        .example(); //

    List<GaeaAgentDO> gas = mapper.selectByExampleWithBLOBs(example);
    for (GaeaAgentDO ga : gas) {
      try {
        Agent a = toAgent(ga);
        agentStorage.put(a);
      } catch (Throwable e) {
        LOGGER.error("parse agent error agentId=[{}]", ga.getAgentId(), e);
      }
    }
    long end = System.currentTimeMillis();

    fireChange();

    LOGGER.info("init load agent success=[{}] size=[{}] cost=[{}]", agentStorage.size(), gas.size(),
        end - begin);
  }

  private void fireChange() {
    centralAgentService.refresh();
    daemonsetAgentService.refresh();
  }

  private static Agent toAgent(GaeaAgentDO ga) {
    Agent a = new Agent();
    a.setId(ga.getAgentId());
    a.setTenant(Dict.get(ga.getTenant()));
    AgentJson json = JsonUtils.fromJson(ga.getJson(), AgentJson.class);
    json.reuseStrings();
    a.setJson(json);
    a.setLastHeartbeat(ga.getGmtModified());
    return a;
  }

  @Data
  public static class AgentInsertEvent {
    public final GaeaAgentDO ga;
  }

  @Data
  public static class AgentUpdateEvent {
    public final GaeaAgentDO ga;
  }
}
