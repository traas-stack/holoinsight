/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * J class.
 * </p>
 *
 * @author xzchaoo
 * @version 1.0: J.java, v 0.1 2022年02月23日 12:00 下午 jinsong.yjs Exp $
 */
public class J {

  /** Constant <code>gs</code> */
  public static ThreadLocal<Gson> gs = new ThreadLocal<Gson>();

  /**
   * <p>
   * get.
   * </p>
   */
  public static Gson get() {
    Gson gson = gs.get();
    if (gson == null) {
      gson = new GsonBuilder().disableHtmlEscaping().create();
      gs.set(gson);
    }
    return gson;
  }

  /**
   * <p>
   * toJson.
   * </p>
   */
  public static String toJson(Object obj) {
    return get().toJson(obj);
  }

  /**
   * <p>
   * fromJson.
   * </p>
   */
  public static <T> T fromJson(String json, Type type) {
    return get().fromJson(json, type);
  }

  /**
   * <p>
   * jsonFormatter.
   * </p>
   */
  public static String jsonFormatter(String uglyJsonStr) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    JsonParser jp = new JsonParser();
    JsonElement je = jp.parse(uglyJsonStr);
    return gson.toJson(je);
  }

  /**
   * <p>
   * toMap.
   * </p>
   */
  public static Map<String, Object> toMap(String json) {
    Type t = new TypeToken<Map<String, Object>>() {}.getType();
    Map<String, Object> whereMap = get().fromJson(json, t);
    if (whereMap == null) {
      return null;
    }
    return whereMap;
  }

  /**
   * <p>
   * toStrMap.
   * </p>
   */
  public static Map<String, String> toStrMap(String json) {
    Type t = new TypeToken<Map<String, String>>() {}.getType();
    return get().fromJson(json, t);
  }


  /**
   * <p>
   * toList.
   * </p>
   */
  public static List<String> toList(String json) {
    Type t = new TypeToken<List<String>>() {}.getType();
    return get().fromJson(json, t);
  }

  /**
   * <p>
   * toMapList.
   * </p>
   */
  public static List<Map<String, Object>> toMapList(String json) {
    Type t = new TypeToken<List<Map<String, Object>>>() {}.getType();
    return get().fromJson(json, t);
  }

  /**
   * <p>
   * json2Bean.
   * </p>
   */
  public static <T> T json2Bean(String jsonStr, Class<T> objClass) {
    return get().fromJson(jsonStr, objClass);
  }

  // public static void toProtoBean( Message.Builder destPojoClass, Object source) {
  // String json = J.toJson(source);
  // try {
  // JsonFormat.parser().merge(json, destPojoClass);
  // } catch (InvalidProtocolBufferException e) {
  // throw new RuntimeException("InvalidProtocolBufferException error", e);
  // }
  // }
  //
  // public static <PojoType> PojoType toPojoBean( Class<PojoType> destPojoClass,
  // Message source) throws InvalidProtocolBufferException {
  // String json = JsonFormat.printer().print(source);
  // return J.fromJson(json, destPojoClass);
  // }
}
