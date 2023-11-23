/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.io.IOException;
import java.util.List;

import org.apache.kafka.common.TopicPartition;

/**
 * <p>
 * created at 2023/9/19
 *
 * @author xzchaoo
 */
public interface PartitionStateStore {
  byte[] loadState(TopicPartition partition) throws Exception;

  void saveState(TopicPartition partition, byte[] state) throws Exception;

  /**
   * Save latest consumer offset.
   *
   * @param partition
   * @param oi
   * @throws Exception
   */
  void saveOffset(TopicPartition partition, OffsetInfo oi) throws Exception;

  /**
   * Query latest consumer offset. Just return the latest 10.
   *
   * @param partition
   * @return
   * @throws Exception
   */
  List<OffsetInfo> loadOffsets(TopicPartition partition) throws Exception;

  void close() throws IOException;
}
