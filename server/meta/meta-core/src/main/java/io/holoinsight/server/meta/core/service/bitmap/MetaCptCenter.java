/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.core.service.bitmap;

import com.google.common.collect.Sets;
import com.googlecode.javaewah.EWAHCompressedBitmap;
import io.holoinsight.server.meta.core.service.bitmap.condition.AndCondition;
import io.holoinsight.server.meta.core.service.bitmap.condition.MetaCondition;
import io.holoinsight.server.meta.core.service.bitmap.condition.OrCondition;
import io.holoinsight.server.meta.core.service.bitmap.execption.NotForeignKeyException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import static io.holoinsight.server.meta.core.service.bitmap.BitmapDataCoreService.UK_FIELD;

/**
 * 元数据计算中心
 *
 * @author xiangwanpeng
 * @version : MetaCptCenter.java, v 0.1 2019年12月05日 20:16 xiangwanpeng Exp $
 */
@Slf4j
@Service
public class MetaCptCenter {

  @Autowired
  @Lazy
  private BitmapDataCoreService bitmapDataCoreService;

  /**
   * 判断指定记录是否匹配复合查询条件
   *
   * @param condition 复合查询条件
   * @return 符合条件的维度数据行
   */
  public boolean match(String tableName, MetaCondition condition, Object ukVal,
      boolean containsDelete) throws NotForeignKeyException {
    AbstractMetaData metaData = bitmapDataCoreService.getMetaData(tableName);
    EWAHCompressedBitmap conditionBitmap =
        computeBitmapByCondition(tableName, metaData, condition, containsDelete);
    EWAHCompressedBitmap pkBitmap =
        computeBitmapByIndex(metaData, Collections.singletonMap(UK_FIELD, ukVal));
    return !(conditionBitmap == null || pkBitmap == null
        || conditionBitmap.and(pkBitmap).isEmpty());
  }

  /**
   * 判断指定条件是否匹配复合查询条件
   *
   * @param condition 复合查询条件
   * @return 符合条件的维度数据行
   */
  public boolean matchByIndex(String tableName, MetaCondition condition, Map<String, Object> index,
      boolean containsDelete) throws NotForeignKeyException {
    AbstractMetaData metaData = bitmapDataCoreService.getMetaData(tableName);
    EWAHCompressedBitmap conditionBitmap =
        computeBitmapByCondition(tableName, metaData, condition, containsDelete);
    EWAHCompressedBitmap indexBitmap = computeBitmapByIndex(metaData, index);
    return !(conditionBitmap == null || indexBitmap == null
        || conditionBitmap.and(indexBitmap).isEmpty());
  }

  /**
   * 根据复合查询条件计算满足的维度数据行
   *
   * @param tableName 维表名
   * @param condition 复合查询条件
   * @return 符合条件的维度数据行
   */
  public List<MetaDataRow> computeByCondition(String tableName, MetaCondition condition,
      boolean containsDelete) throws NotForeignKeyException {
    AbstractMetaData metaData = bitmapDataCoreService.getMetaData(tableName);
    EWAHCompressedBitmap resultBitmap =
        computeBitmapByCondition(tableName, metaData, condition, containsDelete);
    return computeByBitmap(metaData, resultBitmap, containsDelete);
  }

