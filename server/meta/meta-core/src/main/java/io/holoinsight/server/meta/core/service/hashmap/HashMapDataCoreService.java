/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.core.service.hashmap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.Pair;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.common.util.ConstModel;
import io.holoinsight.server.meta.core.common.FilterUtil;
import io.holoinsight.server.meta.core.service.SqlDataCoreService;
import io.holoinsight.server.meta.dal.service.mapper.MetaDataMapper;
import io.holoinsight.server.meta.dal.service.model.MetaDataDO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HashMapDataCoreService extends SqlDataCoreService {

  public static final Logger logger = LoggerFactory.getLogger(HashMapDataCoreService.class);

  public static final int BATCH_INSERT_SIZE = 5;
  public static final int LIMIT = 1000;
  public static final int PERIOD = 10;
  public static final int DELETED = 1;
  public static final String EMPTY_VALUE = "NULL";
  public static final int CLEAN_TASK_PERIOD = 3600;

  // tableName ---> Map(uk ---> Map(key ---> value))
  private Map<String, Map<String, Map<String, Object>>> ukMetaCache;
  // tableName ---> Map(index ---> Map(index ---> ukSet))
  private final Map<String, Map<List<String>, Map<String, Set<String>>>> indexMetaCache;
  private final Map<String, List<List<String>>> metaKeyConfig;
  private volatile long last = -1;
  private volatile long logLast = -1;
  private AtomicBoolean syncing = new AtomicBoolean(false);

  private AtomicBoolean indexBuilding = new AtomicBoolean(false);
  private static final long SYNC_INTERVAL = PERIOD * 1000;
  private static final long LOG_INTERVAL = 60 * 1000;
  private static final long DEFAULT_DEL_DURATION = 3 * 24 * 60 * 60 * 1000;

  public static final ScheduledThreadPoolExecutor scheduledExecutor =
      new ScheduledThreadPoolExecutor(2, r -> new Thread(r, "meta-sync-scheduler"));
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

  public HashMapDataCoreService(MetaDataMapper metaDataMapper,
      SuperCacheService superCacheService) {
    super(metaDataMapper, superCacheService);
    this.ukMetaCache = new ConcurrentHashMap<>();
    this.indexMetaCache = new ConcurrentHashMap<>();
    this.metaKeyConfig = new HashMap<>();
    logger.info("meta service initialized, engine=hashmap");
  }

  @Override
  public void startBuildIndex() {
    if (indexBuilding.compareAndSet(false, true)) {
      initMetaConfig();
      sync();
      scheduledExecutor.scheduleAtFixedRate(this::sync, 60 - LocalTime.now().getSecond(), PERIOD,
          TimeUnit.SECONDS);
      int initialDelay = new Random().nextInt(CLEAN_TASK_PERIOD);
      logger.info("[META-CLEAN] clean task will scheduled after {}", initialDelay);
      cleanMeatExecutor.scheduleAtFixedRate(this::cleanMeta, initialDelay, CLEAN_TASK_PERIOD,
          TimeUnit.SECONDS);
      logger.info("meta service start build index, engine=hashmap");
    }
  }

  private void initMetaConfig() {
    Map<String, Map<String, MetaDataDictValue>> metaDataDictValueMap =
        superCacheService.getSc().metaDataDictValueMap;
    Map<String, MetaDataDictValue> indexKeyMaps =
        metaDataDictValueMap.get(ConstModel.META_INDEX_CONFIG);
    if (CollectionUtils.isEmpty(indexKeyMaps)) {
      return;
    }
    logger.info("[META-INDEX-INFO] indexMetaCache index:{}", indexKeyMaps);
    indexKeyMaps.forEach((key, dict) -> {
      if (StringUtils.isBlank(dict.dictValue)) {
        return;
      }
      List<List<String>> indexFields =
          J.fromJson(dict.dictValue, new TypeToken<List<List<String>>>() {}.getType());
      metaKeyConfig.put(key, indexFields);
    });
  }

  /** load meta data */
  private void sync() {
    StopWatch stopWatch = StopWatch.createStarted();
    long now = System.currentTimeMillis() / 1000 * 1000;
    if (now - last >= SYNC_INTERVAL) {
      if (syncing.compareAndSet(false, true)) {
        try {
          if (last < 0) {
            int count = queryChangedMeta(new Date(0), new Date(now), false, buildCache());
            logger.info("[META-SYNC] init success, size={}, now={}, cost={}", count, now,
                stopWatch.getTime());
          } else {
            int count =
                queryChangedMeta(new Date(last - SYNC_INTERVAL), new Date(now), true, buildCache());
            logger.info("[META-SYNC] sync success, size={}, last={}, now={}, cost={}", count, last,
                now, stopWatch.getTime());
          }
          if (now - logLast >= LOG_INTERVAL && now / 1000 % 60 <= PERIOD) {
            ukMetaCache.forEach((tableName, metaData) -> {
              logger.info("[META-INFO] ukMetaCache at {}, table={}, index={}, records={}", now,
                  tableName, ConstModel.default_pk,
                  CollectionUtils.isEmpty(metaData) ? 0 : metaData.size());
            });
            indexMetaCache.forEach((tableName, indexItem) -> {
              if (!CollectionUtils.isEmpty(indexItem)) {
                indexItem.forEach((index, items) -> {
                  logger.info("[META-INFO] indexMetaCache at {}, table={}, index={}, records={}",
                      now, tableName, index, CollectionUtils.isEmpty(items) ? 0 : items.size());
                });
              }
            });
            logLast = now;
          }
        } catch (Exception e) {
          logger.error("[META-SYNC] sync fail, last={}, now={}", last, now, e);
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
  protected Consumer<List<MetaDataDO>> buildCache() {
    return data -> data.forEach(metaData -> {
      Map<String, Map<String, Object>> items =
          ukMetaCache.computeIfAbsent(metaData.getTableName(), k -> Maps.newConcurrentMap());
      Map<String, Object> metaRows = J.toMap(metaData.getJson());
      if (metaData.getDeleted() == DELETED) {
        items.remove(metaData.getUk());
      } else {
        items.put(metaData.getUk(), metaRows);
      }
      buildIndexCache(metaData, metaRows);
    });
  }

  private void buildIndexCache(MetaDataDO metaData, Map<String, Object> metaRows) {
    List<List<String>> indexFieldList = metaKeyConfig.get(metaData.getTableName());
    if (CollectionUtils.isEmpty(indexFieldList)) {
      return;
    }
    Map<List<String>, Map<String, Set<String>>> indexToMetaDataUks =
        indexMetaCache.computeIfAbsent(metaData.getTableName(), k -> Maps.newConcurrentMap());
    for (List<String> indexFields : indexFieldList) {
      String index = buildIndex(indexFields, metaRows);
      Map<String, Set<String>> indexToUks =
          indexToMetaDataUks.computeIfAbsent(indexFields, k -> Maps.newConcurrentMap());
      Set<String> uks = indexToUks.computeIfAbsent(index, k -> Sets.newConcurrentHashSet());
      if (metaData.getDeleted() == DELETED) {
        uks.remove(metaData.getUk());
      } else {
        uks.add(metaData.getUk());
      }
    }
  }

  private String buildIndex(List<String> indexKeys, Map<String, Object> metaRows) {
    StringBuilder builder = new StringBuilder();
    boolean containAllKeys = true;
    for (String key : indexKeys) {
      Object value;
      // 支持两层 key 索引
      if (key.contains(".")) {
        int doxIndex = key.indexOf('.');
        String firstKey = key.substring(0, doxIndex);
        String secondKey = key.substring(doxIndex + 1);
        Object o = metaRows.get(firstKey);
        if (Objects.isNull(o)) {
          value = null;
        } else {
          Map<String, Object> map = J.toMap(J.toJson(o));
          if (null == map) {
            value = null;
          } else {
            value = map.get(secondKey);
          }
        }
      } else {
        value = metaRows.get(key);
      }
      if (Objects.isNull(value)) {
        containAllKeys = false;
        builder.append(EMPTY_VALUE);
      } else {
        builder.append(value);
      }
      builder.append(":");
    }
    if (!containAllKeys) {
      logger.warn("[INDEX-BUILD] index: {}, uk: {} is not containAllKeys", indexKeys,
          metaRows.get(ConstModel.default_pk));
    }
    return builder.substring(0, builder.length() - 1);
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
    Map<String, Map<String, Object>> ukToRowCache =
        ukMetaCache.getOrDefault(tableName, Maps.newConcurrentMap());
    int existUkSize = 0;
    int sameUkSize = 0;
    for (MetaDataDO metaData : metaDataList) {
      String uk = metaData.getUk();
      existUkSize++;
      Map<String, Object> updateOrInsertRow = ukToUpdateOrInsertRow.remove(uk);
      Map<String, Object> cachedRow = ukToRowCache.get(uk);
      Pair<Boolean, Object> sameWithDbAnnotations =
          sameWithDbAnnotations(metaData, updateOrInsertRow);
      if (sameWithCache(updateOrInsertRow, cachedRow) && sameWithDbAnnotations.left()) {
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
  public List<Map<String, Object>> queryByTable(String tableName) {
    return queryByTable(tableName, Lists.newArrayList());
  }

  @Override
  public List<Map<String, Object>> queryByTable(String tableName, List<String> rowKeys) {
    logger.info("[queryByTable] start, table={}, rowsKeys={}.", tableName, rowKeys);
    StopWatch stopWatch = StopWatch.createStarted();
    Map<String, Map<String, Object>> ukToRowCache =
        ukMetaCache.getOrDefault(tableName, Maps.newConcurrentMap());
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
        ukMetaCache.getOrDefault(tableName, Maps.newConcurrentMap());
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
    Map<String, Map<String, Object>> ukToRowCache = ukMetaCache.get(tableName);
    if (CollectionUtils.isEmpty(ukToRowCache)) {
      return Lists.newArrayList();
    }
    Map<String, Map<String, Object>> filters = FilterUtil.buildFilters(queryExample, false);
    Collection<Map<String, Object>> metaData =
        getMetaDataFromCache(tableName, queryExample, ukToRowCache);
    final List<String> rowKeys = queryExample.getRowKeys();
    Function<Map, Map<String, Object>> func;
    if (CollectionUtils.isEmpty(rowKeys)) {
      func = v -> v;
    } else {
      func = v -> Maps.filterKeys(v, rowKeys::contains);
    }
    List<Map<String, Object>> rows = FilterUtil.filterData(metaData, filters, func);
    logger.info("[queryByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return rows;
  }

  public Collection<Map<String, Object>> getMetaDataFromCache(String tableName,
      QueryExample queryExample, Map<String, Map<String, Object>> ukToRowCache) {
    Map<String, Object> params = queryExample.getParams();
    if (params.containsKey(ConstModel.default_pk)) {
      logger.info("[META-CACHE] hit index: [{}], table={}, params:{}", ConstModel.default_pk,
          tableName, params);
      return getMetaDataFromUkCache(ukToRowCache, params);
    }
    List<String> indexKeys = getMatchedIndex(tableName, params);
    if (!CollectionUtils.isEmpty(indexKeys)) {
      logger.info("[META-CACHE] hit index: {}, table={}, params:{}", indexKeys, tableName, params);
      return getMetaDataFromIndexCache(tableName, ukToRowCache, params, indexKeys);
    }
    logger.info("[META-CACHE] miss index: [-], table={}, params:{}", tableName, params);
    return ukToRowCache.values();
  }

  private Collection<Map<String, Object>> getMetaDataFromIndexCache(String tableName,
      Map<String, Map<String, Object>> ukToRowCache, Map<String, Object> params,
      List<String> indexKeys) {
    List<String> indexes = buildIndexByParam(indexKeys, params);
    Map<String, Set<String>> indexToUks = indexMetaCache.get(tableName).get(indexKeys);
    Collection<Map<String, Object>> metaData = Lists.newArrayList();
    for (String index : indexes) {
      Set<String> uks = indexToUks.get(index);
      if (!CollectionUtils.isEmpty(uks)) {
        uks.forEach(key -> {
          Map<String, Object> data = ukToRowCache.get(key);
          if (!CollectionUtils.isEmpty(data)) {
            metaData.add(data);
          }
        });
      }
    }
    return metaData;
  }

  private List<String> buildIndexByParam(List<String> indexKeys, Map<String, Object> params) {
    List<List<String>> wordLists = Lists.newArrayList();
    for (String key : indexKeys) {
      Object value = params.get(key);
      if (value instanceof List) {
        wordLists.add((List) value);
      } else {
        wordLists.add(Lists.newArrayList(value.toString()));
      }
    }
    return FilterUtil.genCartesianStrList(wordLists);
  }

  private Collection<Map<String, Object>> getMetaDataFromUkCache(
      Map<String, Map<String, Object>> ukToRowCache, Map<String, Object> params) {
    Object uk = params.get(ConstModel.default_pk);
    if (uk instanceof List) {
      Collection<Map<String, Object>> metaData = Lists.newArrayList();
      List<String> ukItems = (List) uk;
      for (String ukItem : ukItems) {
        metaData.add(ukToRowCache.get(ukItem));
      }
      return metaData;
    } else {
      return Lists.newArrayList(ukToRowCache.get(uk));
    }
  }

  private List<String> getMatchedIndex(String tableName, Map<String, Object> params) {
    if (CollectionUtils.isEmpty(params)) {
      return null;
    }
    List<List<String>> indexFieldList = metaKeyConfig.get(tableName);
    if (CollectionUtils.isEmpty(indexFieldList)) {

      return null;
    }
    for (List<String> indexFields : indexFieldList) {
      boolean containsAllKeys = true;
      for (String field : indexFields) {
        if (!params.containsKey(field)) {
          containsAllKeys = false;
          break;
        }
      }
      if (containsAllKeys) {
        return indexFields;
      }
    }
    return null;
  }

  @Override
  public List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample queryExample) {
    logger.info("[fuzzyByExample] start, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    Map<String, Map<String, Object>> filters = FilterUtil.buildFilters(queryExample, true);
    Map<String, Map<String, Object>> ukToRowCache = ukMetaCache.get(tableName);
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
    Map<String, Map<String, Object>> ukToRowCache = ukMetaCache.get(tableName);
    if (CollectionUtils.isEmpty(ukToRowCache)) {
      return 0;
    }
    Collection<Map<String, Object>> metaData =
        getMetaDataFromCache(tableName, queryExample, ukToRowCache);
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
