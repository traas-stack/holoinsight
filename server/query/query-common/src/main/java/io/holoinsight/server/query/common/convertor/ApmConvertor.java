/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.query.common.convertor;

import com.google.common.collect.Iterables;
import io.holoinsight.server.apm.common.model.query.BasicTrace;
import io.holoinsight.server.apm.common.model.query.Call;
import io.holoinsight.server.apm.common.model.query.Endpoint;
import io.holoinsight.server.apm.common.model.query.Node;
import io.holoinsight.server.apm.common.model.query.ResponseMetric;
import io.holoinsight.server.apm.common.model.query.Service;
import io.holoinsight.server.apm.common.model.query.ServiceInstance;
import io.holoinsight.server.apm.common.model.query.SlowSql;
import io.holoinsight.server.apm.common.model.query.StatisticData;
import io.holoinsight.server.apm.common.model.query.StatisticDataList;
import io.holoinsight.server.apm.common.model.query.Topology;
import io.holoinsight.server.apm.common.model.query.TraceBrief;
import io.holoinsight.server.apm.common.model.query.TraceTree;
import io.holoinsight.server.apm.common.model.query.VirtualComponent;
import io.holoinsight.server.apm.common.model.specification.sw.KeyValue;
import io.holoinsight.server.apm.common.model.specification.sw.LogEntity;
import io.holoinsight.server.apm.common.model.specification.sw.Ref;
import io.holoinsight.server.apm.common.model.specification.sw.RefType;
import io.holoinsight.server.apm.common.model.specification.sw.Span;
import io.holoinsight.server.apm.common.model.specification.sw.Tag;
import io.holoinsight.server.apm.common.model.specification.sw.Trace;
import io.holoinsight.server.common.event.Event;
import io.holoinsight.server.query.grpc.QueryProto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xiangwanpeng
 * @version : StorageConvertor.java, v 0.1 2022年10月08日 14:28 xiangwanpeng Exp $
 */
public class ApmConvertor {

  public static QueryProto.Trace convertTrace(Trace trace) {
    if (trace == null) {
      return null;
    }
    QueryProto.Trace.Builder traceBuilder = QueryProto.Trace.newBuilder();
    List<Span> spans = trace.getSpans();
    if (CollectionUtils.isNotEmpty(spans)) {
      for (Span span : spans) {
        traceBuilder.addSpans(convertSpan(span));
      }
    }
    return traceBuilder.build();
  }

  public static Trace convertTrace(QueryProto.Trace traceProto) {
    if (traceProto == null) {
      return null;
    }
    Trace trace = new Trace(traceProto.getSpansList().stream().map(ApmConvertor::convertSpan)
        .collect(Collectors.toList()));
    return trace;
  }

  public static QueryProto.TraceTreeList convertTraceTree(List<TraceTree> traceTreeList) {
    if (CollectionUtils.isEmpty(traceTreeList)) {
      return null;
    }
    QueryProto.TraceTreeList.Builder traceTreeListBuilder = QueryProto.TraceTreeList.newBuilder();
    traceTreeList.forEach(traceTree -> {
      QueryProto.TraceTree.Builder traceTreeBuilder = QueryProto.TraceTree.newBuilder();
      traceTreeBuilder.setSpan(convertSpan(traceTree.getSpan()));
      QueryProto.TraceTreeList childList = convertTraceTree(traceTree.getChildren());
      if (childList != null) {
        traceTreeBuilder.addAllChildren(childList.getTraceTreeList());
      }
      if (traceTree.getMesh() != null) {
        traceTreeBuilder.setMesh(convertSpan(traceTree.getMesh()));
      }
      traceTreeListBuilder.addTraceTree(traceTreeBuilder.build());
    });
    return traceTreeListBuilder.build();
  }

  public static List<TraceTree> convertTraceTree(QueryProto.TraceTreeList traceTreeList) {
    if (traceTreeList == null) {
      return null;
    }
    List<TraceTree> result = new ArrayList<>();
    traceTreeList.getTraceTreeList().forEach(traceTree -> {
      TraceTree root = new TraceTree();
      if (traceTree.hasMesh()) {
        root.setMesh(convertSpan(traceTree.getMesh()));
      }
      root.setSpan(convertSpan(traceTree.getSpan()));
      List<QueryProto.TraceTree> childrenList = traceTree.getChildrenList();
      if (CollectionUtils.isNotEmpty(childrenList)) {
        QueryProto.TraceTreeList.Builder builder = QueryProto.TraceTreeList.newBuilder();
        List<TraceTree> children = convertTraceTree(builder.addAllTraceTree(childrenList).build());
        Collections.sort(children,
            (o1, o2) -> (StringUtils.compare(String.valueOf(o1.getSpan().getStartTime()),
                String.valueOf(o2.getSpan().getStartTime()))));
        root.setChildren(children);
      }
      result.add(root);
    });

    return result;
  }

