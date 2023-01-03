/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.installer;

import io.holoinsight.server.storage.common.model.specification.sw.Layer;
import io.holoinsight.server.storage.common.model.specification.sw.RequestType;
import io.holoinsight.server.storage.core.installer.DataTypeMapping;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Component;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

/**
 * @author jiwliu
 * @version : ColumnTypeEsMapping.java, v 0.1 2022年10月11日 22:27 wanpeng.xwp Exp $
 */
@Component
public class ColumnTypeEsMapping implements DataTypeMapping {

    @Override
    public String transform(Class<?> type, Type genericType) {
        if (Integer.class.equals(type) || int.class.equals(type) || Layer.class.equals(type)) {
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
            return transform((Class<?>) elementType, elementType);
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + type.getName());
        }
    }
}