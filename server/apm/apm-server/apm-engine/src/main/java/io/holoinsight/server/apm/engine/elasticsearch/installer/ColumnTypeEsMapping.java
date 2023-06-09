/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.installer;

import com.google.gson.JsonObject;
import io.holoinsight.server.apm.common.model.specification.sw.Layer;
import io.holoinsight.server.apm.common.model.specification.sw.RequestType;
import io.holoinsight.server.apm.core.installer.DataTypeMapping;
import io.holoinsight.server.apm.engine.model.RecordDO;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

@Component
public class ColumnTypeEsMapping implements DataTypeMapping {

  @Override
  public String transform(String field, Class<?> type, Type genericType) {
    if (RecordDO.TIMESTAMP.equals(field)) {
      return "date";
    } else if (Integer.class.equals(type) || int.class.equals(type) || Layer.class.equals(type)) {
      return "integer";
    } else if (Long.class.equals(type) || long.class.equals(type)) {
      return "long";
    } else if (Double.class.equals(type) || double.class.equals(type)) {
      return "double";
    } else if (String.class.equals(type) || RequestType.class.equals(type)) {
      return "keyword";
    } else if (byte[].class.equals(type)) {
      return "binary";
    } else if (JsonObject.class.equals(type)) {
      return "text";
    } else if (List.class.isAssignableFrom(type)) {
      final Type elementType = ((ParameterizedType) genericType).getActualTypeArguments()[0];
      return transform(field, (Class<?>) elementType, elementType);
    } else {
      throw new IllegalArgumentException("Unsupported data type: " + type.getName());
    }
  }
}
