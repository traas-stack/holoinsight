/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.Maps;
import com.google.protobuf.ByteString;
import com.xzchaoo.commons.stat.StringsKey;

import io.holoinsight.server.agg.v1.core.AggProperties;
import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.agg.v1.core.conf.PartitionKey;
import io.holoinsight.server.agg.v1.core.data.AggKeySerdes;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.core.data.AggValuesSerdes;
import io.holoinsight.server.agg.v1.core.kafka.KafkaProducerHealthChecker;
import io.holoinsight.server.agg.v1.pb.AggProtos;
import io.holoinsight.server.apm.engine.model.SpanDO;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.common.threadpool.CommonThreadPools;
import io.holoinsight.server.extension.model.Record;
import io.holoinsight.server.extension.model.Row;
import io.holoinsight.server.extension.model.Table;
import io.holoinsight.server.gateway.core.utils.StatUtils;
import io.holoinsight.server.gateway.grpc.DataNode;
import io.holoinsight.server.gateway.grpc.Point;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV1;
import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4;
import io.holoinsight.server.registry.grpc.agent.ReportEventRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * This class is responsible for emitting elements to be aggregated.
 * <p>
 * created at 2023/9/18
 *
 * @author xzchaoo
 */
@Slf4j
public class AggDispatcher {
  private KafkaProducer<AggTaskKey, AggProtos.AggTaskValue> kafkaProducer;
  private KafkaProducerHealthChecker kafkaProducerHealthChecker;

  @Autowired
  private AggProperties aggProperties;
  @Autowired
  private AggTaskV1StorageForDispatcher storage;
  @Autowired
  private AggConfig aggConfig;

  @Autowired
  private CommonThreadPools commonThreadPools;