  public static QueryProto.TraceBrief convertTraceBrief(TraceBrief traceBrief) {
    if (traceBrief == null) {
      return null;
    }
    QueryProto.TraceBrief.Builder traceBriefBuilder = QueryProto.TraceBrief.newBuilder();
    List<BasicTrace> basicTraces = traceBrief.getTraces();
    if (CollectionUtils.isNotEmpty(basicTraces)) {
      for (BasicTrace basicTrace : basicTraces) {
        if (CollectionUtils.isNotEmpty(basicTrace.getEndpointNames())
            && basicTrace.getEndpointNames().get(0) != null) {
          traceBriefBuilder.addTraces(convertBasicTrace(basicTrace));
        }
      }
    }
    return traceBriefBuilder.build();
  }

  public static TraceBrief convertTraceBrief(QueryProto.TraceBrief traceBriefProto) {
    return new TraceBrief(traceBriefProto.getTracesList().stream()
        .map(ApmConvertor::convertBasicTrace).collect(Collectors.toList()));
  }

  public static QueryProto.StatisticData convertStatisticData(StatisticData statisticData) {
    if (statisticData == null) {
      return null;
    }
    QueryProto.StatisticData.Builder statisticDataBuilder = QueryProto.StatisticData.newBuilder();
    if (statisticData.getResources() != null) {
      statisticDataBuilder.putAllResources(statisticData.getResources());
    }
    if (statisticData.getDatas() != null) {
      statisticDataBuilder.putAllDatas(statisticData.getDatas());
    }
    return statisticDataBuilder.build();
  }

  public static StatisticData convertStatisticData(QueryProto.StatisticData statisticDataProto) {
    return new StatisticData(statisticDataProto.getResourcesMap(),
        statisticDataProto.getDatasMap());
  }


  public static QueryProto.BasicTrace convertBasicTrace(BasicTrace basicTrace) {
    return QueryProto.BasicTrace.newBuilder().addAllServiceNames(basicTrace.getServiceNames())
        .addAllServiceInstanceNames(basicTrace.getServiceInstanceNames())
        .addAllEndpointNames(basicTrace.getEndpointNames()).setDuration(basicTrace.getDuration())
        .setStart(basicTrace.getStart()).setIsError(basicTrace.isError())
        .addAllTraceIds(basicTrace.getTraceIds()).build();
  }

  public static BasicTrace convertBasicTrace(QueryProto.BasicTrace basicTraceProto) {
    return new BasicTrace(basicTraceProto.getServiceNamesList(),
        basicTraceProto.getServiceInstanceNamesList(), basicTraceProto.getEndpointNamesList(),
        basicTraceProto.getDuration(), basicTraceProto.getStart(), basicTraceProto.getIsError(),
        basicTraceProto.getTraceIdsList());
  }

  public static QueryProto.Span convertSpan(Span span) {
    QueryProto.Span.Builder spanBuilder = QueryProto.Span.newBuilder().setTraceId(span.getTraceId())
        .setSpanId(span.getSpanId()).setParentSpanId(span.getParentSpanId());
    List<Ref> refs = span.getRefs();
    if (CollectionUtils.isNotEmpty(refs)) {
      spanBuilder.addAllRefs(Iterables.transform(refs, ApmConvertor::convertRef));
    }
    if (span.getServiceCode() != null) {
      spanBuilder.setServiceCode(span.getServiceCode());
    }
    if (span.getServiceInstanceName() != null) {
      spanBuilder.setServiceInstanceName(span.getServiceInstanceName());
    }
    spanBuilder.setStartTime(span.getStartTime()).setEndTime(span.getEndTime());
    if (span.getEndpointName() != null) {
      spanBuilder.setEndpointName(span.getEndpointName());
    }
    if (span.getType() != null) {
      spanBuilder.setType(span.getType());
    }
    if (span.getPeer() != null) {
      spanBuilder.setPeer(span.getPeer());
    }
    if (span.getComponent() != null) {
      spanBuilder.setComponent(span.getComponent());
    }
    spanBuilder.setIsError(span.isError());
    if (span.getLayer() != null) {
      spanBuilder.setLayer(span.getLayer());
    }

    List<KeyValue> tags = span.getTags();
    if (CollectionUtils.isNotEmpty(tags)) {
      spanBuilder.addAllTags(Iterables.transform(tags, ApmConvertor::convertKeyValue));
    }

    List<LogEntity> logs = span.getLogs();
    if (CollectionUtils.isNotEmpty(logs)) {
      spanBuilder.addAllLogs(Iterables.transform(logs, ApmConvertor::convertLogEntity));
    }
    spanBuilder.setIsRoot(span.isRoot());
    return spanBuilder.build();
  }

