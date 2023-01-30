/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.analysis.collect;

import lombok.Data;

import java.util.*;

/**
 * @author xiangwanpeng
 * @version : SuperErrorLog.java, v 0.1 2020年01月09日 15:25 xiangwanpeng Exp $
 */
@Data
public class AnalyzedLog extends ErrorLog {

  private static final long serialVersionUID = -2173686352244455806L;
  List<Part> parts;
  TreeSet<SourceWord> sourceWords;
  String sample;
  Map<String, Integer> ipCountMap = new HashMap<>();

  public AnalyzedLog(List<Part> parts, String sample) {
    this.parts = parts;
    this.sample = sample;
  }

  private AnalyzedLog() {
    this.parts = new ArrayList<Part>();
  }

  public AnalyzedLog clone() {
    AnalyzedLog errorLog = (AnalyzedLog) initEmpty();
    errorLog.mergeLog(this);
    return errorLog;
  }

  @Override
  public ErrorLog initEmpty() {
    AnalyzedLog init = new AnalyzedLog();
    init.count = 0; // 空槽位
    init.sample = this.sample;
    for (Part p : this.parts) {
      Part cloned = new Part(p.content, p.source, p.important, 0);
      // p.similarWhileMerge = cloned;
      init.parts.add(cloned);
    }
    return init;
  }

  @Override
  public boolean isSimilarToMe(ErrorLog otherLog) {
    AnalyzedLog sel = (AnalyzedLog) otherLog;
    return SuperEAStrategy.isSimilar(sel.parts, this.parts);
  }

  @Override
  protected void doMerge(ErrorLog otherLog) {
    AnalyzedLog sel = (AnalyzedLog) otherLog;

    if (this.sourceWords == null) {
      this.sourceWords = new TreeSet<SourceWord>(); // 我是被merge的，我是槽位，我要创建
    }

    if (sel.sourceWords != null) { // 对方创建了槽位，是merge后的结果再merge
      SuperEAStrategy.mergeWhileBatch(
          // sel.parts,
          sel.sourceWords, this.sourceWords);
    } else { // 对方未创建槽位，需要merge的是原始parts
      SuperEAStrategy.mergeWhileSingleComputing(sel.parts, this.sourceWords);
    }

    for (Map.Entry<String, Integer> entry : sel.ipCountMap.entrySet()) {
      final Integer oldCount = this.ipCountMap.get(entry.getKey());

      if (oldCount != null) {
        this.ipCountMap.put(entry.getKey(), oldCount + entry.getValue());
      } else if (this.ipCountMap.size() < 50) {
        this.ipCountMap.put(entry.getKey(), entry.getValue());
      }
    }

  }

  @Override
  public int logSize() {
    int size = super.logSize();
    for (Part part : parts) {
      size += part.partSize();
    }
    if (sourceWords != null) {
      for (SourceWord word : sourceWords) {
        size += word.calculateSize();
      }
    }
    return size;
  }

  @Override
  public ErrorStoreModel buildStoreModel() {
    ErrorStoreModel ret = new ErrorStoreModel();
    ret.setCount(this.count);
    ret.setErrorSourceWords(top(this.sourceWords, 5));
    ret.setSourceView(this.sample);
    return ret;
  }

  private List<SourceWord> top(TreeSet<SourceWord> from, int count) {
    List<SourceWord> to = new ArrayList<SourceWord>();
    if (from == null) {
      return to;
    }
    int c = 0;
    for (SourceWord w : from) {
      if (c >= count) {
        break;
      }
      to.add(w);
      c++;
    }
    return to;
  }
}
