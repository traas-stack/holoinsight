/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.core.service.bitmap;

import io.holoinsight.server.meta.core.common.DimForkJoinPool;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.time.StopWatch;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 某个维度的所有数据 基于 bitmap 构建索引
 *
 */
@Data
@Slf4j
public abstract class AbstractMetaData implements IMetaData {

  /**
   * changelog积压比，超过会将数据置为过期
   */
  private static final int STOCK_LOG_FACTOR = 2;

  /**
   * 维表名称
   */
  protected final String tableName;

  /**
   * 数据构建时间
   */
  protected long buildTime;

  /**
   * 有效时间
   */
  protected long ttl;

  protected long version;

  /**
   * 数据是否过期
   */
  protected boolean expired;

  protected Metasynchronizer synchronizer;

  private AtomicBoolean synchronizing = new AtomicBoolean(false);

  private ScheduledExecutorService SCHEDULER;

  /**
   * 所有行 ukVal -> row
   */
  protected Map<String, MetaDataRow> rowsMap = new ConcurrentHashMap<>();

  /**
   * 所有行 id -> row
   */
  protected Map<Long, MetaDataRow> idRowsMap = new ConcurrentHashMap<>();

  /**
   * 所有列 colName -> colData
   */
  protected Map<String, MetaColData> colsMap = new ConcurrentHashMap<>();

  /**
   * 所有变更信息
   */
  protected List<MetaDataRow> allLogs = new ArrayList<>();

