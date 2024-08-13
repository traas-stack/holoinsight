/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.core.conf;

import java.lang.reflect.Type;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;

/**
 * <p>
 * created at 2023/10/7
 *
 * @author xzchaoo
 */
public class ElectObjectDeserializer implements ObjectDeserializer {
  @Override
  public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
    Object obj = parser.parse(fieldName);
    if (obj instanceof String) {
      String string = (String) obj;
      return (T) SelectItem.Elect.of(string);
    }
    return (T) ((JSONObject) obj).toJavaObject(type);
  }

  @Override
  public int getFastMatchToken() {
    return 0;
  }
}