  public static Span convertSpan(QueryProto.Span spanProto) {
    if (spanProto == null) {
      return null;
    }
    Span span = new Span(spanProto.getTraceId(), spanProto.getSpanId(), spanProto.getParentSpanId(),
        spanProto.getRefsList().stream().map(ApmConvertor::convertRef).collect(Collectors.toList()),
        spanProto.getServiceCode(), spanProto.getServiceInstanceName(), spanProto.getStartTime(),
        spanProto.getEndTime(), spanProto.getEndpointName(), spanProto.getType(),
        spanProto.getPeer(), spanProto.getComponent(), spanProto.getIsError(), spanProto.getLayer(),
        spanProto.getTagsList().stream().map(ApmConvertor::convertKeyValue)
            .collect(Collectors.toList()),
        spanProto.getLogsList().stream().map(ApmConvertor::convertLogEntity)
            .collect(Collectors.toList()),
        spanProto.getIsRoot(), spanProto.getIsMesh());
    return span;
  }

  public static QueryProto.Ref convertRef(Ref ref) {
    QueryProto.Ref.Builder refBuilder = QueryProto.Ref.newBuilder();
    if (ref.getTraceId() != null) {
      refBuilder.setTraceId(ref.getTraceId());
    }
    refBuilder.setParentSpanId(ref.getParentSpanId());
    if (ref.getType() != null) {
      refBuilder.setType(ref.getType().name());
    }
    return refBuilder.build();
  }

  public static Ref convertRef(QueryProto.Ref refProto) {
    if (refProto == null) {
      return null;
    }
    Ref ref = new Ref(refProto.getTraceId(), refProto.getParentSpanId(),
        RefType.valueOf(refProto.getType()));
    return ref;
  }

  public static QueryProto.KeyValue convertKeyValue(KeyValue keyValue) {
    return QueryProto.KeyValue.newBuilder().setKey(keyValue.getKey()).setValue(keyValue.getValue())
        .build();
  }

  public static KeyValue convertKeyValue(QueryProto.KeyValue keyValueProto) {
    if (keyValueProto == null) {
      return null;
    }
    return new KeyValue(keyValueProto.getKey(), keyValueProto.getValue());
  }

  public static List<Tag> convertTagsMap(Map<String, String> tagsMap) {
    if (tagsMap == null || tagsMap.isEmpty()) {
      return null;
    } else {
      return tagsMap.entrySet().stream().map(entry -> new Tag(entry.getKey(), entry.getValue()))
          .collect(Collectors.toList());
    }
  }

  public static Map<String, String> convertTagList(List<Tag> tagList) {
    if (tagList == null) {
      return null;
    }
    Map<String, String> tagsMap = new HashMap<>();
    tagList.forEach(tag -> tagsMap.put(tag.getKey(), tag.getValue()));
    return tagsMap;
  }

  public static QueryProto.LogEntity convertLogEntity(LogEntity logEntity) {
    QueryProto.LogEntity.Builder logBuilder =
        QueryProto.LogEntity.newBuilder().setTime(logEntity.getTime());
    List<KeyValue> datas = logEntity.getData();
    if (CollectionUtils.isNotEmpty(datas)) {
      logBuilder.addAllData(Iterables.transform(datas, ApmConvertor::convertKeyValue));
    }
    return logBuilder.build();
  }

  public static LogEntity convertLogEntity(QueryProto.LogEntity logEntityProto) {
    if (logEntityProto == null) {
      return null;
    }
    return new LogEntity(logEntityProto.getTime(), logEntityProto.getDataList().stream()
        .map(ApmConvertor::convertKeyValue).collect(Collectors.toList()));
  }

  public static QueryProto.ResponseMetric convertResponseMetric(ResponseMetric metric) {
    QueryProto.ResponseMetric.Builder builder = QueryProto.ResponseMetric.newBuilder();

    builder.setAvgLatency(metric.getAvgLatency());
    builder.setP95Latency(metric.getP95Latency());
    builder.setP99Latency(metric.getP99Latency());
    builder.setTotalCount(metric.getTotalCount());
    builder.setErrorCount(metric.getErrorCount());
    builder.setSuccessRate(metric.getSuccessRate());

    return builder.build();
  }

