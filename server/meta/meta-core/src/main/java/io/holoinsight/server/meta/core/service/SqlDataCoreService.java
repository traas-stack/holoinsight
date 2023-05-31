/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.Pair;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.core.common.FilterUtil;
import io.holoinsight.server.meta.dal.service.mapper.MetaDataMapper;
import io.holoinsight.server.meta.dal.service.model.MetaData;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 *
 * @author jiwliu
 * @version 1.0: SqlDataCoreService.java, v 0.1 2022年03月07日 9:13 下午 jinsong.yjs Exp $
 */
public class SqlDataCoreService extends AbstractDataCoreService {

  public static final Logger logger = LoggerFactory.getLogger(SqlDataCoreService.class);

  public static final int BATCH_INSERT_SIZE = 5;
  public static final int LIMIT = 1000;
  public static final int PERIOD = 30;
  public static final int DELETED = 1;
  public static final String MODIFIED_FIELD = "_modified";
  public static final String ANNOTATIONS_FIELD = "annotations";
  private MetaDataMapper metaDataMapper;

  // tableName ---> Map(uk ---> Map(key ---> value))
  private Map<String, Map<String, Map<String, Object>>> metaCache;
  private volatile long last = -1;
  private AtomicBoolean syncing = new AtomicBoolean(false);
  private final long interval = PERIOD * 1000;
  public static final ScheduledThreadPoolExecutor scheduledExecutor =
      new ScheduledThreadPoolExecutor(2, r -> new Thread(r, "meta-sync-scheduler"));

  public SqlDataCoreService(MetaDataMapper metaDataMapper) {
    this.metaDataMapper = metaDataMapper;
    metaCache = new HashMap<>();
    sync();
    scheduledExecutor.scheduleAtFixedRate(this::sync, PERIOD, PERIOD, TimeUnit.SECONDS);
  }

  /**
   * load meta data
   */
  private void sync() {
    StopWatch stopWatch = StopWatch.createStarted();
    long now = System.currentTimeMillis() / 1000 * 1000;
    if (now - last >= interval) {
      if (syncing.compareAndSet(false, true)) {
        try {
          if (last < 0) {
            int count = queryChangedMeta(new Date(0), new Date(now), false, dealUpdatedMeta());
            logger.info("[FullCache] init query success, size={}, now={}, cost={}", count, now,
                stopWatch.getTime());
          } else {
            int count =
                queryChangedMeta(new Date(last - 2000), new Date(now), true, dealUpdatedMeta());
            logger.info("[FullCache] sync query success, size={}, last={}, now={}, cost={}", count,
                last, now, stopWatch.getTime());
          } ;
          logger.info("[FullCache] sync success, last={}, now={}, cost={}", last, now,
              stopWatch.getTime());
        } catch (Exception e) {
          logger.error("[FullCache] sync fail, last={}, now={}", last, now, e);
        } finally {
          last = now;
          syncing.compareAndSet(true, false);
        }
      }
    }
  }

  /**
   * update meta cache
   * 
   * @return
   */
  private Consumer<List<MetaData>> dealUpdatedMeta() {
    return data -> data.forEach(metaData -> {
      Map<String, Map<String, Object>> items =
          metaCache.computeIfAbsent(metaData.getTableName(), k -> Maps.newConcurrentMap());
      if (metaData.getDeleted() == DELETED) {
        items.remove(metaData.getUk());
      } else {
        items.put(metaData.getUk(), J.toMap(metaData.getJson()));
      }
    });
  }

  private Integer queryChangedMeta(Date start, Date end, Boolean containDeleted,
      Consumer<List<MetaData>> listConsumer) {
    int offset = 0;
    int count = 0;
    while (true) {
      List<MetaData> metaDataList =
          metaDataMapper.queryChangedMeta(start, end, containDeleted, offset, LIMIT);
      offset += LIMIT;
      if (metaDataList.isEmpty()) {
        break;
      }
      count += metaDataList.size();
      listConsumer.accept(metaDataList);
    }
    return count;
  }

  @Override
  public Pair<Integer, Integer> insertOrUpdate(String tableName, List<Map<String, Object>> rows) {
    logger.info("[insertOrUpdate] start, table={}, records={}.", tableName, rows.size());
    if (CollectionUtils.isEmpty(rows)) {
      return new Pair<>(0, 0);
    }
    try {
      List<Map<String, Object>> filterRows = addUkValues(tableName, rows);
      StopWatch stopWatch = StopWatch.createStarted();
      Map<String, Map<String, Object>> ukToUpdateOrInsertRow = Maps.newHashMap();
      filterRows.forEach(item -> {
        String uk = item.getOrDefault("_uk", "").toString();
        ukToUpdateOrInsertRow.put(uk, item);
      });
      List<MetaData> metaDataList =
          metaDataMapper.selectByUks(tableName, ukToUpdateOrInsertRow.keySet());
      Pair<Integer, Integer> sameAndExistSize;
      if (!CollectionUtils.isEmpty(metaDataList)) {
        sameAndExistSize = doUpdate(tableName, metaDataList, ukToUpdateOrInsertRow);
      } else {
        sameAndExistSize = new Pair<>(0, 0);
      }
      Integer addedSize = doInsert(tableName, ukToUpdateOrInsertRow);
      int modifiedCount = sameAndExistSize.right() - sameAndExistSize.left();
      int upsertSize = addedSize + modifiedCount;
      logger.info(
          "[insertOrUpdate]finish, table={}, upsertSize={}, matchedCount={}, modifiedCount={}, cost={}.",
          tableName, upsertSize, sameAndExistSize.right(), modifiedCount, stopWatch.getTime());
      return new Pair<>(upsertSize, modifiedCount);
    } catch (Exception e) {
      logger.error("[insertOrUpdate] fail, table={}, rows={}, contents={}.", tableName, rows.size(),
          rows, e);
      return new Pair<>(0, 0);
    }
  }

