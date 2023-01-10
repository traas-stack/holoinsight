/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author wangsiyuan
 * @date 2022/3/21 7:45 下午
 */
public class G {
  private static final ThreadLocal<Gson> DEFAULT_GSON = new ThreadLocal<Gson>() {
    @Override
    protected Gson initialValue() {
      return new Gson();
    }
  };
  private static final ThreadLocal<Gson> PRETTY_GSON = new ThreadLocal<Gson>() {
    @Override
    protected Gson initialValue() {
      return new GsonBuilder().setPrettyPrinting().create();
    }
  };

  public static Gson get() {
    return DEFAULT_GSON.get();
  }

  public static Gson getPretty() {
    return PRETTY_GSON.get();
  }

  public static <T> List<T> parseList(String json, Class<T> elementType) {
    JsonElement element = new JsonParser().parse(json);
    JsonArray jsonArray = element.getAsJsonArray();
    List<T> list = new ArrayList<>();
    Iterator<JsonElement> it = jsonArray.iterator();
    while (it.hasNext()) {
      T object = get().fromJson(it.next(), elementType);
      list.add(object);
    }
    return list;
  }

  public static <T> Set<T> parseSet(String json, Class<T> elementType) {
    JsonElement element = new JsonParser().parse(json);
    JsonArray jsonArray = element.getAsJsonArray();
    Set<T> set = new HashSet<>();
    Iterator<JsonElement> it = jsonArray.iterator();
    while (it.hasNext()) {
      T object = get().fromJson(it.next(), elementType);
      set.add(object);
    }
    return set;
  }

  public static <K, V> Map<K, V> parseMap(String json, Class<K> keyType, Class<V> valueType) {
    JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
    Map<K, V> map = new LinkedHashMap<>();
    for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
      String keyStr = entry.getKey();
      Object key = keyStr;
      if (keyType == Long.class) {
        key = Long.valueOf(keyStr);
      } else if (keyType == Integer.class) {
        key = Integer.valueOf(keyStr);
      } else if (keyType == Double.class) {
        key = Double.valueOf(keyStr);
      } else if (keyType == Float.class) {
        key = Float.valueOf(keyStr);
      } else if (keyType == Short.class) {
        key = Short.valueOf(keyStr);
      }
      V value = get().fromJson(entry.getValue(), valueType);
      map.putIfAbsent((K) key, value);
    }
    return map;
  }

}
