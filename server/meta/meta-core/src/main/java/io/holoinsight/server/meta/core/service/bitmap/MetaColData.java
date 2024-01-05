/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.meta.core.service.bitmap;

import com.googlecode.javaewah.EWAHCompressedBitmap;
import io.holoinsight.server.meta.core.common.DimForkJoinPool;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@Getter
@Setter
@ToString(callSuper = true)
@Slf4j
public class MetaColData {

  private String tableName;

  private String colName;

  private Map<Object, EWAHCompressedBitmap> val2BitMap = new ConcurrentSkipListMap<>();

  private AtomicInteger logSizeCounter = new AtomicInteger(0);

  private AtomicInteger valSizeCounter = new AtomicInteger(0);

  private AtomicBoolean loaded = new AtomicBoolean(false);


  private AbstractMetaData metaData;

  public MetaColData(String tableName, String colName, AbstractMetaData metaData) {
    this.tableName = tableName;
    this.colName = colName;
    this.metaData = metaData;
  }

  public MetaColData(String tableName, String colName, Map<Object, EWAHCompressedBitmap> val2BitMap,
      int logSize) {
    this.tableName = tableName;
    this.colName = colName;
    this.val2BitMap = val2BitMap;
    this.logSizeCounter = new AtomicInteger(logSize);
  }

  public int getCount() {
    if (loaded.get() == false) {
      this.lazyLoad();
    }
    return this.valSizeCounter.get();
  }

  public EWAHCompressedBitmap getBitmap(Object val) {
    if (loaded.get() == false) {
      this.lazyLoad();
    }
    return this.val2BitMap.get(val);
  }

  public EWAHCompressedBitmap getBitmapOrDefault(Object val, EWAHCompressedBitmap defaultBitmap) {
    if (loaded.get() == false) {
      this.lazyLoad();
    }
    return this.val2BitMap.getOrDefault(val, defaultBitmap);
  }

  public List<EWAHCompressedBitmap> fuzzyGetBitmap(String targetVal, boolean isRegex) {
    if (targetVal == null) {
      return Collections.singletonList(EWAHCompressedBitmap.bitmapOf());
    }
    if (!loaded.get()) {
      this.lazyLoad();
    }
    List<EWAHCompressedBitmap> result = Collections.synchronizedList(new ArrayList<>());
    DimForkJoinPool.get().submit(() -> this.val2BitMap.keySet().parallelStream().forEach(k -> {
      String colVal = k.toString();
      if (isRegex) {
        if (Pattern.matches(targetVal, colVal)) {
          result.add(this.val2BitMap.get(k));
        }
      } else {
        if (StringUtils.containsIgnoreCase(colVal, targetVal)) {
          result.add(this.val2BitMap.get(k));
        }
      }
    })).join();
    return result;
  }

  /**
   * 懒加载
   */
  public void lazyLoad() {
    if (loaded.get() == false) {
      synchronized (metaData) {
        if (loaded.get() == false) {
          StopWatch stopWatch = StopWatch.createStarted();
          log.info(
              "MetaColData lazy load start, table={}, col={}, size={}, logSize={}, valSize={}.",
              tableName, colName, metaData.getAllLogs().size(), logSizeCounter.get(),
              valSizeCounter.get());
          appendLogs(metaData.getAllLogs());
          log.info(
              "MetaColData lazy load finish, table={}, col={}, size={},  logSize={}, valSize={}, cost={}.",
              tableName, colName, metaData.getAllLogs().size(), logSizeCounter.get(),
              valSizeCounter.get(), stopWatch.getTime());
          loaded.set(true);
        }
      }
    }
  }

  /**
   * 变更日志拼接
   *
   * @param changeLogs
   */
  public void appendLogs(List<MetaDataRow> changeLogs) {
    changeLogs.forEach(changeLog -> {
      Object changeColVal = changeLog.getValues().get(colName);
      if (changeColVal == null) {
        changeColVal = "null";
      }
      int bit = logSizeCounter.get();
      val2BitMap.computeIfAbsent(changeColVal, k -> {
        valSizeCounter.getAndIncrement();
        return new EWAHCompressedBitmap();
      }).set(bit);
      logSizeCounter.getAndIncrement();
    });
  }
}
