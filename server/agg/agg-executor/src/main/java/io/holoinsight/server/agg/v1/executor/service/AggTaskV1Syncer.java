/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.service;

import static io.holoinsight.server.agg.v1.core.mapper.AggTaskV1DOUtils.selectDeltas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.alibaba.fastjson.JSON;

import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.agg.v1.core.mapper.AggTaskV1DOUtils;
import io.holoinsight.server.agg.v1.executor.executor.XAggTask;
import io.holoinsight.server.agg.v1.executor.executor.XParserUtils;
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
public class AggTaskV1Syncer implements InitializingBean {
  private static final SimpleDateFormat SDF = new SimpleDateFormat("HH:mm:ss");

  @Autowired
  private AggTaskV1StorageForExecutor storage;
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
    storage.setEpoch(begin);

    // only select id
    AggTaskV1DOExample example = AggTaskV1DOExample.newAndCreateCriteria() //
        .andDeletedEqualTo(0) //
        .example();
    List<AggTaskV1DO> list = mapper.selectByExampleSelective(example, AggTaskV1DO.Column.id);

    List<Long> needBlobIds = new ArrayList<>();
    for (AggTaskV1DO task : list) {
      XAggTask aggTask = storage.get(task.getId());
      if (aggTask != null) {
        aggTask.epoch = begin;
        continue;
      }
      needBlobIds.add(task.getId());
    }

    if (!needBlobIds.isEmpty()) {
      example = AggTaskV1DOExample.newAndCreateCriteria() //
          .andDeletedEqualTo(0) //
          .andIdIn(needBlobIds).example();
      list = mapper.selectByExampleWithBLOBs(example);
    } else {
      list = Collections.emptyList();
    }

    int add = 0;
    int update = 0;
    int delete = 0;

    for (AggTaskV1DO task : list) {
      XAggTask aggTask = XParserUtils.parse(JSON.parseObject(task.getJson(), AggTask.class));
      if (aggTask == null) {
        continue;
      }
      aggTask.getInner().setId(task.getId());
      aggTask.getInner().setAggId(task.getAggId());
      aggTask.getInner().setVersion(task.getVersion());
      aggTask.epoch = begin;
      if (storage.get(task.getAggId()) != null) {
        ++update;
      } else {
        add++;
      }
      storage.put(aggTask);
    }

    delete = storage.removeByEpoch(begin);

    long cost = System.currentTimeMillis() - begin;

    if (init) {
      log.info("[aggtask] init full sync, total=[{}] cost=[{}]", //
          storage.size(), cost);
    } else {
      log.info("[aggtask] full sync, add=[{}] update=[{}] deleted=[{}] total=[{}] cost=[{}]", //
          add, update, delete, storage.size(), cost);
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

    Map<String, AggTaskV1DOUtils.Delta> deltaMap = selectDeltas(mapper, begin, end);

    int add = 0;
    int update = 0;
    int del = 0;

    for (AggTaskV1DOUtils.Delta d : deltaMap.values()) {
      switch (d.add.size()) {
        case 0:
          // del
          storage.remove(d.aggId);
          log.info("[aggtask] [sync] remove {}", d.aggId);
          ++del;
          break;
        case 1:
          XAggTask aggTask =
              XParserUtils.parse(JSON.parseObject(d.add.get(0).getJson(), AggTask.class));
          if (aggTask == null) {
            if (storage.remove(d.aggId) != null) {
              ++del;
            }
            log.info("[aggtask] parse error [{}/{}]", d.aggId, d.add.get(0).getVersion());
            continue;
          }

          aggTask.getInner().setId(d.add.get(0).getId());
          aggTask.getInner().setVersion(d.add.get(0).getVersion());
          aggTask.getInner().setAggId(d.add.get(0).getAggId());

          if (storage.get(d.aggId) != null) {
            ++update;
          } else {
            ++add;
          }
          storage.put(aggTask);
          log.info("[aggtask] [sync] add [{}/{}]", d.aggId, d.add.get(0).getVersion());
          break;
        default:
          log.error("[aggtask] [sync] invalid {}", d);
          // invalid
          break;
      }
    }

    long cost = System.currentTimeMillis() - time0;
    synchronized (SDF) {
      log.info(
          "[aggtask] delta sync once, [{}, {}), add=[{}] update=[{}] del=[{}] read=[{}] cost=[{}]", //
          SDF.format(begin), SDF.format(end), //
          add, update, del, deltaMap.size(), cost);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    lastEnd = new Date();
    fullSync0(true);
  }

}
