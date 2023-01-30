/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.service.analysis.collect;

import lombok.Data;

import java.util.List;
import java.util.TreeSet;

/**
 * @author xiangwanpeng
 * @version : SuperEAStrategy.java, v 0.1 2020年01月09日 15:25 xiangwanpeng Exp $
 */
@Data
public class SuperEAStrategy {

  public static double SIMILAR_STRING_FACTOR = 0.9;
  public static double SIMILAR_PARTS_FACTOR = 0.8;
  public static int SOURCE_MAX_COUNT = 60;
  public static int IGNORE_TYPE_LENGTH = 5;

  /**
   * 是否相似 如果相似的part占part总数（忽略source）的SIMILAR_FACTOR或以上，则相似
   *
   * @return
   */
  public static boolean isSimilar(List<Part> fs, List<Part> ts) {

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
    for (Part f : fs) {
      if (f.isSource()) {
        continue;
      }
      if (f.content.length() < IGNORE_TYPE_LENGTH) {
        continue;
      }
      boolean found = false;
      for (Part t : ts) {
        if (isSimilar(f.content, t.content)) {
          found = true;
          break;
        }
      }
      int partSize = f.content.length();
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
      return true; // 奇葩的日志，没错误类型
    }

    boolean ret = (similar / total) >= SIMILAR_PARTS_FACTOR;
    return ret;
  }

  // 在stream上单行日志计算时的合并逻辑,newParts来自一行日志
  public static void mergeWhileSingleComputing(List<Part> newParts, TreeSet<SourceWord> sources) {
    for (Part p : newParts) {
      if (p.isSource()) {
        SourceWord exist = null;
        for (SourceWord s : sources) {
          if (s.getSource().equals(p.content)) {
            exist = s;
            break;
          }
        }
        if (exist == null) {
          if (sources.size() >= SOURCE_MAX_COUNT) {
            // error log
            continue;
          } else {
            sources.add(new SourceWord(p.content));
          }
        } else {
          exist.increase();
        }
      } else {

      }
    }
  }

  // newParts和from都是来自已经经过聚合之后的ea
  public static void mergeWhileBatch(
      // List<Part> newParts,
      TreeSet<SourceWord> from, TreeSet<SourceWord> to) {

    for (SourceWord f : from) {
      if (f == null) {
        continue;
      }
      SourceWord exist = null;
      for (SourceWord t : to) {
        if (t == null) {
          continue;
        }
        if (t.getSource().equals(f.getSource())) {
          exist = t;
          break;
        }
      }
      if (exist == null) {
        if (to.size() >= SOURCE_MAX_COUNT) {
          // error log
          continue;
        } else {
          SourceWord clone = new SourceWord(f.getSource());
          clone.setCount(f.getCount());
          to.add(clone);
        }
      } else {
        exist.setCount(exist.getCount() + f.getCount());
      }
    }
  }

  private static boolean isSimilar(String f, String t) {
    StringBuffer fb = new StringBuffer();
    for (int k = 0; k < f.length(); k++) {
      char fc = f.charAt(k);
      if (Character.isLetter(fc)) {
        fb.append(fc);
      }
    }

    StringBuffer tb = new StringBuffer();
    for (int k = 0; k < t.length(); k++) {
      char tc = t.charAt(k);
      if (Character.isLetter(tc)) {
        tb.append(tc);
      }
    }
    return fb.toString().equals(tb.toString());
  }
}