  private Integer doInsert(String tableName,
      Map<String, Map<String, Object>> ukToUpdateOrInsertRow) {
    if (ukToUpdateOrInsertRow.isEmpty()) {
      return 0;
    }
    List<String> addedUks = Lists.newArrayList();
    List<MetaData> metaDataList = Lists.newArrayList();
    ukToUpdateOrInsertRow.forEach((uk, row) -> {
      addedUks.add(uk);
      MetaData metaData = new MetaData();
      metaData.setUk(uk);
      metaData.setTableName(tableName);
      metaData.setDeleted(0);
      Object annotations = row.remove(ANNOTATIONS_FIELD);
      if (annotations != null) {
        metaData.setAnnotations(J.toJson(annotations));
      }
      metaData.setJson(J.toJson(row));
      metaData.setGmtCreate(new Date());
      metaData.setGmtModified(new Date());
      metaDataList.add(metaData);
    });
    if (!CollectionUtils.isEmpty(metaDataList)) {
      Lists.partition(metaDataList, BATCH_INSERT_SIZE)
          .forEach(list -> metaDataMapper.batchInsertOrUpdate(list));
    }
    logger.info("[insertOrUpdate] insert finish, uks:{}", addedUks);
    return addedUks.size();
  }

  private Pair<Integer, Integer> doUpdate(String tableName, List<MetaData> metaDataList,
      Map<String, Map<String, Object>> ukToUpdateOrInsertRow) {
    Map<String, Map<String, Object>> ukToRowCache =
        metaCache.getOrDefault(tableName, Maps.newConcurrentMap());
    List<String> existUks = new ArrayList<>(metaDataList.size());
    List<String> sameUks = new ArrayList<>(metaDataList.size());
    for (MetaData metaData : metaDataList) {
      String uk = metaData.getUk();
      existUks.add(uk);
      Map<String, Object> updateOrInsertRow = ukToUpdateOrInsertRow.remove(uk);
      Map<String, Object> cachedRow = ukToRowCache.get(uk);
      Pair<Boolean, Object> sameWithDbAnnotations =
          sameWithDbAnnotations(metaData, updateOrInsertRow);
      if (sameWithCache(updateOrInsertRow, cachedRow) && sameUks.add(uk)
          && sameWithDbAnnotations.left()) {
        continue;
      }
      if (!sameWithDbAnnotations.left()) {
        if (Objects.isNull(sameWithDbAnnotations.right())) {
          metaData.setAnnotations(J.toJson(new HashMap<String, Object>()));
        } else {
          metaData.setAnnotations(J.toJson(sameWithDbAnnotations.right()));
        }
      }
      Map<String, Object> sourceRow = J.toMap(metaData.getJson());
      sourceRow.putAll(updateOrInsertRow);
      metaData.setJson(J.toJson(sourceRow));
      metaData.setGmtModified(new Date());
      metaDataMapper.updateByUk(tableName, metaData);
    }
    logger.info("[insertOrUpdate] update finish, update same uks:{}, existUks:{}", sameUks,
        existUks);
    return new Pair<>(sameUks.size(), existUks.size());
  }

  /**
   *
   * @param metaData
   * @param updateOrInsertRow
   * @return
   */
  private Pair<Boolean, Object> sameWithDbAnnotations(MetaData metaData,
      Map<String, Object> updateOrInsertRow) {
    Object annotations = updateOrInsertRow.remove(ANNOTATIONS_FIELD);
    Map<String, Object> extraMap = null;
    String dbAnnotations = metaData.getAnnotations();
    if (StringUtils.isNotBlank(dbAnnotations)) {
      extraMap = J.toMap(metaData.getAnnotations());
    }
    if (Objects.equals(extraMap, annotations)) {
      return new Pair<>(true, null);
    }
    return new Pair<>(false, annotations);
  }

