/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package io.holoinsight.server.meta.core.service.bitmap;

import io.holoinsight.server.meta.core.common.DimForkJoinPool;
import lombok.Getter;
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
 * 某个维度的所有数据 基于列式
 *
 * @author wanpeng.xwp
 * @version : DimData.java,v 0.1 2019年12月06日 16:31 wanpeng.xwp Exp $
 */
@Getter
@Slf4j
public abstract class AbstractDimData implements DimData {

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
   * 所有行 pkVal -> row
   */
  protected Map<Object, DimDataRow> rowsMap = new ConcurrentHashMap<>();

  /**
   * 所有行 id -> row
   */
  protected Map<Long, DimDataRow> idRowsMap = new ConcurrentHashMap<>();

  /**
   * 所有列 colName -> colData
   */
  protected Map<String, DimColData> colsMap = new ConcurrentHashMap<>();

  /**
   * 所有变更信息
   */
  protected List<DimDataRow> allLogs = new ArrayList<>();

  public AbstractDimData(String tableName, long ttl, long version, List<DimDataRow> rows,
      Metasynchronizer synchronizer) {
    this.tableName = tableName;
    this.version = version;
    this.ttl = ttl - new Random().nextInt((int) (ttl / 2));
    this.synchronizer = synchronizer;
    StopWatch stopWatch = StopWatch.createStarted();
    try {
      Map<Object, DimDataRow> newRowsMap = new ConcurrentHashMap<>();
      Map<Long, DimDataRow> newIdRowsMap = new ConcurrentHashMap();
      DimForkJoinPool.get()
          .submit(() -> rows.parallelStream().forEach(row -> newRowsMap.put(row.getUk(), row)))
          .join();
      DimForkJoinPool.get()
          .submit(() -> rows.parallelStream().forEach(row -> newIdRowsMap.put(row.getId(), row)))
          .join();
      Map<String, DimColData> initColsMap = new ConcurrentHashMap<>();
      fillColsMapWithLabels(initColsMap, rows);
      synchronized (this) {
        this.rowsMap = newRowsMap;
        this.idRowsMap = newIdRowsMap;
        this.colsMap = initColsMap;
        this.allLogs = new ArrayList<>(rows);
      }
      log.info("DIMSERVICE-STAT,REPLACE,SUCCESS,{},table={},size={}.", stopWatch.getTime(),
          tableName, rows.size());
    } catch (Exception e) {
      log.error("DIMSERVICE-STAT,REPLACE,FAIL,{},table={},size={},msg={}.", stopWatch.getTime(),
          tableName, rows.size(), e.getMessage(), e);
      throw new RuntimeException(e);
    }
    this.SCHEDULER = new ScheduledThreadPoolExecutor(1,
        new BasicThreadFactory.Builder().namingPattern("meta-sync-" + tableName + "-%d").build());
  }

  public void startSync() {
    if (synchronizing.compareAndSet(false, true)) {
      this.SCHEDULER.scheduleAtFixedRate(this::doSync, 0, 5, TimeUnit.SECONDS);
    }
  }

  public void stopSync() {
    synchronizing.compareAndSet(true, false);
  }

  private void doSync() {
    long newVersion = System.currentTimeMillis() / 1000 * 1000 - syncDelay();
    try {
      // 增量更新
      log.info("[DIM-DATA-SYNC] sync start, table={}, currentVersion={}, newVersion={}.", tableName,
          this.getVersion(), newVersion);
      if (newVersion > this.getVersion()) {
        List<DimDataRow> changeRows =
            this.synchronizer.sync(tableName, this.getVersion(), newVersion);
        long realVersion = this.getVersion();
        if (!changeRows.isEmpty()) {
          realVersion = changeRows.stream().map(DimDataRow::getGmtModified)
              .max(Comparator.comparingLong(l -> l)).get();
        }
        if (realVersion > this.getVersion()) {
          this.merge(realVersion, changeRows);
        }
        log.info(
            "[DIM-DATA-SYNC] sync success, table={}, currentVersion={}, newVersion={}, realVersion={}, size={}.",
            tableName, this.getVersion(), newVersion, realVersion, changeRows.size());
      }
    } catch (Exception e) {
      log.error(
          "[DIM-DATA-SYNC] sync fail and keep version try next time, table={}, currentVersion={}, newVersion={}, msg={}.",
          tableName, this.getVersion(), newVersion, e.getMessage(), e);
    }
  }