  public static ResponseMetric deConvertResponseMetric(QueryProto.ResponseMetric metric) {
    ResponseMetric result = new ResponseMetric();
    result.setAvgLatency(metric.getAvgLatency());
    result.setP95Latency(metric.getP95Latency());
    result.setP99Latency(metric.getP99Latency());
    result.setErrorCount(metric.getErrorCount());
    result.setTotalCount(metric.getTotalCount());
    result.setSuccessRate(metric.getSuccessRate());

    return result;
  }

  public static QueryProto.Meta convertService(Service service) {
    QueryProto.Meta.Builder builder = QueryProto.Meta.newBuilder();
    builder.setName(service.getName());

    if (service.getMetric() != null) {
      builder.setMetric(convertResponseMetric(service.getMetric()));
    }

    return builder.build();
  }

  public static Service deConvertService(QueryProto.Meta meta) {
    Service result = new Service();
    result.setName(meta.getName());
    if (meta.hasMetric()) {
      result.setMetric(deConvertResponseMetric(meta.getMetric()));
    }

    return result;
  }

  public static QueryProto.Meta convertEndpoint(Endpoint endpoint) {
    QueryProto.Meta.Builder builder = QueryProto.Meta.newBuilder();
    builder.setName(endpoint.getName());

    if (endpoint.getMetric() != null) {
      builder.setMetric(convertResponseMetric(endpoint.getMetric()));
    }

    return builder.build();
  }

  public static Endpoint deConvertEndpoint(QueryProto.Meta meta) {
    Endpoint result = new Endpoint();
    result.setName(meta.getName());

    if (meta.hasMetric()) {
      result.setMetric(deConvertResponseMetric(meta.getMetric()));
    }

    return result;
  }

  public static QueryProto.Meta convertServiceInstance(ServiceInstance service) {
    QueryProto.Meta.Builder builder = QueryProto.Meta.newBuilder();
    builder.setName(service.getName());

    if (service.getMetric() != null) {
      builder.setMetric(convertResponseMetric(service.getMetric()));
    }

    return builder.build();
  }

  public static ServiceInstance deConvertServiceInstance(QueryProto.Meta meta) {
    ServiceInstance result = new ServiceInstance();
    result.setName(meta.getName());
    if (meta.hasMetric()) {
      result.setMetric(deConvertResponseMetric(meta.getMetric()));
    }

    return result;
  }

  public static QueryProto.VirtualComponent convertDb(VirtualComponent db) {
    QueryProto.VirtualComponent.Builder builder = QueryProto.VirtualComponent.newBuilder();
    builder.setAddress(db.getAddress());
    builder.setType(db.getType());
    if (db.getMetric() != null) {
      builder.setMetric(convertResponseMetric(db.getMetric()));
    }

    return builder.build();
  }

  public static VirtualComponent deConvertDb(QueryProto.VirtualComponent db) {
    VirtualComponent result = new VirtualComponent();
    result.setAddress(db.getAddress());
    result.setType(db.getType());

    if (db.hasMetric()) {
      result.setMetric(deConvertResponseMetric(db.getMetric()));
    }

    return result;
  }

  public static QueryProto.Topology convertTopology(Topology topology) {
    QueryProto.Topology.Builder builder = QueryProto.Topology.newBuilder();

    topology.getNodes().forEach(node -> {
      builder.addNode(convertNode(node));
    });
    topology.getCalls().forEach(call -> {
      builder.addCall(convertCall(call));
    });

    return builder.build();
  }

  public static Topology deConvertTopology(QueryProto.Topology topology) {
    Topology result = new Topology();

    topology.getNodeList().forEach(node -> {
      result.getNodes().add(deConvertNode(node));
    });
    topology.getCallList().forEach(call -> {
      result.getCalls().add(deConvertCall(call));
    });

    return result;
  }

  public static QueryProto.Call convertCall(Call call) {
    QueryProto.Call.Builder builder = QueryProto.Call.newBuilder();
    builder.setId(call.getId());
    builder.setSourceId(call.getSourceId());
    builder.setSourceName(call.getSourceName());
    builder.setDestId(call.getDestId());
    builder.setDestName(call.getDestName());
    builder.setMetric(convertResponseMetric(call.getMetric()));

    if (call instanceof Call.DeepCall) {
      builder.setSourceServiceName(((Call.DeepCall) call).getSourceServiceName());
      builder.setDestServiceName(((Call.DeepCall) call).getDestServiceName());
    }

    return builder.build();
  }

