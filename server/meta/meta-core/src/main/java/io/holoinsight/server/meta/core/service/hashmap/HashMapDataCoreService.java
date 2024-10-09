/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.core.service.hashmap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.dao.entity.MetaDimData;
import io.holoinsight.server.common.service.MetaDimDataService;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.common.util.ConstModel;
import io.holoinsight.server.meta.core.common.FilterUtil;
import io.holoinsight.server.meta.core.service.SqlDataCoreService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class HashMapDataCoreService extends SqlDataCoreService {

  public static final Logger logger = LoggerFactory.getLogger(HashMapDataCoreService.class);

  // public static final int LIMIT = 1000;
  public static final int PERIOD = 10;
  public static final int DELETED = 1;
  public static final String EMPTY_VALUE = "NULL";
  // public static final int CLEAN_TASK_PERIOD = 3600;

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

  public HashMapDataCoreService(MetaDimDataService metaDimDataService,
      SuperCacheService superCacheService) {
    super(metaDimDataService, superCacheService);
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

    Boolean booleanValue = superCacheService.getSc().getBooleanValue("global_config", "hashmapDataSync");
    if(null == booleanValue || Boolean.FALSE == booleanValue) return;

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
  protected Consumer<List<MetaDimData>> buildCache() {
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

  private void buildIndexCache(MetaDimData metaData, Map<String, Object> metaRows) {
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

  public Map<String, Object> getMetaByCacheUk(String tableName, String uk) {
    Map<String, Map<String, Object>> ukToRowCache =
        ukMetaCache.getOrDefault(tableName, Maps.newConcurrentMap());
    return ukToRowCache.get(uk);
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
    if (CollectionUtils.isEmpty(metaData)) {
      return Lists.newArrayList();
    }
    final List<String> rowKeys = queryExample.getRowKeys();
    Function<Map<String, Object>, Map<String, Object>> func;
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
      List<String> ukItems = (List<String>) uk;
      for (String ukItem : ukItems) {
        if (null != ukToRowCache.get(ukItem)) {
          metaData.add(ukToRowCache.get(ukItem));
        }
      }
      return metaData;
    } else if (uk instanceof String) {
      if (null == ukToRowCache.get((String) uk)) {
        return new ArrayList<>();
      }
      return Lists.newArrayList(ukToRowCache.get(uk));
    }
    return new ArrayList<>();
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
}
