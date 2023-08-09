/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.common;

import io.holoinsight.server.apm.common.constants.Const;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.common.v1.KeyValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransformAttr {

  public static Map<String, AnyValue> attList2Map(List<KeyValue> attributes) {
    Map<String, AnyValue> result = new HashMap<>(attributes.size());
    for (io.opentelemetry.proto.common.v1.KeyValue attribute : attributes) {
      result.put(attribute.getKey(), attribute.getValue());

      if (Const.REAL_TRACE_ID_TAGS.contains(attribute.getKey())) {
        result.put(Const.REAL_TRACE_ID, attribute.getValue());
      } else if (Const.REAL_SPAN_ID_TAGS.contains(attribute.getKey())) {
        result.put(Const.REAL_SPAN_ID, attribute.getValue());
      } else if (Const.REAL_PARENT_SPAN_ID_TAGS.contains(attribute.getKey())) {
        result.put(Const.REAL_PARENT_SPAN_ID, attribute.getValue());
      }
    }
    return result;
  }
}
