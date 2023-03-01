/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package io.holoinsight.server.storage.engine.storage;

import io.holoinsight.server.storage.engine.model.RecordDO;

import java.io.IOException;
import java.util.List;

/**
 *
 * @author xiangwanpeng
 * @version : RecordStorage.java, v 0.1 2023年02月28日 16:20 xiangwanpeng Exp $
 */
public interface RecordStorage<T extends RecordDO> {
  void batchInsert(List<T> entities) throws IOException;
}