  /**
   * 延迟获取时间，防止对端数据还未准备好
   */
  protected abstract long syncDelay();

  @Override
  public Collection<DimDataRow> allRows() {
    return rowsMap.values();
  }

  @Override
  public Collection<DimColData> allCols() {
    return colsMap.values();
  }

  @Override
  public void merge(long version, List<DimDataRow> changeLogs) {
    if (version > this.version) {
      if (CollectionUtils.isEmpty(changeLogs)) {
        return;
      }
      synchronized (this) {
        StopWatch stopWatch = StopWatch.createStarted();
        try {
          mergeRows(changeLogs);
          mergeCols(changeLogs);
          stopWatch.stop();
          log.info("DIMSERVICE-STAT,MERGE,SUCCESS,{},table={},size={}.", stopWatch.getTime(),
              tableName, changeLogs.size());
        } catch (Exception e) {
          log.error("DIMSERVICE-STAT,MERGE,FAIL,{},table={},size={},msg={}.", stopWatch.getTime(),
              tableName, changeLogs.size(), e.getMessage(), e);
          throw new RuntimeException(e);
        }
      }
    } else {
      log.warn("DIMSERVICE-STAT,MERGE,SKIP,table={},mergeVersion={},currentVersion={}.", tableName,
          version, this.version);
    }
  }

  @Override
  public DimDataRow getByUk(Object pk) {
    return rowsMap.get(pk);
  }

  @Override
  public DimDataRow getById(long id) {
    return idRowsMap.get(id);
  }

  @Override
  public List<DimDataRow> getByPks(List<String> pks) {
    if (CollectionUtils.isEmpty(pks)) {
      return Collections.emptyList();
    }
    return pks.stream().map(this::getByUk).filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  public List<DimDataRow> getByIds(List<Long> ids) {
    if (CollectionUtils.isEmpty(ids)) {
      return Collections.emptyList();
    }
    return ids.stream().map(this::getById).filter(Objects::nonNull).collect(Collectors.toList());
  }

  protected void mergeRows(List<DimDataRow> changeLogs) {
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

  private void mergeCols(List<DimDataRow> changeLogs) {
    fillColsMapWithLabels(this.colsMap, changeLogs);
    appendLogsOnCols(this.colsMap, changeLogs);
  }

  private void appendLogsOnCols(Map<String, DimColData> colsMap, List<DimDataRow> changeLogs) {
    if (colsMap != null && changeLogs != null) {
      colsMap.values().parallelStream().filter(dimColData -> dimColData.getLoaded().get())
          .forEach(dimColData -> dimColData.appendLogs(changeLogs));
    }
  }

  protected void addOneRow(DimDataRow addLog) {
    Object addPkVal = addLog.getUk();
    rowsMap.put(addPkVal, addLog);
    idRowsMap.put(addLog.getId(), addLog);
  }

  protected void deleteOneRow(DimDataRow deleteLog) {
    Object deletePkVal = deleteLog.getUk();
    rowsMap.remove(deletePkVal);
    idRowsMap.remove(deleteLog.getId());
  }

  private void fillColsMapWithLabels(Map<String, DimColData> colsMap, List<DimDataRow> rows) {
    rows.parallelStream().forEach(row -> {
      Map<String, Object> labelMap = row.getValues();
      if (labelMap != null) {
        labelMap.forEach((k, v) -> colsMap.putIfAbsent(k, new DimColData(this.tableName, k, this)));
      }
    });
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
    log.info("dim data expired, table={}, buildTime={}, ttl={}, rowSize={}, logSize={}", tableName,
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
