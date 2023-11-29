/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.Pair;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.common.util.ConstModel;
import io.holoinsight.server.meta.core.common.FilterUtil;
import io.holoinsight.server.meta.dal.service.mapper.MetaDataMapper;
import io.holoinsight.server.meta.dal.service.model.MetaDataDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 *
 * @author jiwliu
 * @version 1.0: SqlDataCoreService.java, v 0.1 2022年03月07日 9:13 下午 jinsong.yjs Exp $
 */
public abstract class SqlDataCoreService extends AbstractDataCoreService {

  public static final Logger logger = LoggerFactory.getLogger(SqlDataCoreService.class);

  public static final int BATCH_INSERT_SIZE = 5;
  public static final int LIMIT = 1000;
  public static final int PERIOD = 10;
  public static final int CLEAN_TASK_PERIOD = 3600;
  protected MetaDataMapper metaDataMapper;
  protected SuperCacheService superCacheService;

  private static final long DEFAULT_DEL_DURATION = 3 * 24 * 60 * 60 * 1000;
  public static final ScheduledThreadPoolExecutor cleanMeatExecutor =
      new ScheduledThreadPoolExecutor(2, r -> new Thread(r, "meta-clean-scheduler"));

  private void cleanMeta() {
    StopWatch stopWatch = StopWatch.createStarted();
    try {
      long cleanMetaDataDuration = getCleanMetaDataDuration();
      long end = System.currentTimeMillis() - cleanMetaDataDuration;
      logger.info("[META-CLEAN] the cleaning task will clean up the data before {}", end);
      Integer count = metaDataMapper.cleanMetaData(new Date(end));
      logger.info("[META-CLEAN] cleaned up {} pieces of data before {}, cost: {}", count, end,
          stopWatch.getTime());
    } catch (Exception e) {
      logger.error("[META-CLEAN] an exception occurred in the cleanup task", e);
    }
  }

  public SqlDataCoreService(MetaDataMapper metaDataMapper, SuperCacheService superCacheService) {
    this.metaDataMapper = metaDataMapper;
    this.superCacheService = superCacheService;
    int initialDelay = new Random().nextInt(CLEAN_TASK_PERIOD);
    logger.info("[META-CLEAN] clean task will scheduled after {}", initialDelay);
    cleanMeatExecutor.scheduleAtFixedRate(this::cleanMeta, initialDelay, CLEAN_TASK_PERIOD,
        TimeUnit.SECONDS);
  }

