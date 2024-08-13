/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.Pair;
import io.holoinsight.server.common.dao.entity.MetaDimData;
import io.holoinsight.server.common.service.MetaDimDataService;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.common.util.ConstModel;
import io.holoinsight.server.meta.core.common.FilterUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
  protected MetaDimDataService metaDimDataService;
  protected SuperCacheService superCacheService;


  public SqlDataCoreService(MetaDimDataService metaDimDataService,
      SuperCacheService superCacheService) {
    this.metaDimDataService = metaDimDataService;
    this.superCacheService = superCacheService;
  }

  public Integer queryChangedMeta(Date start, Date end, Boolean containDeleted,
      Consumer<List<MetaDimData>> listConsumer) {
    int pageNum = 1;
    int count = 0;
    while (true) {
      List<MetaDimData> metaDataList =
          metaDimDataService.queryChangedMeta(start, end, containDeleted, pageNum, LIMIT);
      // offset += LIMIT;
      if (metaDataList.isEmpty()) {
        break;
      }
      count += metaDataList.size();
      pageNum++;
      listConsumer.accept(metaDataList);
    }
    return count;
  }

  public List<MetaDimData> queryTableChangedMeta(String tableName, Date start, Date end,
      Boolean containDeleted) {
    int pageNum = 1;
    List<MetaDimData> result = new ArrayList<>();
    while (true) {
      List<MetaDimData> metaDataList = metaDimDataService.queryTableChangedMeta(tableName, start,
          end, containDeleted, pageNum, LIMIT);
      // offset += LIMIT;
      if (metaDataList.isEmpty()) {
        break;
      }
      pageNum++;
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
      List<MetaDimData> metaDataList =
          metaDimDataService.selectByUks(tableName, ukToUpdateOrInsertRow.keySet());
      logger.info("selectByUks, table={}, metaDataList={}", tableName, metaDataList.size());
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

  public Integer doInsert(String tableName,
      Map<String, Map<String, Object>> ukToUpdateOrInsertRow) {
    if (ukToUpdateOrInsertRow.isEmpty()) {
      return 0;
    }
    List<String> addedUks = Lists.newArrayList();
    List<MetaDimData> metaDataList = Lists.newArrayList();
    ukToUpdateOrInsertRow.forEach((uk, row) -> {
      addedUks.add(uk);
      MetaDimData metaData = new MetaDimData();
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
          .forEach(list -> metaDimDataService.batchInsertOrUpdate(tableName, list));
    }
    logger.info("[insertOrUpdate] insert finish, uks:{}", addedUks);
    return addedUks.size();
  }

  public Pair<Integer, Integer> doUpdate(String tableName, List<MetaDimData> metaDataList,
      Map<String, Map<String, Object>> ukToUpdateOrInsertRow) {
    int existUkSize = 0;
    int sameUkSize = 0;
    for (MetaDimData metaData : metaDataList) {
      String uk = metaData.getUk();
      existUkSize++;
      Map<String, Object> updateOrInsertRow = ukToUpdateOrInsertRow.get(uk);
      Map<String, Object> cachedRows = getMetaByCacheUk(tableName, uk);
      Pair<Boolean, Object> sameWithDbAnnotations =
          sameWithDbAnnotations(metaData, updateOrInsertRow);
      if (!CollectionUtils.isEmpty(cachedRows) && sameWithCache(updateOrInsertRow, cachedRows)
          && sameWithDbAnnotations.left()) {
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
      assert sourceRow != null;
      sourceRow.putAll(updateOrInsertRow);
      metaData.setJson(J.toJson(sourceRow));
      metaData.setGmtModified(new Date());
      metaDimDataService.updateByUk(tableName, metaData);
    }
    logger.info("[insertOrUpdate] update finish, update existUkSize:{}, sameUkSize:{}", existUkSize,
        sameUkSize);
    return new Pair<>(sameUkSize, existUkSize);
  }

  public Map<String, Object> getMetaByCacheUk(String tableName, String uk) {
    List<Map<String, Object>> maps = queryByPks(tableName, Collections.singletonList(uk));
    if (CollectionUtils.isEmpty(maps))
      return null;
    return maps.get(0);
  }

  /**
   *
   * @param metaData
   * @param updateOrInsertRow
   * @return
   */
  public Pair<Boolean, Object> sameWithDbAnnotations(MetaDimData metaData,
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

  public boolean sameWithCache(Map<String, Object> updateOrInsertRow,
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
    Integer count = metaDimDataService.softDeleteByUks(tableName, default_pks, new Date());
    logger.info("[batchDeleteByPk] finish, table={}, deleteCount={}, cost={}", tableName, count,
        stopWatch.getTime());
    return count;
  }

  // private long getCleanMetaDataDuration() {
  // Map<String, Map<String, MetaDataDictValue>> metaDataDictValueMap =
  // superCacheService.getSc().metaDataDictValueMap;
  // Map<String, MetaDataDictValue> indexKeyMaps = metaDataDictValueMap.get(ConstModel.META_CONFIG);
  // if (CollectionUtils.isEmpty(indexKeyMaps)) {
  // return DEFAULT_DEL_DURATION;
  // }
  // MetaDataDictValue metaDataDictValue = indexKeyMaps.get(ConstModel.CLEAN_META_DURATION_HOURS);
  // if (Objects.isNull(metaDataDictValue)) {
  // return DEFAULT_DEL_DURATION;
  // }
  // int durationHours = Integer.parseInt(metaDataDictValue.getDictValue());
  // return durationHours * 60L * 60 * 1000;
  // }

}
