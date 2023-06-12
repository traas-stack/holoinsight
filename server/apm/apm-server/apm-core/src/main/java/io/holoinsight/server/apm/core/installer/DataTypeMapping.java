/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.core.installer;

import java.lang.reflect.Type;

/**
 * @author jiwliu
 * @version : DataTypeMapping.java, v 0.1 2022年10月11日 22:27 xiangwanpeng Exp $
 */
public interface DataTypeMapping {

  String transform(String field, Class<?> type, Type genericType);
}
