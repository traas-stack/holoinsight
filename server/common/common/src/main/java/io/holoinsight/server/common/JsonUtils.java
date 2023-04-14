/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

/**
 * <p>
 * created at 2020-09-30
 *
 * @author xzchaoo
 */
public class JsonUtils {
  private static final ObjectMapper OM = new ObjectMapper();

  static {
    OM.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    OM.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    OM.setSerializationInclusion(JsonInclude.Include.NON_NULL);
  }

  /**
   * <p>
   * toJson.
   * </p>
   */
  public static String toJson(Object obj) {
    try {
      return OM.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * <p>
   * toJson.
   * </p>
   */
  public static String toJson(Object obj, Class<?> view) {
    try {
      return OM.writerWithView(view).writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * <p>
   * fromJson.
   * </p>
   */
  public static <T> T fromJson(String str, Class<T> clazz) {
    try {
      return OM.readValue(str, clazz);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * <p>
   * fromJson.
   * </p>
   */
  public static <T> T fromJson(InputStream is, Class<T> clazz) {
    try {
      return OM.readValue(is, clazz);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * <p>
   * fromJson.
   * </p>
   */
  public static <T> T fromJson(String str, TypeReference<T> typeReference) {
    try {
      return OM.readValue(str, typeReference);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * <p>
   * fromJson.
   * </p>
   */
  public static <T> T fromJson(String str, Class<T> clazz, Class<?> view) {
    try {
      return OM.readerWithView(view).forType(clazz).readValue(str);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  public static void writeValue(Writer w, Object value) {
    try {
      OM.writeValue(w, value);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
}
