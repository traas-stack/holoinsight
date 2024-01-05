/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service.bitmap;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.core.common.DimForkJoinPool;
import io.holoinsight.server.meta.core.common.FilterUtil;
import io.holoinsight.server.meta.core.service.SqlDataCoreService;
import io.holoinsight.server.meta.core.service.bitmap.condition.MetaCondition;
import io.holoinsight.server.meta.core.service.bitmap.execption.NotForeignKeyException;
import io.holoinsight.server.meta.dal.service.mapper.MetaDataMapper;
import io.holoinsight.server.meta.dal.service.model.MetaDataDO;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BitmapDataCoreService extends SqlDataCoreService {

  public static final String UK_FIELD = "__meta_uk";

  public static final Logger logger = LoggerFactory.getLogger(BitmapDataCoreService.class);

  private static final ListeningExecutorService RELOADER = MoreExecutors.listeningDecorator(
      new ThreadPoolExecutor(8, 8, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
          new BasicThreadFactory.Builder().namingPattern("meta-reload-%d").build()));

  private LoadingCache<String, AbstractMetaData> metaDatas = CacheBuilder.newBuilder()
      .expireAfterAccess(60, TimeUnit.MINUTES).build(new CacheLoader<String, AbstractMetaData>() {
        @Override
        public AbstractMetaData load(String tableName) {
          return buildMetaData(tableName);
        }

        @Override
        public ListenableFuture<AbstractMetaData> reload(String tableName,
            AbstractMetaData oldData) {
          return RELOADER.submit(() -> buildMetaData(tableName));
        }
      });

  private Map<String, Map<String, ForeignKey>> foreignKeys = new HashMap<>();

  @Autowired
  private MetaCptCenter metaCptCenter;

  public BitmapDataCoreService(MetaDataMapper metaDataMapper, SuperCacheService superCacheService) {
    super(metaDataMapper, superCacheService);
    logger.info("meta service initialized, engine=bitmap");
  }

  private AbstractMetaData buildMetaData(String tableName) {
    long version = System.currentTimeMillis() / 1000 * 1000 - 1000;
    List<MetaDataRow> rows = fromMetaDatas(loadFromDB(tableName, version));
    return new ServerMetaData(tableName, version, rows,
        (table, start, end) -> fromMetaDatas(BitmapDataCoreService.super.queryTableChangedMeta(
            table, new Date(start), new Date(end), true)));
  }

  private List<MetaDataDO> loadFromDB(String tableName, long version) {
    return queryTableChangedMeta(tableName, new Date(0), new Date(version), false);
  }

  public AbstractMetaData getMetaData(String tableName) {
    AbstractMetaData metaData = this.metaDatas.getUnchecked(tableName);
    if (metaData.isExpired()) {
      this.metaDatas.refresh(tableName);
    }
    Assert.notNull(metaData, String.format("table not found: %s", tableName));
    return metaData;
  }

  public ForeignKey getForeignKey(String tableName, String field) {
    return foreignKeys.getOrDefault(tableName, new HashMap<>()).get(field);
  }

  private List<MetaDataRow> fromMetaDatas(List<MetaDataDO> metaDatas) {
    return DimForkJoinPool.get().submit(() -> metaDatas.parallelStream().map(metaData -> {
      Map<String, Object> values = J.toMap(metaData.getJson());
      values.put(UK_FIELD, metaData.getUk());
      return new MetaDataRow(metaData.getTableName(), metaData.getId(), metaData.getUk(), values,
          metaData.getDeleted() != null && metaData.getDeleted() == 1,
          metaData.getGmtModified().getTime());
    }).collect(Collectors.toList())).join();
  }

  @Override
  public List<Map<String, Object>> queryByTable(String tableName) {
    return queryByTable(tableName, Lists.newArrayList());
  }

  @Override
  public List<Map<String, Object>> queryByTable(String tableName, List<String> rowKeys) {
    logger.info("[queryByTable] start, table={}, rowsKeys={}.", tableName, rowKeys);
    StopWatch stopWatch = StopWatch.createStarted();
    AbstractMetaData metaData = getMetaData(tableName);
    Collection<MetaDataRow> rows;
    rows = metaData.getRowsMap().values();
    List<Map<String, Object>> result = toValMap(rows, rowKeys);
    logger.info("[queryByTable] finish, table={}, records={}, cost={}.", tableName, result.size(),
        stopWatch.getTime());
    return result;
  }

  @Override
  public List<Map<String, Object>> queryByPks(String tableName, List<String> pkValList) {
    logger.info("[queryByPks] start, table={}, pkValList={}.", tableName, pkValList.size());
    AbstractMetaData metaData = getMetaData(tableName);
    if (CollectionUtils.isEmpty(pkValList)) {
      return Lists.newArrayList();
    }
    StopWatch stopWatch = StopWatch.createStarted();
    List<Map<String, Object>> result = toValMap(metaData.getByPks(pkValList), null);
    logger.info("[queryByPks] finish, table={}, records={}, cost={}.", tableName, result.size(),
        stopWatch.getTime());
    return result;
  }

  @Override
  public List<Map<String, Object>> queryByExample(String tableName, QueryExample queryExample) {
    logger.info("[queryByExample] start, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    MetaCondition metaCondition = FilterUtil.buildDimCondition(queryExample, false);
    List<Map<String, Object>> rows = null;
    try {
      rows = toValMap(metaCptCenter.computeByCondition(tableName, metaCondition, false),
          queryExample.getRowKeys());
    } catch (NotForeignKeyException e) {
      throw new RuntimeException(e);
    }
    logger.info("[queryByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return rows;
  }

  @Override
  public List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample queryExample) {
    logger.info("[fuzzyByExample] start, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    MetaCondition metaCondition = FilterUtil.buildDimCondition(queryExample, true);
    List<Map<String, Object>> rows = null;
    try {
      rows = toValMap(metaCptCenter.computeByCondition(tableName, metaCondition, false),
          queryExample.getRowKeys());
    } catch (NotForeignKeyException e) {
      throw new RuntimeException(e);
    }
    logger.info("[fuzzyByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return rows;
  }

  private List<Map<String, Object>> toValMap(Collection<MetaDataRow> rows, List<String> rowKeys) {
    Stream<Map<String, Object>> stream = rows.parallelStream().map(MetaDataRow::getValues);
    if (!CollectionUtils.isEmpty(rowKeys)) {
      stream = stream
          .map(data -> data.entrySet().stream().filter(entry -> rowKeys.contains(entry.getKey()))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }
    return stream.collect(Collectors.toList());
  }

}
