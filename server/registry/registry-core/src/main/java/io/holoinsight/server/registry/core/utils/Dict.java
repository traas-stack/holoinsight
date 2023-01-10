/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.utils;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * created at 2022/4/11
 *
 * @author zzhb101
 */
public final class Dict {
  private static final Cache<String, String> DICT;

  static {
    DICT = Caffeine.newBuilder() //
        // .maximumSize() 无法估计
        .expireAfterAccess(Duration.ofHours(1)) //
        // .scheduler(Schedulers.CAFFEINE_SCHEDULER) //
        .build(); //
  }

  private Dict() {}


  public static String get(String str) {
    if (str == null) {
      return null;
    }
    return DICT.get(str, Function.identity());
  }

  public static String[] replace(String[] array) {
    for (int i = 0; i < array.length; i++) {
      array[i] = get(array[i]);
    }
    return array;
  }

  public static String[] copy(String[] array) {
    String[] newArray = new String[array.length];
    for (int i = 0; i < array.length; i++) {
      newArray[i] = get(array[i]);
    }
    return newArray;
  }

  public static List<String> copy(List<String> list) {
    List<String> newList = new ArrayList<>(list.size());
    for (String s : list) {
      newList.add(get(s));
    }
    return newList;
  }

  public static Set<String> copySet(Set<String> values) {
    if (values == null) {
      return null;
    }
    return values.stream().map(Dict::get).collect(Collectors.toSet());
  }

}
