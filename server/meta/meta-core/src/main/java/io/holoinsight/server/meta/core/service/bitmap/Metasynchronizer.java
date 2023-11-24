/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.service.bitmap;

import java.util.List;

/**
 *
 * @author xiangwanpeng
 * @version : Metasynchronizer.java, v 0.1 2023年11月23日 19:25 xiangwanpeng Exp $
 */
public interface Metasynchronizer {
  List<MetaDataRow> sync(String tableName, long start, long end);
}
