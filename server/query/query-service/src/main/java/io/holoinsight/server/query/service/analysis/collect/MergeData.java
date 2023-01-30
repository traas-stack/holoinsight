/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.analysis.collect;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author xiangwanpeng
 * @version : EaMergeData.java, v 0.1 2020年01月09日 15:25 xiangwanpeng Exp $
 */
@Data
public class MergeData implements Serializable {

  private static final long serialVersionUID = -5868633420239276321L;
  /**
   * 一个key下的所有EA
   */
  private List<AnalyzedLog> analyzedLogs = new ArrayList<AnalyzedLog>();

  public static final Comparator<AnalyzedLog> COMPARATOR;

  static {
    COMPARATOR = (o1, o2) -> o2.getCount() - o1.getCount();
  }

  public MergeData clone() {
    MergeData mergeData = new MergeData();
    for (AnalyzedLog ea : analyzedLogs) {
      mergeData.analyzedLogs.add(ea.clone());
    }
    return mergeData;
  }

  public int getSize() {
    int size = 0;
    for (ErrorLog log : analyzedLogs) {
      size += log.logSize();
    }
    return size;
  }

  public void merge(List<AnalyzedLog> newData) {
    for (AnalyzedLog log : newData) {
      analysisNewLine(log);
    }
    Collections.sort(analyzedLogs, COMPARATOR);
  }

  private void analysisNewLine(AnalyzedLog newLogLine) {
    AnalyzedLog similar = getSimilarLine(newLogLine);
    if (null != similar) {
      similar.mergeLog(newLogLine);
    } else if (analyzedLogs.size() <= 64) {
      similar = (AnalyzedLog) newLogLine.initEmpty();
      similar.mergeLog(newLogLine);
      analyzedLogs.add(similar);
    } else {

    }
  }

  /**
   * 从List中查找与新的SplitedLine模式相同的SplitedLine，没有则返回null
   *
   * @return 这里返回对象而不是boolean是为了方便进一步操作匹配的对象
   */
  private AnalyzedLog getSimilarLine(AnalyzedLog log) {
    for (AnalyzedLog exist : analyzedLogs) {
      if (exist.isSimilarToMe(log)) {
        return exist;
      }
    }
    return null;
  }
}
