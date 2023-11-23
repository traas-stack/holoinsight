/**
 * Alipay.com Inc. Copyright (c) 2004-2019 All Rights Reserved.
 */
package io.holoinsight.server.meta.core.service.bitmap;

import com.google.common.collect.Sets;
import com.googlecode.javaewah.EWAHCompressedBitmap;
import io.holoinsight.server.meta.core.service.bitmap.condition.AndCondition;
import io.holoinsight.server.meta.core.service.bitmap.condition.DimCondition;
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
 * @author wanpeng.xwp
 * @version : MetaCptCenter.java, v 0.1 2019年12月05日 20:16 wanpeng.xwp Exp $
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
  public boolean match(String tableName, DimCondition condition, Object ukVal,
      boolean containsDelete) throws NotForeignKeyException {
    AbstractDimData dimData = bitmapDataCoreService.getMetaData(tableName);
    EWAHCompressedBitmap conditionBitmap =
        computeBitmapByCondition(tableName, dimData, condition, containsDelete);
    EWAHCompressedBitmap pkBitmap =
        computeBitmapByIndex(dimData, Collections.singletonMap(UK_FIELD, ukVal));
    return !(conditionBitmap == null || pkBitmap == null
        || conditionBitmap.and(pkBitmap).isEmpty());
  }

  /**
   * 判断指定条件是否匹配复合查询条件
   *
   * @param condition 复合查询条件
   * @return 符合条件的维度数据行
   */
  public boolean matchByIndex(String tableName, DimCondition condition, Map<String, Object> index,
      boolean containsDelete) throws NotForeignKeyException {
    AbstractDimData dimData = bitmapDataCoreService.getMetaData(tableName);
    EWAHCompressedBitmap conditionBitmap =
        computeBitmapByCondition(tableName, dimData, condition, containsDelete);
    EWAHCompressedBitmap indexBitmap = computeBitmapByIndex(dimData, index);
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
  public List<DimDataRow> computeByCondition(String tableName, DimCondition condition,
      boolean containsDelete) throws NotForeignKeyException {
    AbstractDimData dimData = bitmapDataCoreService.getMetaData(tableName);
    EWAHCompressedBitmap resultBitmap =
        computeBitmapByCondition(tableName, dimData, condition, containsDelete);
    return computeByBitmap(dimData, resultBitmap, containsDelete);
  }

  /**
   * 根据条件计算满足的维度数据行
   *
   * @param condition 查询条件
   * @return 符合条件的维度数据行
   */
  public List<DimDataRow> computeByIndex(String tableName, Map<String, Object> condition,
      boolean containsDelete) {
    AbstractDimData dimData = bitmapDataCoreService.getMetaData(tableName);
    if (condition == null || condition.isEmpty()) {
      throw new IllegalArgumentException(
          String.format("invalid query condition, table=%s, condition=%s.", tableName, condition));
    }
    EWAHCompressedBitmap resultBitmap = computeBitmapByIndex(dimData, condition);
    return computeByBitmap(dimData, resultBitmap, containsDelete);
  }

  /**
   * 模糊查询
   *
   * @param tableName
   * @param dimData
   * @param condition
   * @param containsDelete
   * @return
   */
  public List<DimDataRow> fuzzyCompute(String tableName, AbstractDimData dimData,
      Map<String, String> condition, boolean containsDelete) {
    EWAHCompressedBitmap resultBitmap = computeFuzzyBitmap(tableName, dimData, condition, false);
    return resultBitmap == null ? null : computeByBitmap(dimData, resultBitmap, containsDelete);
  }

  private EWAHCompressedBitmap computeFuzzyBitmap(String tableName, AbstractDimData dimData,
      Map<String, String> condition, boolean isRegex) {
    if (condition == null) {
      return null;
    }
    List<EWAHCompressedBitmap> fuzzyBitmaps = new ArrayList<>();
    for (Entry<String, String> entry : condition.entrySet()) {
      String key = entry.getKey();
      String val = entry.getValue();
      DimColData dimColData = dimData.getColsMap().get(key);
      if (dimColData == null) {
        return emptyBitmap();
      } else {
        List<EWAHCompressedBitmap> batchFuzzyBitmaps = dimColData.fuzzyGetBitmap(val, isRegex);
        fuzzyBitmaps.addAll(batchFuzzyBitmaps);
      }
    }
    EWAHCompressedBitmap resultBitmap = bitmapOr(fuzzyBitmaps);
    return resultBitmap;
  }

  public List<DimDataRow> computeByBitmap(AbstractDimData dimData,
      EWAHCompressedBitmap resultBitmap, boolean containsDelete) {
    if (containsDelete) {
      return computeByBitmapWithDelete(dimData, resultBitmap);
    } else {
      return computeByBitmapWithoutDelete(dimData, resultBitmap);
    }
  }

  public List<DimDataRow> computeByBitmapWithDelete(AbstractDimData dimData,
      EWAHCompressedBitmap resultBitmap) {
    List<DimDataRow> resultDimDataRows = new ArrayList<>();
    for (int i : resultBitmap.toArray()) {
      DimDataRow dimDataRow = dimData.getAllLogs().get(i);
      resultDimDataRows.add(dimDataRow);
    }
    return resultDimDataRows;
  }

  public List<DimDataRow> computeByBitmapWithoutDelete(AbstractDimData dimData,
      EWAHCompressedBitmap resultBitmap) {
    Map<Object, DimDataRow> resultDimDataRows = new HashMap<>();
    for (int i : resultBitmap.toArray()) {
      DimDataRow dimDataRow = dimData.getAllLogs().get(i);
      if (dimDataRow.equals(dimData.getByUk(dimDataRow.getUk()))) {
        resultDimDataRows.put(dimDataRow.getUk(), dimDataRow);
      }
    }
    return new ArrayList<>(resultDimDataRows.values());
  }

  /**
   * 计算符合条件的下标位图
   *
   * @param dimData 维度数据
   * @param condition 普通查询条件，各组条件之间是与的关系
   * @return 符合条件的下标位图
   */
  public EWAHCompressedBitmap computeBitmapByIndex(AbstractDimData dimData,
      Map<String, Object> condition) {
    if (condition == null || condition.isEmpty()) {
      return emptyBitmap();
    }
    List<EWAHCompressedBitmap> andBitmaps = new ArrayList<>();
    for (Entry<String, Object> entry : condition.entrySet()) {
      String key = entry.getKey();
      Object val = entry.getValue();
      DimColData dimColData = dimData.getColsMap().get(key);
      if (dimColData == null) {
        return emptyBitmap();
      } else {
        EWAHCompressedBitmap bitmap = computeBitmapSingle(dimData, dimColData, key, val);
        andBitmaps.add(bitmap);
      }
    }
    return bitmapAnd(andBitmaps);
  }

  /**
   * 查询一个条件
   *
   * @param dimData 维度数据
   * @param dimColData 维度列数据
   * @param key 条件列
   * @param val 条件值
   * @return 符合条件的下标位图
   */
  private EWAHCompressedBitmap computeBitmapSingle(AbstractDimData dimData, DimColData dimColData,
      String key, Object val) {
    if (val == null) {
      return emptyBitmap();
    }
    if (val instanceof List) {
      return computeBitmapBatch(dimData, dimColData, key, new HashSet<>((List<Object>) val));
    } else {
      EWAHCompressedBitmap bitmap = dimColData.getBitmapOrDefault(val, emptyBitmap());
      return bitmap;
    }
  }

  /**
   * 查询一批条件
   *
   * @param dimData 维度数据
   * @param dimColData 维度列数据
   * @param key 条件列
   * @param vals 条件值集合，集合内条件值之间是或的关系
   * @return 符合条件的下标位图
   */
  private EWAHCompressedBitmap computeBitmapBatch(AbstractDimData dimData, DimColData dimColData,
      String key, Set<Object> vals) {
    int valsCount = vals.size();
    int totalCount = dimColData.getCount();
    double quotient = (double) valsCount / (double) totalCount;
    // 批量查询的val超过总val量的70%，正向查询可能导致海量的bitmap计算，改为反向查询
    if (quotient > 0.7) {
      Set<Object> inverseVals = Sets.difference(dimColData.getVal2BitMap().keySet(), vals);
      EWAHCompressedBitmap inverseBitmap =
          computeBitmapBatch(dimData, dimColData, key, inverseVals);
      return computeNotBitmap(inverseBitmap, dimData);
    } else {
      List<EWAHCompressedBitmap> orBitmaps = new ArrayList<>();
      for (Object val : vals) {
        EWAHCompressedBitmap bitmap = computeBitmapSingle(dimData, dimColData, key, val);
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
   * @param dimData
   * @return
   */
  private EWAHCompressedBitmap computeFullBitmap(AbstractDimData dimData) {
    int size = dimData.getAllLogs().size();
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
   * @param dimData
   * @return
   */
  private EWAHCompressedBitmap computeNotBitmap(EWAHCompressedBitmap bitmap,
      AbstractDimData dimData) {
    EWAHCompressedBitmap fullBitmap = computeFullBitmap(dimData);
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

  private EWAHCompressedBitmap computeBitmapByCondition(String tableName, AbstractDimData dimData,
      DimCondition dimCondition, boolean containsDelete) throws NotForeignKeyException {
    List<OrCondition> orConditions = dimCondition.getOrConditions();
    List<EWAHCompressedBitmap> orBitmaps = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(orConditions)) {
      for (OrCondition orCondition : orConditions) {
        List<AndCondition> andConditions = orCondition.getAndConditions();
        List<EWAHCompressedBitmap> andBitmaps = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(andConditions)) {
          for (AndCondition andCondition : andConditions) {
            EWAHCompressedBitmap andBitmap =
                computeBitmapByOneCondition(tableName, dimData, andCondition, containsDelete);
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
      AbstractDimData dimData, AndCondition andCondition, boolean containsDelete)
      throws NotForeignKeyException {
    EWAHCompressedBitmap resultBitmap;
    if (andCondition.isAll()) {
      resultBitmap = computeFullBitmap(dimData);
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
      resultBitmap = computeFuzzyBitmap(tableName, dimData,
          Collections.singletonMap(key, String.valueOf(val)), true);
    } else {
      resultBitmap = computeBitmapByRange(tableName, dimData, andCondition.getValueRange(),
          containsDelete, andCondition.getExpress());
    }
    if (andCondition.isNot()) {
      resultBitmap = computeNotBitmap(resultBitmap, dimData);
    }
    return resultBitmap;
  }

  /**
   * 根据path路径计算位图
   *
   * @param tableName
   * @param dimData
   * @param values
   * @param tag
   * @return
   * @throws NotForeignKeyException
   */
  private EWAHCompressedBitmap computeBitmapByRange(String tableName, AbstractDimData dimData,
      List<Object> values, boolean containsDelete, String... tag) throws NotForeignKeyException {
    String childName = tag[0];
    if (tag.length == 1) {
      return computeBitmapByIndex(dimData, Collections.singletonMap(childName, values));
    }
    ForeignKey foreignKey = bitmapDataCoreService.getForeignKey(tableName, childName);
    if (foreignKey == null) {
      throw new NotForeignKeyException(tableName, childName);
    }
    String refTable = foreignKey.getRefTable();
    AbstractDimData refDimData = bitmapDataCoreService.getMetaData(refTable);
    EWAHCompressedBitmap childBitmap = computeBitmapByRange(refTable, refDimData, values,
        containsDelete, Arrays.copyOfRange(tag, 1, tag.length));
    List<DimDataRow> childDimDataRows = computeByBitmap(refDimData, childBitmap, containsDelete);
    List<Object> newValues =
        childDimDataRows.parallelStream().map(DimDataRow::getUk).collect(Collectors.toList());
    return computeBitmapByRange(tableName, dimData, newValues, containsDelete, childName);
  }

}
