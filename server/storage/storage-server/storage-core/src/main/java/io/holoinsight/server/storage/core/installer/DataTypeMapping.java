/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.core.installer;

import java.lang.reflect.Type;

/**
 * @author jiwliu
 * @version : DataTypeMapping.java, v 0.1 2022年10月11日 22:27 wanpeng.xwp Exp $
 */
public interface DataTypeMapping {

    String transform(Class<?> type, Type genericType);
}