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

  public static Map<String, String> attList2Map(List<KeyValue> attributes) {
    Map<String, String> result = new HashMap<>(attributes.size());
    for (io.opentelemetry.proto.common.v1.KeyValue attribute : attributes) {
      result.put(attribute.getKey(), anyValueToString(attribute.getValue()));

      if (Const.REAL_TRACE_ID_TAGS.contains(attribute.getKey())) {
        result.put(Const.REAL_TRACE_ID, anyValueToString(attribute.getValue()));
      } else if (Const.REAL_SPAN_ID_TAGS.contains(attribute.getKey())) {
        result.put(Const.REAL_SPAN_ID, anyValueToString(attribute.getValue()));
      } else if (Const.REAL_PARENT_SPAN_ID_TAGS.contains(attribute.getKey())) {
        result.put(Const.REAL_PARENT_SPAN_ID, anyValueToString(attribute.getValue()));
      }
    }
    return result;
  }

  public static String anyValueToString(AnyValue anyValue) {
    switch (anyValue.getValueCase().getNumber()) {
      case AnyValue.STRING_VALUE_FIELD_NUMBER:
        return anyValue.getStringValue();
      case AnyValue.BOOL_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getBoolValue());
      case AnyValue.INT_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getIntValue());
      case AnyValue.DOUBLE_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getDoubleValue());
      case AnyValue.ARRAY_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getArrayValue());
      case AnyValue.KVLIST_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getKvlistValue());
      case AnyValue.BYTES_VALUE_FIELD_NUMBER:
        return String.valueOf(anyValue.getBytesValue());
      default:
        throw new UnsupportedOperationException("unsupported value type: " + anyValue);
    }
  }

  public static io.holoinsight.server.apm.common.model.specification.otel.KeyValue convertKeyValue(
      KeyValue keyValue) {
    AnyValue anyValue = keyValue.getValue();
    io.holoinsight.server.apm.common.model.specification.otel.KeyValue result;
    switch (anyValue.getValueCase().getNumber()) {
      case AnyValue.STRING_VALUE_FIELD_NUMBER:
        result = new io.holoinsight.server.apm.common.model.specification.otel.KeyValue(
            keyValue.getKey(), anyValue.getStringValue());
        break;
      case AnyValue.BOOL_VALUE_FIELD_NUMBER:
        result = new io.holoinsight.server.apm.common.model.specification.otel.KeyValue(
            keyValue.getKey(), anyValue.getBoolValue());
        break;
      case AnyValue.INT_VALUE_FIELD_NUMBER:
        result = new io.holoinsight.server.apm.common.model.specification.otel.KeyValue(
            keyValue.getKey(), anyValue.getIntValue());
        break;
      case AnyValue.DOUBLE_VALUE_FIELD_NUMBER:
        result = new io.holoinsight.server.apm.common.model.specification.otel.KeyValue(
            keyValue.getKey(), anyValue.getDoubleValue());
        break;
      case AnyValue.ARRAY_VALUE_FIELD_NUMBER:
        result = new io.holoinsight.server.apm.common.model.specification.otel.KeyValue(
            keyValue.getKey(), anyValue.getArrayValue());
        break;
      case AnyValue.KVLIST_VALUE_FIELD_NUMBER:
        result = new io.holoinsight.server.apm.common.model.specification.otel.KeyValue(
            keyValue.getKey(), anyValue.getKvlistValue());
        break;
      case AnyValue.BYTES_VALUE_FIELD_NUMBER:
        result = new io.holoinsight.server.apm.common.model.specification.otel.KeyValue(
            keyValue.getKey(), anyValue.getBytesValue());
        break;
      default:
        throw new UnsupportedOperationException("unsupported value type: " + anyValue);
    }
    return result;
  }

}
