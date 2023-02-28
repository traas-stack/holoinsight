/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.storage.engine;

import io.holoinsight.server.storage.engine.elasticsearch.model.RecordEsDO;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author wanpeng.xwp
 * @version : RecordStorage.java, v 0.1 2023年02月28日 16:20 wanpeng.xwp Exp $
 */
public interface RecordStorage<T extends RecordEsDO> {
  void batchInsert(List<T> entities) throws IOException;
}
