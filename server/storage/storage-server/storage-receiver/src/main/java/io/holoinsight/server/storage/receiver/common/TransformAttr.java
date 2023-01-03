/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.receiver.common;

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
        }
        return result;
    }
}
