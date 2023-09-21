/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.master;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.holoinsight.server.common.JsonUtils;
import io.holoinsight.server.common.dao.entity.GaeaLockDO;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.registry.core.lock.LockService;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2022/3/12
 *
 * @author zzhb101
 */
@Service
@Slf4j
public class MasterService {
  @Autowired
  private LockService lockService;
  private List<RegisterRecord> records = new ArrayList<>();

  @Autowired
  private CommonThreadPools commonThreadPools;
  private ScheduledFuture<?> future;

  @PostConstruct
  public void start() {
    ScheduledExecutorService scheduler = commonThreadPools.getScheduler();
    future = scheduler.scheduleWithFixedDelay(this::refresh0, 5, 5, TimeUnit.SECONDS);
  }

  @PreDestroy
  public void stop() {
    ScheduledFuture<?> future = this.future;
    this.future = null;
    if (future != null) {
      future.cancel(true);
    }
  }

  /**
   * @param tenant master tenant
   * @param name master name
   * @param myId id for current instance
   * @param listener
   * @return
   */
  public synchronized RegisterRecord register(String tenant, String name, String myId,
      String payload, MasterListener listener) {
    RegisterRecord rr = new RegisterRecord();
    rr.tenant = tenant;
    rr.name = name;
    rr.myId = myId;
    rr.listener = listener;
    rr.myJson = new MasterJson(myId, payload);

    records.add(rr);
    refresh0(rr, 0);
    return rr;
  }

  private synchronized void refresh0() {
    for (RegisterRecord rr : records) {
      try {
        refresh0(rr, 0);
      } catch (Exception e) {
        log.error("[master] refresh error, tenant=[{}] master=[{}] id=[{}]", rr.tenant, rr.name,
            rr.myId, e);
      }
    }
  }

  private MasterJson getMasterJson(String tenant, String lockMasterName) {
    // lock error, maybe locked by other
    GaeaLockDO lockDO = lockService.getLockDO(tenant, lockMasterName);
    if (lockDO != null && lockDO.getStatus() == LockService.STATUS_LOCKED) {
      return JsonUtils.fromJson(lockDO.getJson(), MasterJson.class);
    }

    return null;
  }

  private synchronized void refresh0(RegisterRecord rr, int retry) {
    if (retry > 3) {
      throw new IllegalStateException("fail to refresh master");
    }

    String lockMasterName = "master_" + rr.name;

    if (rr.lock == null) {

      LockService.Lock lock =
          lockService.tryLock(rr.tenant, lockMasterName, JsonUtils.toJson(rr.myJson), 30_000L);
      if (lock != null) {
        // lock success
        rr.lock = lock;
        MasterJson old = rr.master;
        rr.master = rr.myJson;
        rr.listener.onEnter(rr.myJson);
        rr.listener.onChange(old, rr.master);
        return;
      }

      MasterJson master = getMasterJson(rr.tenant, lockMasterName);
      if (master == null || rr.myId.equals(master.id)) {
        refresh0(rr, retry + 1);
        return;
      }

      MasterJson old = rr.master;
      if (old == null || !old.id.equals(master.id)) {
        rr.master = master;
        rr.listener.onChange(old, master);
      }
    } else {
      if (!rr.lock.touch()) {
        MasterJson master = getMasterJson(rr.tenant, lockMasterName);

        rr.lock = null;
        rr.master = null;
        rr.listener.onLeave(rr.myJson);
        if (master == null || rr.myId.equals(master.id)) {
          rr.listener.onChange(rr.myJson, null);
          refresh0(rr, retry + 1);
          return;
        }

        rr.master = master;
        rr.listener.onChange(rr.myJson, master);
      }
    }
  }

  public synchronized void unregister(RegisterRecord rr) {
    if (records.remove(rr)) {
      if (rr.isMaster(rr.master) && rr.lock != null) {
        rr.lock.unlock();
        MasterJson old = rr.master;
        rr.listener.onLeave(old);
        rr.listener.onChange(old, null);
      }
    }
  }

  public static class RegisterRecord {
    MasterListener listener;
    private String tenant;
    private String name;
    private String myId;
    private MasterJson myJson;

    private MasterJson master;
    private LockService.Lock lock;

    void updateMaster(MasterJson mj) {
      MasterJson old = this.master;
      this.master = mj;

      if (myId.equals(mj.id)) {
        listener.onEnter(mj);
      }

      if (old != null && !old.id.equals(mj.id)) {
        if (old.id.equals(myId)) {
          listener.onLeave(mj);
        }
        listener.onChange(old, mj);
      }
    }

    boolean isMaster(MasterJson mj) {
      return mj != null && myId.equals(mj.id);
    }

    public boolean isMaster() {
      return isMaster(master);
    }
  }

}