  protected Integer queryChangedMeta(Date start, Date end, Boolean containDeleted,
      Consumer<List<MetaDataDO>> listConsumer) {
    int offset = 0;
    int count = 0;
    while (true) {
      List<MetaDataDO> metaDataList =
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

  protected List<MetaDataDO> queryTableChangedMeta(String tableName, Date start, Date end,
      Boolean containDeleted) {
    int offset = 0;
    List<MetaDataDO> result = new ArrayList<>();
    while (true) {
      List<MetaDataDO> metaDataList = metaDataMapper.queryTableChangedMeta(tableName, start, end,
          containDeleted, offset, LIMIT);
      offset += LIMIT;
      if (metaDataList.isEmpty()) {
        break;
      }
      result.addAll(metaDataList);
    }
    return result;
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
        String uk = item.get(ConstModel.default_pk).toString();
        ukToUpdateOrInsertRow.put(uk, item);
      });
      List<MetaDataDO> metaDataList =
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
          "[insertOrUpdate] finish, table={}, upsertSize={}, matchedCount={}, modifiedCount={}, cost={}.",
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
    List<MetaDataDO> metaDataList = Lists.newArrayList();
    ukToUpdateOrInsertRow.forEach((uk, row) -> {
      addedUks.add(uk);
      MetaDataDO metaData = new MetaDataDO();
      metaData.setUk(uk);
      metaData.setTableName(tableName);
      metaData.setDeleted(0);
      Object annotations = row.remove(ConstModel.ANNOTATIONS);
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

  private Pair<Integer, Integer> doUpdate(String tableName, List<MetaDataDO> metaDataList,
      Map<String, Map<String, Object>> ukToUpdateOrInsertRow) {
    int existUkSize = 0;
    int sameUkSize = 0;
    for (MetaDataDO metaData : metaDataList) {
      String uk = metaData.getUk();
      existUkSize++;
      Map<String, Object> updateOrInsertRow = ukToUpdateOrInsertRow.remove(uk);
      List<Map<String, Object>> cachedRows = queryByPks(tableName, Collections.singletonList(uk));
      Pair<Boolean, Object> sameWithDbAnnotations =
          sameWithDbAnnotations(metaData, updateOrInsertRow);
      if (!CollectionUtils.isEmpty(cachedRows)
          && sameWithCache(updateOrInsertRow, cachedRows.get(0)) && sameWithDbAnnotations.left()) {
        sameUkSize++;
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
    logger.info("[insertOrUpdate] update finish, update existUkSize:{}, sameUkSize:{}", existUkSize,
        sameUkSize);
    return new Pair<>(sameUkSize, existUkSize);
  }

  /**
   *
   * @param metaData
   * @param updateOrInsertRow
   * @return
   */
  private Pair<Boolean, Object> sameWithDbAnnotations(MetaDataDO metaData,
      Map<String, Object> updateOrInsertRow) {
    Object annotations = updateOrInsertRow.remove(ConstModel.ANNOTATIONS);
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
      if (!ConstModel.default_modified.equalsIgnoreCase(key)
          && !Objects.equals(updateOrInsertRow.get(key), cachedRow.get(key))) {
        return false;
      }
    }
    return true;
  }

  @Override
  public abstract List<Map<String, Object>> queryByTable(String tableName);

  @Override
  public abstract List<Map<String, Object>> queryByTable(String tableName, List<String> rowKeys);

  @Override
  public abstract List<Map<String, Object>> queryByPks(String tableName, List<String> pkValList);

  @Override
  public abstract List<Map<String, Object>> queryByExample(String tableName,
      QueryExample queryExample);


  @Override
  public abstract List<Map<String, Object>> fuzzyByExample(String tableName,
      QueryExample queryExample);


  @Override
  public long deleteByExample(String tableName, QueryExample queryExample) {
    logger.info("[deleteByExample] start, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    Collection<Map<String, Object>> metaData = queryByExample(tableName, queryExample);
    if (CollectionUtils.isEmpty(metaData)) {
      return 0;
    }
    Map<String, Map<String, Object>> filters = FilterUtil.buildFilters(queryExample, false);
    List<String> uks =
        FilterUtil.filterData(metaData, filters, map -> map.get(ConstModel.default_pk).toString());
    if (CollectionUtils.isEmpty(uks)) {
      return 0;
    }
    batchDeleteByPk(tableName, uks);
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
    logger.info("[batchDeleteByPk] finish, table={}, deleteCount={}, cost={}", tableName, count,
        stopWatch.getTime());
    return count;
  }

  private long getCleanMetaDataDuration() {
    Map<String, Map<String, MetaDataDictValue>> metaDataDictValueMap =
        superCacheService.getSc().metaDataDictValueMap;
    Map<String, MetaDataDictValue> indexKeyMaps = metaDataDictValueMap.get(ConstModel.META_CONFIG);
    if (CollectionUtils.isEmpty(indexKeyMaps)) {
      return DEFAULT_DEL_DURATION;
    }
    MetaDataDictValue metaDataDictValue = indexKeyMaps.get(ConstModel.CLEAN_META_DURATION_HOURS);
    if (Objects.isNull(metaDataDictValue)) {
      return DEFAULT_DEL_DURATION;
    }
    int durationHours = Integer.parseInt(metaDataDictValue.getDictValue());
    return durationHours * 60L * 60 * 1000;
  }

}
