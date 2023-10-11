/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import org.apache.kafka.common.TopicPartition;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/19
 *
 * @author xzchaoo
 */
@Slf4j
public class FilePartitionStateStore implements PartitionStateStore {
  private final String baseDir;

  public FilePartitionStateStore(String baseDir) {
    this.baseDir = Objects.requireNonNull(baseDir);
  }

  @Override
  public byte[] loadState(TopicPartition partition) throws Exception {
    File file = new File(baseDir, String.format("%s-%d", partition.topic(), partition.partition()));
    if (!file.exists()) {
      return null;
    }

    return Files.readAllBytes(file.toPath());
  }

  @Override
  public void saveState(TopicPartition partition, byte[] state) throws Exception {
    File file = new File(baseDir, String.format("%s-%d", partition.topic(), partition.partition()));
    file.getParentFile().mkdirs();

    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(state);
    }
  }

  @Override
  public void saveOffset(TopicPartition partition, OffsetInfo oi) throws Exception {
    List<OffsetInfo> offsets = new ArrayList<>(loadOffsets(partition));
    offsets.add(oi);
    Collections.sort(offsets, Comparator.comparingLong(OffsetInfo::getOffset).reversed());
    if (offsets.size() > 10) {
      offsets = offsets.subList(0, 10);
    }

    File dir = new File(this.baseDir, "offsets");
    dir.mkdirs();
    File file = new File(dir, partition.toString());

    byte[] data = JSON.toJSONBytes(offsets);
    Files.write(file.toPath(), data);
  }

  @Override
  public List<OffsetInfo> loadOffsets(TopicPartition partition) throws Exception {
    File dir = new File(this.baseDir, "offsets");
    dir.mkdirs();
    File file = new File(dir, partition.toString());
    if (!file.exists()) {
      return Collections.emptyList();
    }

    byte[] data = Files.readAllBytes(file.toPath());
    return JSON.parseArray(new String(data, "UTF-8"), OffsetInfo.class);
  }

  @Override
  public void close() throws IOException {}
}
