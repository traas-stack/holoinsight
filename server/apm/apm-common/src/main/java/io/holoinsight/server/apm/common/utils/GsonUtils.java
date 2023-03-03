/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author jiwliu
 * @version : GsonUtils.java, v 0.1 2022年09月30日 14:47 xiangwanpeng Exp $
 */
public class GsonUtils {
  public static ThreadLocal<Gson> gs = new ThreadLocal<Gson>();
  public static ThreadLocal<Gson> esGs = new ThreadLocal<Gson>();

  public static Gson get() {
    Gson gson = gs.get();
    if (gson == null) {
      gson = new GsonBuilder().disableHtmlEscaping().create();
      gs.set(gson);
    }
    return gson;
  }

  public static String toJson(Object obj) {
    return get().toJson(obj);
  }

  public static <T> T fromJson(String json, Type type) {
    return get().fromJson(json, type);
  }

  public static JsonObject fromJson(String json) {
    return JsonParser.parseString(json).getAsJsonObject();
  }

  public static String jsonFormatter(String uglyJsonStr) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(uglyJsonStr);
    return gson.toJson(je);
  }

  public static Map<String, Object> toMap(String json) {
    Type t = new TypeToken<Map<String, Object>>() {}.getType();
    Map<String, Object> whereMap = get().fromJson(json, t);
    if (whereMap == null) {
      return null;
    }
    return whereMap;
  }

  public static Map<String, String> toStrMap(String json) {
    Type t = new TypeToken<Map<String, String>>() {}.getType();
    return get().fromJson(json, t);
  }

  public static List<String> toList(String json) {
    Type t = new TypeToken<List<String>>() {}.getType();
    return get().fromJson(json, t);
  }

  public static <T> List<T> toList(String json, Class<T> cls) {
    Type t = new ListParameterizedTypeImpl(cls);
    return get().fromJson(json, t);
  }

  public static List<Map<String, Object>> toMapList(String json) {
    Type t = new TypeToken<List<Map<String, Object>>>() {}.getType();
    return get().fromJson(json, t);
  }

  public static <T> T json2Bean(String jsonStr, Class<T> objClass) {
    return get().fromJson(jsonStr, objClass);
  }

  @AllArgsConstructor
  @Data
  private static class ListParameterizedTypeImpl implements ParameterizedType {
    private Class clazz;

    @Override
    public Type[] getActualTypeArguments() {
      return new Type[] {clazz};
    }

    @Override
    public Type getRawType() {
      return List.class;
    }

    @Override
    public Type getOwnerType() {
      return null;
    }
  }
}

