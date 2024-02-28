/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.executor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * After an analyzed log, similar logs will be aggregated together.
 * <p>
 * created at 2024/2/26
 *
 * @author xzchaoo
 */
@Data
class AnalyzedLog {
  public static double SIMILAR_STRING_FACTOR = 0.9;
  public static double SIMILAR_PARTS_FACTOR = 0.8;
  public static int SOURCE_MAX_COUNT = 60;
  public static int IGNORE_TYPE_LENGTH = 5;

  private List<LAPart> parts;

  /**
   * One line of log sampling
   */
  private String sample;

  /**
   * Number of similar log lines
   */
  private int count;

  private List<SourceWord> sourceWords;
  private Map<String, Integer> ipCountMap = new HashMap<>();

  @JsonIgnore
  private transient Map<String, Integer> sources;

  public void addIpCount(String hostname, int count) {
    if (StringUtils.isEmpty(hostname)) {
      return;
    }
    ipCountMap.put(hostname, ipCountMap.getOrDefault(hostname, 0) + count);
  }

  public boolean isSimilarTo(StringBuilder reuse, AnalyzedLog al) {
    return isSimilar(reuse, parts, al.parts);
  }

  public void merge(AnalyzedLog al) {
    // merge count
    this.count += al.count;

    // merge ipCountMap
    al.ipCountMap.forEach(this::addIpCount);

    // merge source words
    if (sources == null) {
      sources = new HashMap<>();
      for (SourceWord sw : sourceWords) {
        sources.put(sw.getSource(), sw.getCount());
      }
    }
    for (SourceWord sw : al.getSourceWords()) {
      sources.put(sw.getSource(), sources.getOrDefault(sw.getSource(), 0) + sw.getCount());
    }
  }

  public void update() {
    if (sources != null) {
      List<SourceWord> sourceWords = new ArrayList<>();
      sources.forEach((source, count) -> {
        SourceWord sw = new SourceWord();
        sw.setSource(source);
        sw.setCount(count);
        sourceWords.add(sw);
      });
      this.sourceWords = sourceWords;
    }
  }

  private static boolean isSimilar(StringBuilder reuse, List<LAPart> fs, List<LAPart> ts) {
    double small, big;

    if (fs.size() > ts.size()) {
      big = fs.size();
      small = ts.size();
    } else {
      big = ts.size();
      small = fs.size();
    }

    if (small / big < SIMILAR_PARTS_FACTOR) {
      return false;
    }

    double similar = 0;
    double total = 0;
    for (LAPart f : fs) {
      if (f.isSource()) {
        continue;
      }
      if (f.getContent().length() < IGNORE_TYPE_LENGTH) {
        continue;
      }
      boolean found = false;
      for (LAPart t : ts) {
        if (f.isSimilarTo(reuse, t)) {
          found = true;
          break;
        }
      }
      int partSize = f.getContent().length();
      int s_add = 0, t_add = partSize;
      if (found) {
        s_add = partSize;
      }
      if (f.isImportant()) {
        s_add *= 2;
      } else {
        t_add /= 2;
      }
      similar += s_add;
      total += t_add;
    }
    if (total == 0) {
      return true;
    }

    return (similar / total) >= SIMILAR_PARTS_FACTOR;
  }

}