  private boolean sameWithCache(Map<String, Object> updateOrInsertRow,
      Map<String, Object> cachedRow) {
    if (CollectionUtils.isEmpty(cachedRow)) {
      return false;
    }
    Set<String> keys = updateOrInsertRow.keySet();
    for (String key : keys) {
      if (!MODIFIED_FIELD.equalsIgnoreCase(key)
          && !Objects.equals(updateOrInsertRow.get(key), cachedRow.get(key))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public List<Map<String, Object>> queryByTable(String tableName) {
    return queryByTable(tableName, Lists.newArrayList());
  }

  @Override
  public List<Map<String, Object>> queryByTable(String tableName, List<String> rowKeys) {
    logger.info("[queryByTable] start, table={}, rowsKeys={}.", tableName, rowKeys);
    StopWatch stopWatch = StopWatch.createStarted();
    Map<String, Map<String, Object>> ukToRowCache =
        metaCache.getOrDefault(tableName, Maps.newConcurrentMap());
    List<Map<String, Object>> results;
    if (!CollectionUtils.isEmpty(rowKeys)) {
      results = ukToRowCache.values().stream()
          .map(data -> data.entrySet().stream().filter(entry -> rowKeys.contains(entry.getKey()))
              .collect(Collectors.toMap(Entry::getKey, Entry::getValue)))
          .collect(Collectors.toList());
    } else {
      results = new ArrayList<>(ukToRowCache.values());
    }
    logger.info("[queryByTable] finish, table={}, records={}, cost={}.", tableName, results.size(),
        stopWatch.getTime());
    return results;
  }

  @Override
  public List<Map<String, Object>> queryByPks(String tableName, List<String> pkValList) {
    logger.info("[queryByPks] start, table={}, pkValList={}.", tableName, pkValList.size());
    Map<String, Map<String, Object>> ukToRowCache =
        metaCache.getOrDefault(tableName, Maps.newConcurrentMap());
    if (CollectionUtils.isEmpty(pkValList) || null == ukToRowCache) {
      return Lists.newArrayList();
    }
    StopWatch stopWatch = StopWatch.createStarted();
    List<Map<String, Object>> result = Lists.newArrayList();
    pkValList.forEach(uk -> {
      Map<String, Object> item = ukToRowCache.get(uk);
      if (!CollectionUtils.isEmpty(item)) {
        result.add(item);
      }
    });
    logger.info("[queryByPks] finish, table={}, records={}, cost={}.", tableName, result.size(),
        stopWatch.getTime());
    return result;
  }

  @Override
  public List<Map<String, Object>> queryByExample(String tableName, QueryExample queryExample) {
    logger.info("[queryByExample] start, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    Map<String, Map<String, Object>> filters = FilterUtil.buildFilters(queryExample, false);
    Map<String, Map<String, Object>> ukToRowCache = metaCache.get(tableName);
    if (CollectionUtils.isEmpty(ukToRowCache)) {
      return Lists.newArrayList();
    }
    List<Map<String, Object>> rows = FilterUtil.filterData(ukToRowCache.values(), filters, v -> v);
    logger.info("[queryByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return rows;
  }

  @Override
  public List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample queryExample) {
    logger.info("[fuzzyByExample] start, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    Map<String, Map<String, Object>> filters = FilterUtil.buildFilters(queryExample, true);
    Map<String, Map<String, Object>> ukToRowCache = metaCache.get(tableName);

    if (CollectionUtils.isEmpty(ukToRowCache)) {
      return Lists.newArrayList();
    }
    List<Map<String, Object>> rows = FilterUtil.filterData(ukToRowCache.values(), filters, v -> v);
    logger.info("[fuzzyByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return rows;
  }


  @Override
  public long deleteByExample(String tableName, QueryExample queryExample) {
    logger.info("[deleteByExample] start, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    Map<String, Map<String, Object>> filters = FilterUtil.buildFilters(queryExample, false);
    List<Map<String, Object>> metaDatas = queryByTable(tableName);
    if (CollectionUtils.isEmpty(metaDatas)) {
      return 0;
    }
    List<String> uks = FilterUtil.filterData(metaDatas, filters, map -> map.get("_uk").toString());
    if (CollectionUtils.isEmpty(uks)) {
      return 0;
    }
    batchDeleteByPk(tableName, uks);
    // 输出结果信息
    logger.info("[deleteByExample] finish, table={}, deleteCount={}, cost={}.", tableName,
        uks.size(), stopWatch.getTime());
    return uks.size();
  }

  @Override
  public long deleteByRowMap(String tableName, List<Map<String, Object>> rows) {
    logger.info("[deleteByRowMap] start, table={}, default_pks={}.", tableName, rows.size());
    if (CollectionUtils.isEmpty(rows))
      return 0;

    List<String> uks = getUks(tableName, rows);
    if (CollectionUtils.isEmpty(uks)) {
      logger.info("[deleteByRowMap] finish, table={}, uks is null.", tableName);
      return 0;
    }
    return batchDeleteByPk(tableName, uks);
  }

  @Override
  public long batchDeleteByPk(String tableName, List<String> default_pks) {
    logger.info("[batchDeleteByPk] start, table={}, default_pks={}.", tableName,
        default_pks.size());

    if (CollectionUtils.isEmpty(default_pks)) {
      return 0;
    }
    StopWatch stopWatch = StopWatch.createStarted();
    Integer count = metaDataMapper.softDeleteByUks(tableName, default_pks, new Date());
    logger.info("[batchDeleteByPk] finish, table={}, deleteCount={}, cost={}, deleteUks:{}",
        tableName, count, stopWatch.getTime(), default_pks);
    return count;
  }
}
