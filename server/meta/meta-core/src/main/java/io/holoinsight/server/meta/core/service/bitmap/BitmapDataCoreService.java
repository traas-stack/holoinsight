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
import io.holoinsight.server.meta.common.util.ConstModel;
import io.holoinsight.server.meta.core.common.DimForkJoinPool;
import io.holoinsight.server.meta.core.common.FilterUtil;
import io.holoinsight.server.meta.core.service.SqlDataCoreService;
import io.holoinsight.server.meta.core.service.bitmap.condition.DimCondition;
import io.holoinsight.server.meta.core.service.bitmap.execption.NotForeignKeyException;
import io.holoinsight.server.meta.dal.service.mapper.MetaDataMapper;
import io.holoinsight.server.meta.dal.service.model.MetaData;
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

public class BitmapDataCoreService extends SqlDataCoreService {

  public static final String UK_FIELD = "__meta_uk";

  public static final Logger logger = LoggerFactory.getLogger(BitmapDataCoreService.class);

  private static final ListeningExecutorService RELOADER = MoreExecutors.listeningDecorator(
      new ThreadPoolExecutor(8, 8, 60L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
          new BasicThreadFactory.Builder().namingPattern("meta-reload-%d").build()));

  private LoadingCache<String, AbstractDimData> metaDatas = CacheBuilder.newBuilder()
      .expireAfterAccess(60, TimeUnit.MINUTES).build(new CacheLoader<String, AbstractDimData>() {
        @Override
        public AbstractDimData load(String tableName) {
          return buildMetaData(tableName);
        }

        @Override
        public ListenableFuture<AbstractDimData> reload(String tableName, AbstractDimData oldData) {
          return RELOADER.submit(() -> buildMetaData(tableName));
        }
      });

  private Map<String, Map<String, ForeignKey>> foreignKeys = new HashMap<>();

  @Autowired
  private MetaCptCenter metaCptCenter;

  public BitmapDataCoreService(MetaDataMapper metaDataMapper, SuperCacheService superCacheService) {
    super(metaDataMapper, superCacheService);
  }

  private AbstractDimData buildMetaData(String tableName) {
    long version = System.currentTimeMillis() / 1000 * 1000 - 1000;
    List<DimDataRow> rows = fromMetaDatas(loadFromDB(tableName, version));
    return new ServerDimData(tableName, version, rows,
        (table, start, end) -> fromMetaDatas(BitmapDataCoreService.super.queryTableChangedMeta(
            table, new Date(start), new Date(end), false)));
  }

  private List<MetaData> loadFromDB(String tableName, long version) {
    return queryTableChangedMeta(tableName, new Date(0), new Date(version), false);
  }

  public AbstractDimData getMetaData(String tableName) {
    AbstractDimData dimData = this.metaDatas.getUnchecked(tableName);
    if (dimData.isExpired()) {
      this.metaDatas.refresh(tableName);
    }
    Assert.notNull(dimData, String.format("table not found: %s", tableName));
    return dimData;
  }

  public ForeignKey getForeignKey(String tableName, String field) {
    return foreignKeys.getOrDefault(tableName, new HashMap<>()).get(field);
  }

  private List<DimDataRow> fromMetaDatas(List<MetaData> metaDatas) {
    return DimForkJoinPool.get().submit(() -> metaDatas.parallelStream().map(metaData -> {
      Map<String, Object> values = J.toMap(metaData.getJson());
      values.put(UK_FIELD, metaData.getUk());
      return new DimDataRow(metaData.getTableName(), metaData.getId(), metaData.getUk(), values,
          metaData.getDeleted()!=null&& metaData.getDeleted()== 1, metaData.getGmtModified().getTime());
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
    AbstractDimData dimData = getMetaData(tableName);
    Collection<DimDataRow> rows;
    if (!CollectionUtils.isEmpty(rowKeys)) {
      rows = dimData.getByPks(rowKeys);
    } else {
      rows = dimData.getRowsMap().values();
    }
    List<Map<String, Object>> result = toValMap(rows);
    logger.info("[queryByTable] finish, table={}, records={}, cost={}.", tableName, result.size(),
        stopWatch.getTime());
    return result;
  }

  @Override
  public List<Map<String, Object>> queryByPks(String tableName, List<String> pkValList) {
    logger.info("[queryByPks] start, table={}, pkValList={}.", tableName, pkValList.size());
    AbstractDimData dimData = getMetaData(tableName);
    Collection<DimDataRow> rows;
    if (CollectionUtils.isEmpty(pkValList)) {
      return Lists.newArrayList();
    }
    StopWatch stopWatch = StopWatch.createStarted();
    List<Map<String, Object>> result = toValMap(dimData.getByPks(pkValList));
    logger.info("[queryByPks] finish, table={}, records={}, cost={}.", tableName, result.size(),
        stopWatch.getTime());
    return result;
  }

  @Override
  public List<Map<String, Object>> queryByExample(String tableName, QueryExample queryExample) {
    logger.info("[queryByExample] start, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    DimCondition dimCondition = FilterUtil.buildDimCondition(queryExample, false);
    List<Map<String, Object>> rows = null;
    try {
      rows = toValMap(metaCptCenter.computeByCondition(tableName, dimCondition, false));
    } catch (NotForeignKeyException e) {
      throw new RuntimeException(e);
    }
    logger.info("[queryByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return rows;
  }

  @Override
  public Collection<Map<String, Object>> getMetaDataFromCache(String tableName, QueryExample queryExample,
                                                              Map<String, Map<String, Object>> ukToRowCache) {
    return queryByExample(tableName,queryExample);
  }

  @Override
  public List<Map<String, Object>> fuzzyByExample(String tableName, QueryExample queryExample) {
    logger.info("[fuzzyByExample] start, table={}, queryExample={}.", tableName,
        J.toJson(queryExample));
    StopWatch stopWatch = StopWatch.createStarted();
    DimCondition dimCondition = FilterUtil.buildDimCondition(queryExample, true);
    List<Map<String, Object>> rows = null;
    try {
      rows = toValMap(metaCptCenter.computeByCondition(tableName, dimCondition, false));
    } catch (NotForeignKeyException e) {
      throw new RuntimeException(e);
    }
    logger.info("[fuzzyByExample] finish, table={}, records={}, cost={}.", tableName, rows.size(),
        stopWatch.getTime());
    return rows;
  }

  private List<Map<String, Object>> toValMap(Collection<DimDataRow> rows) {
    return rows.parallelStream().map(DimDataRow::getValues).collect(Collectors.toList());
  }

}
