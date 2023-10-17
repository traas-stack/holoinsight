/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.agg.v1.dispatcher;

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
import com.xzchaoo.commons.stat.Stats;
import com.xzchaoo.commons.stat.StringsKey;

import io.holoinsight.server.agg.v1.core.AggProperties;
import io.holoinsight.server.agg.v1.core.conf.AggTask;
import io.holoinsight.server.agg.v1.core.conf.PartitionKey;
import io.holoinsight.server.agg.v1.core.data.AggKeySerdes;
import io.holoinsight.server.agg.v1.core.data.AggTaskKey;
import io.holoinsight.server.agg.v1.core.data.AggValuesSerdes;
import io.holoinsight.server.agg.v1.pb.AggProtos;
import io.holoinsight.server.common.auth.AuthInfo;
import io.holoinsight.server.gateway.core.grpc.GatewayHook;
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

  @Autowired
  private AggProperties aggProperties;
  @Autowired
  private AggTaskV1StorageForDispatcher storage;
  @Autowired
  private AggConfig aggConfig;

  @PostConstruct
  public void init() {
    Properties properties = new Properties();
    properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
        aggProperties.getKafkaBootstrapServers());
    properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, AggKeySerdes.S.class.getName());
    properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, AggValuesSerdes.S.class.getName());
    properties.put(ProducerConfig.BATCH_SIZE_CONFIG, 64 * 1024);
    properties.put(ProducerConfig.LINGER_MS_CONFIG, 100);
    properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG,
        aggProperties.getProducerCompressionType());
    this.kafkaProducer = new KafkaProducer<>(properties);
  }

  @PreDestroy
  public void close() {
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

          ProducerRecord<AggTaskKey, AggProtos.AggTaskValue> record =
              new ProducerRecord<>(aggProperties.getTopic(), aggTaskKey, aggTaskValue);

          kafkaProducer.send(record);
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

        ProducerRecord<AggTaskKey, AggProtos.AggTaskValue> record =
            new ProducerRecord<>(aggProperties.getTopic(), aggKey, aggTaskValue.build());
        kafkaProducer.send(record);
        StatUtils.KAFKA_SEND.add(StringsKey.of("v4"), Stats.V_1);
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

        ProducerRecord<AggTaskKey, AggProtos.AggTaskValue> record =
            new ProducerRecord<>(aggProperties.getTopic(), aggTaskKey, taskValue);
        kafkaProducer.send(record);
        StatUtils.KAFKA_SEND.add(StringsKey.of("v1"), Stats.V_1);
      }
    }
  }

  public void dispatchUpEvent(AuthInfo authInfo, ReportEventRequest request) {
    if (true) {
      return;
    }

    for (ReportEventRequest.Event e : request.getEventsList()) {
      if (e.getEventType().equals("STAT") && e.getPayloadType().equals("log_monitor_up")) {
        String key = e.getTagsOrDefault("t_key", null);
        if (key == null) {
          continue;
        }
        String[] ss = key.split("/");
        if (ss.length != 2) {
          continue;
        }
        String refCollectKey = ss[0];
        String refTargetKey = ss[1];

        List<AggTask> aggTasks = storage.getByTableName(refCollectKey);
        if (aggTasks.isEmpty()) {
          continue;
        }

        AggProtos.AggTaskValue aggTaskValue = AggProtos.AggTaskValue.newBuilder() //
            .setType(2) //
            .setTimestamp(e.getEventTimestamp()) //
            .putExtension("refCollectKey", refCollectKey) //
            .putExtension("refTargetKey", refTargetKey) //
            .build();

        for (AggTask aggTask : aggTasks) {
          // TODO partition ???
          AggTaskKey aggTaskKey = new AggTaskKey(authInfo.getTenant(), aggTask.getAggId(), "");

          ProducerRecord<AggTaskKey, AggProtos.AggTaskValue> record =
              new ProducerRecord<>(aggProperties.getTopic(), aggTaskKey, aggTaskValue);
          kafkaProducer.send(record);
        }
      }
    }
  }

  public void dispatchDetailData(AuthInfo authInfo, List<GatewayHook.Data> request) {
    AggTaskKey aggTaskKey = new AggTaskKey(authInfo.getTenant(), request.get(0).getName(), "");

    AggProtos.AggTaskValue.Builder b = AggProtos.AggTaskValue.newBuilder() //
        .setType(0) //
        .setTimestamp(request.get(0).getTimestamp()).setMetric(request.get(0).getName());

    for (GatewayHook.Data d : request) {
      AggProtos.InDataNode.Builder inB = AggProtos.InDataNode.newBuilder().setType(2) //
          .putAllTags(d.getTags()) //
          .setTimestamp(d.getTimestamp());

      for (Map.Entry<String, Double> e : d.getFields().entrySet()) {
        AggProtos.BasicField bf =
            AggProtos.BasicField.newBuilder().setType(0).setFloat64Value(e.getValue()).build();
        inB.putFields(e.getKey(), bf);
      }

      b.addInDataNodes(inB.build());
    }

    AggProtos.AggTaskValue aggTaskValue = b.build();

    ProducerRecord<AggTaskKey, AggProtos.AggTaskValue> record =
        new ProducerRecord<>(aggProperties.getTopic(), aggTaskKey, aggTaskValue);
    kafkaProducer.send(record);
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
