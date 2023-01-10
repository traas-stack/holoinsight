/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.common.model.storage;

import java.util.Map;

/**
 * @author jiwliu
 * @version : StorageBuilder.java, v 0.1 2022年10月12日 15:34 wanpeng.xwp Exp $
 */
public interface StorageBuilder<T> {

  T storage2Entity(Map<String, Object> map);

  Map<String, Object> entity2Storage(T entity);
}