  @PostConstruct
  public void init() {
    Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        aggProperties.getKafkaBootstrapServers());
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, AggKeySerdes.S.class.getName());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AggValuesSerdes.S.class.getName());
    properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 64 * 1024);
    properties.put(ProducerConfig.LINGER_MS_CONFIG, 100);
    properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 3000);
    properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,
        aggProperties.getProducerCompressionType());
    this.kafkaProducer = new KafkaProducer<>(properties);
    kafkaProducerHealthChecker = new KafkaProducerHealthChecker(kafkaProducer,
        aggProperties.getTopic(), Duration.ofSeconds(30), commonThreadPools.getScheduler());
    kafkaProducerHealthChecker.start();
  }

  @PreDestroy
  public void close() {
    kafkaProducerHealthChecker.stop();
    this.kafkaProducer.close();
  }

  public void dispatch(AuthInfo authInfo, WriteMetricsRequestV4 request) {
    StringBuilder sb = new StringBuilder();
    for (WriteMetricsRequestV4.TaskResult taskResult : request.getResultsList()) {
      if (taskResult.hasCompleteness()) {
        List<AggTask> aggTasks = storage.getByTableName(taskResult.getRefCollectKey());
        if (aggTasks.isEmpty()) {
          continue;
        }

        AggProtos.AggTaskValue aggTaskValue = AggProtos.AggTaskValue.newBuilder() //
            .setType(2) //
            .setTimestamp(taskResult.getTimestamp()) //
            .putExtension("refCollectKey", taskResult.getRefCollectKey()) //
            .putExtension("refTargetKey", taskResult.getRefTargetKey()) //
            .putExtension("ok", Boolean.toString(taskResult.getCompleteness().getOk())) //
            .build();

        for (AggTask aggTask : aggTasks) {
          AggTaskKey aggTaskKey = new AggTaskKey(authInfo.getTenant(), aggTask.getAggId(), "");
          send(aggTaskKey, aggTaskValue);
        }

        continue;
      }

      if (taskResult.getTable().getRowsCount() == 0) {
        continue;
      }

      WriteMetricsRequestV4.Table table = taskResult.getTable();
      WriteMetricsRequestV4.Header header = table.getHeader();

      List<AggTask> aggTasks = storage.getByMetric(header.getMetricName());
      for (AggTask aggTask : aggTasks) {
        String partition = buildPartition(sb, authInfo, aggTask);

        AggTaskKey aggKey = new AggTaskKey(authInfo.getTenant(), aggTask.getAggId(), partition);
        AggProtos.AggTaskValue.Builder aggTaskValue = AggProtos.AggTaskValue.newBuilder() //
            .setType(0) //
            .setMetric(header.getMetricName())
            .putExtension("refCollectKey", taskResult.getRefCollectKey()) //
            .putExtension("refTargetKey", taskResult.getRefTargetKey()) //
        ;

        for (WriteMetricsRequestV4.Row row : table.getRowsList()) {

          Map<String, String> tags = Maps.newHashMapWithExpectedSize(header.getTagKeysCount());
          for (int i = 0; i < header.getTagKeysCount(); i++) {
            tags.put(header.getTagKeys(i), row.getTagValues(i));
          }

          if (row.getValueValuesList().size() != 1) {
            continue;
          }

          DataNode dataNode = row.getValueValues(0);

          AggProtos.InDataNode.Builder b =
              AggProtos.InDataNode.newBuilder().setTimestamp(row.getTimestamp()) //
                  .putAllTags(tags);

          switch (dataNode.getType()) {
            case 0:
              b.setType(0);
              b.setCount(1).setFloat64Value(dataNode.getValue());
              break;
            case 1:
              b.setType(0);
              b.setCount(dataNode.getCount()).setFloat64Value(dataNode.getValue());
              break;
            case 2:
              b.setType(1);
              b.setBytesValue(dataNode.getBytes());
              break;
          }
          aggTaskValue.addInDataNodes(b);
        }
        if (send(aggKey, aggTaskValue.build())) {
          StatUtils.KAFKA_SEND.add(StringsKey.of("v4"),
              new long[] {1, aggTaskValue.getInDataNodesCount()});
        }
      }
    }
  }

  public void dispatch(AuthInfo authInfo, WriteMetricsRequestV1 request) {
    Map<String, List<Point>> groupByName = new HashMap<>();

    for (Point point : request.getPointList()) {
      groupByName.computeIfAbsent(point.getMetricName(), i -> new ArrayList<>()).add(point);
    }

    StringBuilder sb = new StringBuilder();
    for (Map.Entry<String, List<Point>> e : groupByName.entrySet()) {
      List<AggTask> aggTasks = storage.getByMetric(e.getKey());
      if (aggTasks.isEmpty()) {
        continue;
      }

      for (AggTask aggTask : aggTasks) {
        String partition = buildPartition(sb, authInfo, aggTask);
        AggTaskKey aggTaskKey = new AggTaskKey(authInfo.getTenant(), aggTask.getAggId(), partition);

        AggProtos.AggTaskValue.Builder aggTaskValue =
            AggProtos.AggTaskValue.newBuilder().setMetric(e.getKey());

        List<Point> points = e.getValue();

        for (Point point : points) { //
          aggTaskValue.addInDataNodes(AggProtos.InDataNode.newBuilder() //
              .setType(0) //
              .setTimestamp(point.getTimestamp()) //
              .putAllTags(point.getTagsMap()) //
              .setFloat64Value(point.getNumberValuesOrDefault("value", 0)) //
              .build()); //
        }

        AggProtos.AggTaskValue taskValue = aggTaskValue.build();

        if (send(aggTaskKey, taskValue)) {
          StatUtils.KAFKA_SEND.add(StringsKey.of("v1"),
              new long[] {1, taskValue.getInDataNodesCount()});
        }
      }
    }
  }

  public void dispatchUpEvent(AuthInfo authInfo, ReportEventRequest request) {
    // for (ReportEventRequest.Event e : request.getEventsList()) {
    // if (e.getEventType().equals("STAT") && e.getPayloadType().equals("log_monitor_up")) {
    // String key = e.getTagsOrDefault("t_key", null);
    // if (key == null) {
    // continue;
    // }
    // String[] ss = key.split("/");
    // if (ss.length != 2) {
    // continue;
    // }
    // String refCollectKey = ss[0];
    // String refTargetKey = ss[1];
    //
    // List<AggTask> aggTasks = storage.getByTableName(refCollectKey);
    // if (aggTasks.isEmpty()) {
    // continue;
    // }
    //
    // AggProtos.AggTaskValue aggTaskValue = AggProtos.AggTaskValue.newBuilder() //
    // .setType(2) //
    // .setTimestamp(e.getEventTimestamp()) //
    // .putExtension("refCollectKey", refCollectKey) //
    // .putExtension("refTargetKey", refTargetKey) //
    // .build();
    //
    // for (AggTask aggTask : aggTasks) {
    // // TODO partition ???
    // AggTaskKey aggTaskKey = new AggTaskKey(authInfo.getTenant(), aggTask.getAggId(), "");
    // send(aggTaskKey, aggTaskValue);
    // }
    // }
    // }
  }

  public void dispatchDetailData(AuthInfo authInfo, Table table) {
    List<AggTask> aggTasks = this.storage.getByMetric(table.getName());
    if (CollectionUtils.isEmpty(aggTasks)) {
      return;
    }

    AggProtos.AggTaskValue.Builder b = AggProtos.AggTaskValue.newBuilder() //
        .setType(0) //
        .setTimestamp(table.getTimestamp()) //
        .setMetric(table.getName()); //

    AggProtos.Table.Builder tb = AggProtos.Table.newBuilder();

    tb.setName(table.getName()).setTimestamp(table.getTimestamp());
    tb.setHeader(AggProtos.Table.Header.newBuilder() //
        .addAllTagKeys(table.getHeader().getTagKeys()) //
        .addAllFieldKeys(table.getHeader().getFieldKeys())); //

    for (Row row : table.getRows()) {

      AggProtos.Table.Row.Builder pbRow = AggProtos.Table.Row.newBuilder();
      pbRow.setTimestamp(row.getTimestamp());
      pbRow.addAllTagValues(row.getTagValues());
      for (Double d : row.getFieldValues()) {
        pbRow.addFieldValues(
            AggProtos.BasicField.newBuilder().setType(0).setFloat64Value(d).build());
      }

      tb.addRow(pbRow);
    }

    b.setDataTable(tb);
    AggProtos.AggTaskValue aggTaskValue = b.build();

    for (AggTask aggTask : aggTasks) {
      AggTaskKey aggTaskKey = new AggTaskKey(authInfo.getTenant(), aggTask.getAggId());
      if (send(aggTaskKey, aggTaskValue)) {
        StatUtils.KAFKA_SEND.add(StringsKey.of("detail"), new long[] {1, table.getRows().size()});
      }
    }
  }

  public boolean supportsDetail(String name) {
    return CollectionUtils.isNotEmpty(this.storage.getByMetric(name));
  }

  private boolean send(AggTaskKey key, AggProtos.AggTaskValue value) {
    if (!kafkaProducerHealthChecker.isHealthy()) {
      int count = value.getInDataNodesCount();
      if (value.hasDataTable()) {
        count += value.getDataTable().getRowCount();
      }
      StatUtils.KAFKA_SEND.add(StringsKey.of("discard"), new long[] {1, count});
      return false;
    }

    ProducerRecord<AggTaskKey, AggProtos.AggTaskValue> record =
        new ProducerRecord<>(aggProperties.getTopic(), key, value);

    // When kafka is unhealthy, the calling to `KafkaProducer.send` itself will block at waiting for
    // metadata.
    // If the metadata already exists, well, we hit the cache and the send method doesn't block.
    // In any case, we'd better have a health check for kafka and fail quickly in unhealthy
    // situations instead of blocking. (Although this will occasionally lose some data)
    kafkaProducer.send(record, (metadata, exception) -> {
      if (exception != null) {
        int count = value.getInDataNodesCount();
        if (value.hasDataTable()) {
          count += value.getDataTable().getRowCount();
        }
        log.error("[agg] [{}] write kafka error, metric=[{}] size=[{}]", //
            key, value.getMetric(), count, exception);
      }
    });
    return true;
  }

  public void dispatchRecords(AuthInfo ai, String name, List<Record> records) {
    List<AggTask> aggTasks = storage.getByMetric(name);
    if (aggTasks.isEmpty()) {
      return;
    }

    StringBuilder sb = new StringBuilder();
    for (AggTask aggTask : aggTasks) {
      String partition = buildPartition(sb, ai, aggTask);
      AggTaskKey aggTaskKey = new AggTaskKey(ai.getTenant(), aggTask.getAggId(), partition);

      AggProtos.AggTaskValue.Builder aggTaskValue =
          AggProtos.AggTaskValue.newBuilder().setMetric(name);

      for (Record record : records) {
        AggProtos.InDataNode.Builder b = AggProtos.InDataNode.newBuilder() //
            .setType(2) //
            .setTimestamp(record.getTimestamp());

        record.getTags().forEach((k, v) -> {
          if (v != null) {
            b.putTags(k, v);
          }
        });

        record.getFields().forEach((k, v) -> {
          if (v instanceof Number) {
            putField(b, k, ((Number) v).doubleValue());
          } else if (v instanceof String) {
            putField(b, k, v.toString());
          }
        });

        aggTaskValue.addInDataNodes(b); //
      }

      AggProtos.AggTaskValue taskValue = aggTaskValue.build();

      if (send(aggTaskKey, taskValue)) {
        if (aggTask.getExtension().isDebug()) {
          log.info("send to kafka {} {}", aggTaskKey.getAggId(), taskValue.getInDataNodesCount());
        }
        StatUtils.KAFKA_SEND.add(StringsKey.of("v1"),
            new long[] {1, taskValue.getInDataNodesCount()});
      }
    }
  }

  public void dispatchSpans(AuthInfo ai, List<SpanDO> spans) {
    if (CollectionUtils.isEmpty(spans)) {
      return;
    }

    String name = "_span";
    List<AggTask> aggTasks = storage.getByMetric(name);
    if (aggTasks.isEmpty()) {
      return;
    }

    StringBuilder sb = new StringBuilder();
    for (AggTask aggTask : aggTasks) {
      String partition = buildPartition(sb, ai, aggTask);
      AggTaskKey aggTaskKey = new AggTaskKey(ai.getTenant(), aggTask.getAggId(), partition);

      AggProtos.AggTaskValue.Builder aggTaskValue =
          AggProtos.AggTaskValue.newBuilder().setMetric(name);

      for (SpanDO span : spans) {
        AggProtos.InDataNode.Builder b = AggProtos.InDataNode.newBuilder() //
            .setType(2) //
            .setTimestamp(span.getEndTime());

        maybePutTag(b, "trace_id", span.getTraceId());
        maybePutTag(b, "kind", span.getKind());
        maybePutTag(b, "name", span.getName());
        maybePutTag(b, "trace_status", Integer.toString(span.getTraceStatus()));

        putField(b, "latency", span.getLatency());
        for (Map.Entry<String, Object> e : span.getTags().entrySet()) {
          Object value = e.getValue();
          if (value instanceof String) {
            maybePutTag(b, e.getKey(), (String) e.getValue());
          }
        }

        aggTaskValue.addInDataNodes(b); //
      }

      AggProtos.AggTaskValue taskValue = aggTaskValue.build();

      if (send(aggTaskKey, taskValue)) {
        if (aggTask.getExtension().isDebug()) {
          log.info("send to kafka {} {}", aggTaskKey.getAggId(), taskValue.getInDataNodesCount());
        }
        StatUtils.KAFKA_SEND.add(StringsKey.of("v1"),
            new long[] {1, taskValue.getInDataNodesCount()});
      }
    }

  }

  private static void maybePutTag(AggProtos.InDataNode.Builder b, String key, String value) {
    if (value != null) {
      b.putTags(key, value);
    }
  }

  private static void putField(AggProtos.InDataNode.Builder b, String key, String value) {
    b.putFields(key, AggProtos.BasicField.newBuilder().setType(1)
        .setBytesValue(ByteString.copyFromUtf8(value)).build());
  }

  private static void putField(AggProtos.InDataNode.Builder b, String key, double value) {
    b.putFields(key, AggProtos.BasicField.newBuilder().setType(0).setFloat64Value(value).build());
  }

  @NotNull
  private static String buildPartition(StringBuilder sb, AuthInfo authInfo, AggTask aggTask) {
    if (sb == null) {
      sb = new StringBuilder();
    }
    sb.setLength(0);

    if (CollectionUtils.isNotEmpty(aggTask.getPartitionKeys())) {
      for (PartitionKey partitionKey : aggTask.getPartitionKeys()) {
        String ref = partitionKey.getRef();
        switch (ref) {
          case "meta.tenant":
            sb.append(partitionKey.getAs()).append('=').append(authInfo.getTenant()).append(',');
            break;
        }
      }
    }

    return sb.toString();
  }
}
