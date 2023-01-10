/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/3/29 8:12 下午
 */
public class ObjectToMapUtil {


  public static Map<String, String> generateObjectToStringMap(Object o) throws Exception {
    Field[] fields = o.getClass().getDeclaredFields();
    Map<String, String> map = new HashMap<>();
    for (int i = 0; i < fields.length; i++) {
      if (fields[i].getGenericType().toString().equalsIgnoreCase("class java.lang.String")) {
        Method method = o.getClass().getMethod("get" + getMethodName(fields[i].getName()));
        String value = (String) method.invoke(o);
        if (value == null) {
          value = "";
        }
        map.put(fields[i].getName(), value);
      }
    }
    return map;
  }

  private static String getMethodName(String fildeName) throws Exception {
    byte[] items = fildeName.getBytes();
    items[0] = (byte) ((char) items[0] - 'a' + 'A');
    return new String(items);
  }
}
