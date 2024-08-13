/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.fastjson.JSON;

import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.agg.v1.core.mapper.AggTaskV1DOUtils;
import io.holoinsight.server.common.dao.entity.AggTaskV1DO;
import io.holoinsight.server.common.dao.entity.AggTaskV1DOExample;
import io.holoinsight.server.common.dao.mapper.AggTaskV1DOMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/26
 *
 * @author xzchaoo
 */
@Slf4j
public class AggTaskV1Syncer2 implements InitializingBean {
  private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private AggTaskV1StorageForDispatcher aggTaskV1Storage;

  @Autowired
  private AggTaskV1DOMapper mapper;

  private Date lastEnd;

  @Scheduled(initialDelay = 60_000L, fixedDelay = 60_000L)
  public synchronized void fullSync() {
    try {
      fullSync0(false);
    } catch (Exception e) {
      log.error("[aggtask] full sync error", e);
    }
  }

  @Scheduled(initialDelay = 10000L, fixedDelay = 10000L)
  public synchronized void deltaSync() {
    try {
      deltaSync0();
    } catch (Exception e) {
      log.error("[aggtask] sync error", e);
    }
  }

  private void fullSync0(boolean init) {
    long begin = System.currentTimeMillis();

    AggTaskV1DOExample example = AggTaskV1DOExample.newAndCreateCriteria() //
        .andDeletedEqualTo(0) //
        .example();
    List<AggTaskV1DO> list = mapper.selectByExampleWithBLOBs(example);

    Map<String, AggTask> old = aggTaskV1Storage.getState().aggTasks;

    List<AggTask> aggTasks = new ArrayList<>(list.size());
    int add = 0;
    int update = 0;
    for (AggTaskV1DO task : list) {
      AggTask aggTask = old.get(task.getAggId());
      if (aggTask != null) {
        aggTask.epoch = begin;
        if (aggTask.getVersion() == task.getVersion()) {
          aggTasks.add(aggTask);
          continue;
        }
        ++update;
      }
      ++add;

      aggTask = JSON.parseObject(task.getJson(), AggTask.class);
      aggTask.setAggId(task.getAggId());
      aggTask.setVersion(task.getVersion());
      aggTask.epoch = begin;
      aggTasks.add(aggTask);
    }
    long del = old.values().stream().filter(x -> x.epoch != begin).count();


    if (add + update + del > 0) {
      aggTaskV1Storage.replace(aggTasks);
    }
    long cost = System.currentTimeMillis() - begin;

    if (init) {
      log.info("[aggtask] init full sync, add=[{}] total=[{}] cost=[{}]", //
          add, aggTaskV1Storage.size(), cost);
    } else {
      log.info("[aggtask] full sync, add=[{}] update=[{}] del=[{}] total=[{}] cost=[{}]", //
          add, update, del, aggTaskV1Storage.size(), cost);
    }
  }

  private void deltaSync0() {
    long time0 = System.currentTimeMillis();

    Date begin = lastEnd;
    Date end = new Date(time0 - 10_000L);
    lastEnd = end;

    if (!begin.before(end)) {
      return;
    }

    Map<String, AggTaskV1DOUtils.Delta> deltaMap =
        AggTaskV1DOUtils.selectDeltas(mapper, begin, end);

    int add = 0;
    int del = 0;

    Map<String, AggTask> aggTasks = new HashMap<>(aggTaskV1Storage.getState().aggTasks);
    for (AggTaskV1DOUtils.Delta d : deltaMap.values()) {
      switch (d.add.size()) {
        case 0:
          // del
          aggTasks.remove(d.aggId);
          log.info("[aggtask] [sync] remove {}", d.aggId);
          ++del;
          break;
        case 1:
          AggTask aggTask = JSON.parseObject(d.add.get(0).getJson(), AggTask.class);
          aggTasks.put(d.aggId, aggTask);
          log.info("[aggtask] [sync] add [{}/{}]", d.aggId, d.add.get(0).getVersion());
          ++add;
          break;
        default:
          log.info("[aggtask] [sync] invalid {}", d);
          // invalid
          break;
      }
    }

    aggTaskV1Storage.replace(aggTasks);
    long cost = System.currentTimeMillis() - time0;
    log.info("[aggtask] sync once, add=[{}] del=[{}] cost=[{}]", add, del, cost);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    lastEnd = new Date();
    fullSync0(true);
  }
}