  /**
   * 根据条件计算满足的维度数据行
   *
   * @param condition 查询条件
   * @return 符合条件的维度数据行
   */
  public List<MetaDataRow> computeByIndex(String tableName, Map<String, Object> condition,
      boolean containsDelete) {
    AbstractMetaData metaData = bitmapDataCoreService.getMetaData(tableName);
    if (condition == null || condition.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("invalid query condition, table=%s, condition=%s.", tableName, condition));
    }
    EWAHCompressedBitmap resultBitmap = computeBitmapByIndex(metaData, condition);
    return computeByBitmap(metaData, resultBitmap, containsDelete);
  }

  /**
   * 模糊查询
   *
   * @param tableName
   * @param metaData
   * @param condition
   * @param containsDelete
   * @return
   */
  public List<MetaDataRow> fuzzyCompute(String tableName, AbstractMetaData metaData,
      Map<String, String> condition, boolean containsDelete) {
    EWAHCompressedBitmap resultBitmap = computeFuzzyBitmap(tableName, metaData, condition, false);
    return resultBitmap == null ? null : computeByBitmap(metaData, resultBitmap, containsDelete);
  }

  private EWAHCompressedBitmap computeFuzzyBitmap(String tableName, AbstractMetaData metaData,
      Map<String, String> condition, boolean isRegex) {
    if (condition == null) {
      return null;
    }
    List<EWAHCompressedBitmap> fuzzyBitmaps = new ArrayList<>();
    for (Entry<String, String> entry : condition.entrySet()) {
      String key = entry.getKey();
      String val = entry.getValue();
      MetaColData metaColData = metaData.getColsMap().get(key);
      if (metaColData == null) {
        return emptyBitmap();
      } else {
        List<EWAHCompressedBitmap> batchFuzzyBitmaps = metaColData.fuzzyGetBitmap(val, isRegex);
        fuzzyBitmaps.addAll(batchFuzzyBitmaps);
      }
    }
    EWAHCompressedBitmap resultBitmap = bitmapOr(fuzzyBitmaps);
    return resultBitmap;
  }

  public List<MetaDataRow> computeByBitmap(AbstractMetaData metaData,
      EWAHCompressedBitmap resultBitmap, boolean containsDelete) {
    if (containsDelete) {
      return computeByBitmapWithDelete(metaData, resultBitmap);
    } else {
      return computeByBitmapWithoutDelete(metaData, resultBitmap);
    }
  }

  public List<MetaDataRow> computeByBitmapWithDelete(AbstractMetaData metaData,
      EWAHCompressedBitmap resultBitmap) {
    List<MetaDataRow> resultMetaDataRows = new ArrayList<>();
    for (int i : resultBitmap.toArray()) {
      MetaDataRow metaDataRow = metaData.getAllLogs().get(i);
      resultMetaDataRows.add(metaDataRow);
    }
    return resultMetaDataRows;
  }

  public List<MetaDataRow> computeByBitmapWithoutDelete(AbstractMetaData metaData,
      EWAHCompressedBitmap resultBitmap) {
    Map<Object, MetaDataRow> resultmetaDataRows = new HashMap<>();
    for (int i : resultBitmap.toArray()) {
      MetaDataRow metaDataRow = metaData.getAllLogs().get(i);
      if (metaDataRow.equals(metaData.getByUk(metaDataRow.getUk()))) {
        resultmetaDataRows.put(metaDataRow.getUk(), metaDataRow);
      }
    }
    return new ArrayList<>(resultmetaDataRows.values());
  }

  /**
   * 计算符合条件的下标位图
   *
   * @param metaData 维度数据
   * @param condition 普通查询条件，各组条件之间是与的关系
   * @return 符合条件的下标位图
   */
  public EWAHCompressedBitmap computeBitmapByIndex(AbstractMetaData metaData,
      Map<String, Object> condition) {
    if (condition == null || condition.isEmpty()) {
      return emptyBitmap();
    }
    List<EWAHCompressedBitmap> andBitmaps = new ArrayList<>();
    for (Entry<String, Object> entry : condition.entrySet()) {
      String key = entry.getKey();
      Object val = entry.getValue();
      MetaColData metaColData = metaData.getColsMap().get(key);
      if (metaColData == null) {
        return emptyBitmap();
      } else {
        EWAHCompressedBitmap bitmap = computeBitmapSingle(metaData, metaColData, key, val);
        andBitmaps.add(bitmap);
      }
    }
    return bitmapAnd(andBitmaps);
  }

  /**
   * 查询一个条件
   *
   * @param metaData 维度数据
   * @param metaColData 维度列数据
   * @param key 条件列
   * @param val 条件值
   * @return 符合条件的下标位图
   */
  private EWAHCompressedBitmap computeBitmapSingle(AbstractMetaData metaData,
      MetaColData metaColData, String key, Object val) {
    if (val == null) {
      return emptyBitmap();
    }
    if (val instanceof List) {
      return computeBitmapBatch(metaData, metaColData, key, new HashSet<>((List<Object>) val));
    } else {
      EWAHCompressedBitmap bitmap = metaColData.getBitmapOrDefault(val, emptyBitmap());
      return bitmap;
    }
  }

  /**
   * 查询一批条件
   *
   * @param metaData 维度数据
   * @param metaColData 维度列数据
   * @param key 条件列
   * @param vals 条件值集合，集合内条件值之间是或的关系
   * @return 符合条件的下标位图
   */
  private EWAHCompressedBitmap computeBitmapBatch(AbstractMetaData metaData,
      MetaColData metaColData, String key, Set<Object> vals) {
    int valsCount = vals.size();
    int totalCount = metaColData.getCount();
    double quotient = (double) valsCount / (double) totalCount;
    // 批量查询的val超过总val量的70%，正向查询可能导致海量的bitmap计算，改为反向查询
    if (quotient > 0.7) {
      Set<Object> inverseVals = Sets.difference(metaColData.getVal2BitMap().keySet(), vals);
      EWAHCompressedBitmap inverseBitmap =
          computeBitmapBatch(metaData, metaColData, key, inverseVals);
      return computeNotBitmap(inverseBitmap, metaData);
    } else {
      List<EWAHCompressedBitmap> orBitmaps = new ArrayList<>();
      for (Object val : vals) {
        EWAHCompressedBitmap bitmap = computeBitmapSingle(metaData, metaColData, key, val);
        orBitmaps.add(bitmap);
      }
      return bitmapOr(orBitmaps);
    }
  }

  /**
   * 获取空bitmap
   *
   * @return
   */
  private EWAHCompressedBitmap emptyBitmap() {
    return EWAHCompressedBitmap.bitmapOf();
  }

  /**
   * 获取全置位1的bitmap
   *
   * @param metaData
   * @return
   */
  private EWAHCompressedBitmap computeFullBitmap(AbstractMetaData metaData) {
    int size = metaData.getAllLogs().size();
    if (size == 0) {
      return emptyBitmap();
    }
    EWAHCompressedBitmap bitmap = EWAHCompressedBitmap.bitmapOf(size);
    bitmap.not();
    return bitmap;
  }

  /**
   * 取反
   *
   * @param bitmap
   * @param metaData
   * @return
   */
  private EWAHCompressedBitmap computeNotBitmap(EWAHCompressedBitmap bitmap,
      AbstractMetaData metaData) {
    EWAHCompressedBitmap fullBitmap = computeFullBitmap(metaData);
    return EWAHCompressedBitmap.xor(fullBitmap, bitmap);
  }

  private EWAHCompressedBitmap bitmapAnd(List<EWAHCompressedBitmap> bitmaps) {
    if (CollectionUtils.isEmpty(bitmaps)) {
      return emptyBitmap();
    } else if (bitmaps.size() == 1) {
      try {
        return bitmaps.get(0).clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    } else {
      return EWAHCompressedBitmap.and(bitmaps.toArray(new EWAHCompressedBitmap[bitmaps.size()]));
    }
  }

  private EWAHCompressedBitmap bitmapOr(List<EWAHCompressedBitmap> bitmaps) {
    if (CollectionUtils.isEmpty(bitmaps)) {
      return emptyBitmap();
    } else if (bitmaps.size() == 1) {
      try {
        return bitmaps.get(0).clone();
      } catch (CloneNotSupportedException e) {
        throw new RuntimeException(e);
      }
    } else {
      return EWAHCompressedBitmap.or(bitmaps.toArray(new EWAHCompressedBitmap[bitmaps.size()]));
    }
  }

  private EWAHCompressedBitmap computeBitmapByCondition(String tableName, AbstractMetaData metaData,
      MetaCondition metaCondition, boolean containsDelete) throws NotForeignKeyException {
    List<OrCondition> orConditions = metaCondition.getOrConditions();
    List<EWAHCompressedBitmap> orBitmaps = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(orConditions)) {
      for (OrCondition orCondition : orConditions) {
        List<AndCondition> andConditions = orCondition.getAndConditions();
        List<EWAHCompressedBitmap> andBitmaps = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(andConditions)) {
          for (AndCondition andCondition : andConditions) {
            EWAHCompressedBitmap andBitmap =
                computeBitmapByOneCondition(tableName, metaData, andCondition, containsDelete);
            andBitmaps.add(andBitmap);
          }
        }
        EWAHCompressedBitmap andBitmapResult = bitmapAnd(andBitmaps);
        orBitmaps.add(andBitmapResult);
      }
    }
    EWAHCompressedBitmap resultBitmap = bitmapOr(orBitmaps);
    return resultBitmap;
  }

  private EWAHCompressedBitmap computeBitmapByOneCondition(String tableName,
      AbstractMetaData metaData, AndCondition andCondition, boolean containsDelete)
      throws NotForeignKeyException {
    EWAHCompressedBitmap resultBitmap;
    if (andCondition.isAll()) {
      resultBitmap = computeFullBitmap(metaData);
    } else if (andCondition.isRegex()) {
      String[] express = andCondition.getExpress();
      if (express == null || express.length > 1) {
        throw new IllegalArgumentException("REGEX not support cascade expr");
      }
      String key = express[0];
      List<Object> vals = andCondition.getValueRange();
      if (CollectionUtils.isEmpty(vals) || vals.size() > 1) {
        throw new IllegalArgumentException("REGEX not support multi values");
      }
      Object val = vals.get(0);
      resultBitmap = computeFuzzyBitmap(tableName, metaData,
          Collections.singletonMap(key, String.valueOf(val)), true);
    } else {
      resultBitmap = computeBitmapByRange(tableName, metaData, andCondition.getValueRange(),
          containsDelete, andCondition.getExpress());
    }
    if (andCondition.isNot()) {
      resultBitmap = computeNotBitmap(resultBitmap, metaData);
    }
    return resultBitmap;
  }

  /**
   * 根据path路径计算位图
   *
   * @param tableName
   * @param metaData
   * @param values
   * @param tag
   * @return
   * @throws NotForeignKeyException
   */
  private EWAHCompressedBitmap computeBitmapByRange(String tableName, AbstractMetaData metaData,
      List<Object> values, boolean containsDelete, String... tag) throws NotForeignKeyException {
    String childName = tag[0];
    if (tag.length == 1) {
      return computeBitmapByIndex(metaData, Collections.singletonMap(childName, values));
    }
    ForeignKey foreignKey = bitmapDataCoreService.getForeignKey(tableName, childName);
    if (foreignKey == null) {
      throw new NotForeignKeyException(tableName, childName);
    }
    String refTable = foreignKey.getRefTable();
    AbstractMetaData refmetaData = bitmapDataCoreService.getMetaData(refTable);
    EWAHCompressedBitmap childBitmap = computeBitmapByRange(refTable, refmetaData, values,
        containsDelete, Arrays.copyOfRange(tag, 1, tag.length));
    List<MetaDataRow> childMetaDataRows = computeByBitmap(refmetaData, childBitmap, containsDelete);
    List<Object> newValues =
        childMetaDataRows.parallelStream().map(MetaDataRow::getUk).collect(Collectors.toList());
    return computeBitmapByRange(tableName, metaData, newValues, containsDelete, childName);
  }

}
