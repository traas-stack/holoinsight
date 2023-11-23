/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.executor.state;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;

import io.holoinsight.server.agg.v1.core.AggProperties;
import io.holoinsight.server.agg.v1.core.mapper.AggOffsetV1DO;
import io.holoinsight.server.agg.v1.core.mapper.AggOffsetV1DOMapper;
import io.holoinsight.server.agg.v1.core.mapper.AggStateV1DO;
import io.holoinsight.server.agg.v1.core.mapper.AggStateV1DOMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * created at 2023/9/24
 *
 * @author xzchaoo
 */
@Slf4j
public class JdbcPartitionStateStore implements PartitionStateStore {

  private static final int OFFSET_LIMIT = 10;
  private static final int OFFSET_LIMIT2 = OFFSET_LIMIT + 10;

  @Autowired
  private AggOffsetV1DOMapper aggOffsetV1DOMapper;
  @Autowired
  private AggStateV1DOMapper aggStateV1DOMapper;
  @Autowired
  private AggProperties aggProperties;

  public JdbcPartitionStateStore() {}

  @Override
  public byte[] loadState(TopicPartition partition) throws Exception {
    AggStateV1DO d = selectState(partition);
    if (d == null) {
      return null;
    }
    log.info("[state] [{}] engine=[jdbc] load state successfully, bytes=[{}]", partition,
        d.getState().length);

    return d.getState();
  }

  @Override
  public void saveState(TopicPartition partition, byte[] state) throws Exception {
    Date now = new Date();

    AggStateV1DO d = selectState(partition);
    if (d == null) {
      d = new AggStateV1DO();
      d.setGmtCreate(now);
      d.setGmtModified(now);
      d.setPartitionName(partition.toString());
      d.setConsumerGroup(aggProperties.getConsumerGroupId());
      d.setState(state);
      aggStateV1DOMapper.insert(d);
    } else {
      d.setGmtModified(now);
      d.setState(state);
      aggStateV1DOMapper.updateById(d);
    }
    log.info("[state] [{}] engine=[jdbc] save state successfully, bytes=[{}]", partition,
        state.length);
  }

  @Override
  public void saveOffset(TopicPartition partition, OffsetInfo oi) throws Exception {
    Date now = new Date();
    byte[] data = JSON.toJSONBytes(oi);

    AggOffsetV1DO d = new AggOffsetV1DO();
    d.setGmtCreate(now);
    d.setGmtModified(now);
    d.setPartitionName(partition.toString());
    d.setConsumerGroup(aggProperties.getConsumerGroupId());
    d.setVersion(now.getTime());
    d.setData(data);
    aggOffsetV1DOMapper.insert(d);

    QueryWrapper<AggOffsetV1DO> wrapper = partitionWhere(partition);
    Long count = aggOffsetV1DOMapper.selectCount(wrapper);
    if (count >= OFFSET_LIMIT2) {
      UpdateWrapper<AggOffsetV1DO> wrapper2 = new UpdateWrapper<>();
      wrapper2.eq("partition_name", partition.toString());
      wrapper2.eq("consumer_group", aggProperties.getConsumerGroupId());
      wrapper2.orderByAsc("id");
      wrapper2.last("limit " + (count.intValue() - OFFSET_LIMIT));
      aggOffsetV1DOMapper.delete(wrapper2);
    }
  }

  @Override
  public List<OffsetInfo> loadOffsets(TopicPartition partition) throws Exception {
    List<AggOffsetV1DO> offsets = selectAllOffsets(partition, OFFSET_LIMIT);

    List<OffsetInfo> ret = new ArrayList<>(offsets.size());
    for (AggOffsetV1DO d : offsets) {
      ret.add(JSON.parseObject(d.getData(), OffsetInfo.class));
    }
    return ret;
  }

  @Override
  public void close() throws IOException {}

  private List<AggOffsetV1DO> selectAllOffsets(TopicPartition partition, int limit) {
    QueryWrapper<AggOffsetV1DO> wrapper = partitionWhere(partition);
    if (limit > 0) {
      wrapper.orderByDesc("id").last("limit " + limit);
    }
    return aggOffsetV1DOMapper.selectList(wrapper);
  }

  private AggStateV1DO selectState(TopicPartition partition) throws SQLException {
    QueryWrapper<AggStateV1DO> wrapper = partitionWhere(partition);
    return aggStateV1DOMapper.selectOne(wrapper);
  }

  private <T> QueryWrapper<T> partitionWhere(TopicPartition partition) {
    QueryWrapper<T> w = new QueryWrapper<>();
    w.eq("partition_name", partition.toString());
    w.eq("consumer_group", aggProperties.getConsumerGroupId());
    return w;
  }

}