  public static Call deConvertCall(QueryProto.Call call) {
    if (!StringUtils.isEmpty(call.getSourceServiceName())) {
      Call.DeepCall result = new Call.DeepCall();
      result.setId(call.getId());
      result.setSourceName(call.getSourceName());
      result.setSourceId(call.getSourceId());
      result.setDestId(call.getDestId());
      result.setDestName(call.getDestName());
      result.setMetric(deConvertResponseMetric(call.getMetric()));
      result.setSourceServiceName(call.getSourceServiceName());
      result.setDestServiceName(call.getDestServiceName());
      return result;
    } else {
      Call result = new Call();
      result.setId(call.getId());
      result.setSourceName(call.getSourceName());
      result.setSourceId(call.getSourceId());
      result.setDestId(call.getDestId());
      result.setDestName(call.getDestName());
      result.setMetric(deConvertResponseMetric(call.getMetric()));
      return result;
    }
  }

  public static QueryProto.Node convertNode(Node node) {
    QueryProto.Node.Builder builder = QueryProto.Node.newBuilder();
    builder.setId(node.getId());
    builder.setName(node.getName());
    builder.setIsReal(node.isReal());
    if (node.getMetric() != null) {
      builder.setMetric(convertResponseMetric(node.getMetric()));
    }
    if (!StringUtils.isEmpty(node.getType())) {
      builder.setType(node.getType());
    }
    if (node instanceof Node.EndpointNode) {
      builder.setServiceName(((Node.EndpointNode) node).getServiceName());
    } else if (node instanceof Node.ServiceInstanceNode) {
      builder.setServiceName(((Node.ServiceInstanceNode) node).getServiceName());
    }

    return builder.build();
  }

  public static Node deConvertNode(QueryProto.Node node) {
    if (!StringUtils.isEmpty(node.getServiceName())) {
      Node.EndpointNode result = new Node.EndpointNode();
      result.setServiceName(node.getServiceName());
      result.setId(node.getId());
      result.setName(node.getName());
      result.setType(node.getType());
      result.setReal(node.getIsReal());

      if (node.hasMetric()) {
        result.setMetric(deConvertResponseMetric(node.getMetric()));
      }
      return result;
    } else {
      Node result = new Node();
      result.setId(node.getId());
      result.setName(node.getName());
      result.setType(node.getType());
      result.setReal(node.getIsReal());
      if (node.hasMetric()) {
        result.setMetric(deConvertResponseMetric(node.getMetric()));
      }
      return result;
    }
  }

  public static QueryProto.SlowSql convertSlowSql(SlowSql slowSql) {
    QueryProto.SlowSql.Builder builder = QueryProto.SlowSql.newBuilder();
    builder.setServiceName(slowSql.getServiceName());
    builder.setAddress(slowSql.getAddress());
    builder.setTraceId(slowSql.getTraceId());
    builder.setLatency(slowSql.getLatency());
    builder.setStatement(slowSql.getStatement());
    builder.setStartTime(slowSql.getStartTime());

    return builder.build();
  }

  public static SlowSql deConvertSlowSql(QueryProto.SlowSql slowSql) {
    SlowSql result = new SlowSql();
    result.setLatency(slowSql.getLatency());
    result.setStatement(slowSql.getStatement());
    result.setServiceName(slowSql.getServiceName());
    result.setTraceId(slowSql.getTraceId());
    result.setAddress(slowSql.getAddress());
    result.setStartTime(slowSql.getStartTime());

    return result;
  }

  public static QueryProto.Event convertEvent(Event event) {
    QueryProto.Event.Builder builder = QueryProto.Event.newBuilder();
    builder.setTenant(event.getTenant());
    builder.setName(event.getName());
    builder.setId(event.getId());
    builder.setTimestamp(event.getTimestamp());
    builder.setType(event.getType());
    if (event.getTags() != null) {
      builder.putAllTags(event.getTags());
    }
    return builder.build();
  }

  public static Event deConvertEvent(QueryProto.Event event) {
    Event result = new Event();
    result.setId(event.getId());
    result.setName(event.getName());
    result.setTimestamp(event.getTimestamp());
    result.setType(event.getType());
    result.setTags(event.getTagsMap());
    return result;
  }

  public static QueryProto.StatisticDataList convert(StatisticDataList statisticDataList) {
    QueryProto.StatisticDataList.Builder builder = QueryProto.StatisticDataList.newBuilder();
    if (!CollectionUtils.isEmpty(statisticDataList.getStatisticDataList())) {
      for (StatisticData data : statisticDataList.getStatisticDataList()) {
        builder.addStatisticData(convertStatisticData(data));
      }
    }
    return builder.build();
  }
}
