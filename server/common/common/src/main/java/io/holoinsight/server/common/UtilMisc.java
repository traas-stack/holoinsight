/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <p>
 * UtilMisc class.
 * </p>
 *
 * @author xzchaoo
 * @version 1.0: UtilMisc.java, v 0.1 2022年04月24日 12:36 下午 jinsong.yjs Exp $
 */
public class UtilMisc {
  private static Random seed = new Random();

  // 切分List
  /**
   * <p>
   * divideList.
   * </p>
   */
  public static <T> List<List<T>> divideList(List<T> listInput, Integer num) {
    List<List<T>> res = new ArrayList<List<T>>();
    if (CollectionUtils.isEmpty(listInput)) {
      return res;
    }
    if (num <= 0) {
      res.add(listInput);
      return res;
    }
    List<T> temp = new ArrayList<T>();
    int count = 0;
    for (T t : listInput) {
      temp.add(t);
      count++;
      if (count >= num) {
        res.add(temp);
        temp = new ArrayList<T>();
        count = 0;
      }
    }
    if (!CollectionUtils.isEmpty(temp)) {
      res.add(temp);
    }
    return res;
  }

  /**
   * 获取0到max-1的随机数
   *
   * @param max
   */
  public static int getRandom(int max) {
    return Math.abs(seed.nextInt() % max);
  }
}