  public AbstractMetaData(String tableName, long ttl, long version, List<MetaDataRow> rows,
      Metasynchronizer synchronizer) {
    this.tableName = tableName;
    this.version = version;
    this.ttl = ttl - new Random().nextInt((int) (ttl / 2));
    this.synchronizer = synchronizer;
    StopWatch stopWatch = StopWatch.createStarted();
    try {
      Map<String, MetaDataRow> newRowsMap = new ConcurrentHashMap<>();
      Map<Long, MetaDataRow> newIdRowsMap = new ConcurrentHashMap();
      DimForkJoinPool.get()
          .submit(() -> rows.parallelStream().forEach(row -> newRowsMap.put(row.getUk(), row)))
          .join();
      DimForkJoinPool.get()
          .submit(() -> rows.parallelStream().forEach(row -> newIdRowsMap.put(row.getId(), row)))
          .join();
      Map<String, MetaColData> initColsMap = new ConcurrentHashMap<>();
      fillColsMapWithLabels(initColsMap, rows);
      synchronized (this) {
        this.rowsMap = newRowsMap;
        this.idRowsMap = newIdRowsMap;
        this.colsMap = initColsMap;
        this.allLogs = new ArrayList<>(rows);
      }
      log.info("META-STAT,REPLACE,SUCCESS,{},table={},size={}.", stopWatch.getTime(), tableName,
          rows.size());
    } catch (Exception e) {
      log.error("META-STAT,REPLACE,FAIL,{},table={},size={},msg={}.", stopWatch.getTime(),
          tableName, rows.size(), e.getMessage(), e);
      throw new RuntimeException(e);
    }
    this.SCHEDULER = new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
        .namingPattern("meta-sync-" + this.tableName + "-" + this.buildTime + "-%d").build());
  }

  public void startSync() {
    if (synchronizing.compareAndSet(false, true)) {
      this.SCHEDULER.scheduleAtFixedRate(this::doSync, 0, 5, TimeUnit.SECONDS);
    }
  }

  public void stopSync() {
    if (synchronizing.compareAndSet(true, false)) {
      this.SCHEDULER.shutdown();
    }
  }

  private void doSync() {
    long newVersion = System.currentTimeMillis() / 1000 * 1000 - syncDelay();
    try {
      // 增量更新
      log.info("[META-DATA-SYNC] sync start, table={}, currentVersion={}, newVersion={}.",
          tableName, this.getVersion(), newVersion);
      if (newVersion > this.getVersion()) {
        List<MetaDataRow> changeRows =
            this.synchronizer.sync(tableName, this.getVersion(), newVersion);
        long realVersion = this.getVersion();
        if (!changeRows.isEmpty()) {
          realVersion = changeRows.stream().map(MetaDataRow::getGmtModified)
              .max(Comparator.comparingLong(l -> l)).get() + 1;
        }
        if (realVersion > this.getVersion()) {
          this.merge(realVersion, changeRows);
        }
        log.info(
            "[META-DATA-SYNC] sync success, table={}, currentVersion={}, newVersion={}, realVersion={}, size={}.",
            tableName, this.getVersion(), newVersion, realVersion, changeRows.size());
      }
    } catch (Exception e) {
      log.error(
          "[META-DATA-SYNC] sync fail and keep version try next time, table={}, currentVersion={}, newVersion={}, msg={}.",
          tableName, this.getVersion(), newVersion, e.getMessage(), e);
    }
  }

  /**
   * 延迟获取时间，防止对端数据还未准备好
   */
  protected abstract long syncDelay();

  @Override
  public Collection<MetaDataRow> allRows() {
    return rowsMap.values();
  }

  @Override
  public Collection<MetaColData> allCols() {
    return colsMap.values();
  }

  @Override
  public void merge(long version, List<MetaDataRow> changeLogs) {
    if (version > this.version) {
      if (CollectionUtils.isEmpty(changeLogs)) {
        setVersion(version);
        return;
      }
      synchronized (this) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
          mergeRows(changeLogs);
          mergeCols(changeLogs);
          stopWatch.stop();
          log.info(
              "META-STAT,MERGE,SUCCESS,{},table={},oldVersion={},currentVersion={},size={},rows={},cols={}.",
              stopWatch.getTime(), tableName, this.version, version,
              CollectionUtils.size(changeLogs), CollectionUtils.size(rowsMap),
              CollectionUtils.size(colsMap));
          this.setVersion(version);
        } catch (Exception e) {
          log.error(
              "META-STAT,MERGE,FAIL,{},table={},mergeVersion={},currentVersion={},size={},rows={},cols={},msg={}.",
              stopWatch.getTime(), tableName, version, this.version,
              CollectionUtils.size(changeLogs), CollectionUtils.size(rowsMap),
              CollectionUtils.size(colsMap), e.getMessage(), e);
          throw new RuntimeException(e);
        }
      }
    } else {
      log.warn("META-STAT,MERGE,SKIP,table={},mergeVersion={},currentVersion={}.", tableName,
          version, this.version);
    }
  }

  @Override
  public MetaDataRow getByUk(Object pk) {
    return rowsMap.get(pk);
  }

  @Override
  public MetaDataRow getById(long id) {
    return idRowsMap.get(id);
  }

  @Override
  public List<MetaDataRow> getByPks(List<String> pks) {
    if (CollectionUtils.isEmpty(pks)) {
      return Collections.emptyList();
    }
    return pks.stream().map(this::getByUk).filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public List<MetaDataRow> getByIds(List<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Collections.emptyList();
    }
    return ids.stream().map(this::getById).filter(Objects::nonNull).collect(Collectors.toList());
  }

  protected void mergeRows(List<MetaDataRow> changeLogs) {
    allLogs.addAll(changeLogs);
    changeLogs.forEach(changeLog -> {
      boolean deleted = changeLog.getDeleted();
      if (deleted) {
        deleteOneRow(changeLog);
      } else {
        addOneRow(changeLog);
      }
    });
  }

  private void mergeCols(List<MetaDataRow> changeLogs) {
    fillColsMapWithLabels(this.colsMap, changeLogs);
    appendLogsOnCols(this.colsMap, changeLogs);
  }

  private void appendLogsOnCols(Map<String, MetaColData> colsMap, List<MetaDataRow> changeLogs) {
    if (colsMap != null && changeLogs != null) {
      DimForkJoinPool.get()
          .submit(() -> colsMap.values().parallelStream()
              .filter(metaColData -> metaColData.getLoaded().get())
              .forEach(metaColData -> metaColData.appendLogs(changeLogs)))
          .join();
    }
  }

  protected void addOneRow(MetaDataRow addLog) {
    String addUkVal = addLog.getUk();
    rowsMap.put(addUkVal, addLog);
    idRowsMap.put(addLog.getId(), addLog);
  }

  protected void deleteOneRow(MetaDataRow deleteLog) {
    String deleteUkVal = deleteLog.getUk();
    rowsMap.remove(deleteUkVal);
    idRowsMap.remove(deleteLog.getId());
  }

  private void fillColsMapWithLabels(Map<String, MetaColData> colsMap, List<MetaDataRow> rows) {
    DimForkJoinPool.get().submit(() -> rows.parallelStream().forEach(row -> {
      Map<String, Object> labelMap = row.getValues();
      if (labelMap != null) {
        labelMap
            .forEach((k, v) -> colsMap.putIfAbsent(k, new MetaColData(this.tableName, k, this)));
      }
    })).join();
    log.info("fill cols with labels, rows={}, cols={}", CollectionUtils.size(rows),
        CollectionUtils.size(this.colsMap));
  }

  public boolean isExpired() {
    if (this.expired) {
      return true;
    }
    if (timeExpired() || countExpired()) {
      this.setExpired();
    }
    return this.expired;
  }

  public void setExpired() {
    this.expired = true;
    log.info("meta data expired, table={}, buildTime={}, ttl={}, rowSize={}, logSize={}", tableName,
        buildTime, ttl, rowsMap.size(), this.allLogs.size());
    this.stopSync();

  }

  private boolean timeExpired() {
    return System.currentTimeMillis() - buildTime > ttl;
  }

  private boolean countExpired() {
    return this.allLogs.size() > rowsMap.size() * STOCK_LOG_FACTOR;
  }

}
