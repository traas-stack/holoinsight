/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.common.constants.Const;
import io.holoinsight.server.storage.common.model.query.Call;
import io.holoinsight.server.storage.common.model.query.Node;
import io.holoinsight.server.storage.common.model.query.ResponseMetric;
import io.holoinsight.server.storage.common.model.query.Topology;
import io.holoinsight.server.storage.common.utils.IDManager;
import io.holoinsight.server.storage.server.cache.NetworkAddressMappingCache;
import io.holoinsight.server.storage.engine.model.NetworkAddressMappingDO;
import io.holoinsight.server.storage.engine.model.SpanDO;
import io.holoinsight.server.storage.engine.storage.TopologyStorage;
import io.holoinsight.server.storage.server.service.TopologyService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@ConditionalOnFeature("trace")
public class TopologyServiceImpl implements TopologyService {

  @Resource
  @Qualifier("topologyEsStorage")
  private TopologyStorage topologyEsService;

  @Autowired
  private NetworkAddressMappingCache networkAddressMappingCache;

  private static final String USERID = IDManager.ServiceID.buildId(Const.USER_SERVICE_NAME, false);

  @Override
  public Topology getTenantTopology(String tenant, long startTime, long endTime,
      Map<String, String> termParams) throws IOException {
    List<Call> calls = topologyEsService.getTenantCalls(tenant, startTime, endTime, termParams);

    Topology topology = buildServiceTopo(calls);
    setNodeMetric(tenant, topology.getNodes(), startTime, endTime, termParams);
    return topology;
  }

  @Override
  public Topology getServiceTopology(String tenant, String service, long startTime, long endTime,
      int depth, Map<String, String> termParams) throws IOException {
    List<Call> allCalls = topologyEsService.getTenantCalls(tenant, startTime, endTime, termParams);
    List<Call> result = filterDepend(allCalls, service, depth);

    Topology topology = buildServiceTopo(result);
    setNodeMetric(tenant, topology.getNodes(), startTime, endTime, termParams);
    return topology;
  }

  @Override
  public Topology getServiceInstanceTopology(String tenant, String service, String serviceInstance,
      long startTime, long endTime, int depth, Map<String, String> termParams) throws IOException {
    List<Call.DeepCall> calls = new ArrayList<>();
    List<Call.DeepCall> sourceEndpointList = new ArrayList<>();
    Call.DeepCall sourceEndpoint = new Call.DeepCall();
    sourceEndpoint.setSourceServiceName(service);
    sourceEndpoint.setSourceName(serviceInstance);
    sourceEndpointList.add(sourceEndpoint);

    List<Call.DeepCall> destEndpointList = new ArrayList<>();
    Call.DeepCall destEndpoint = new Call.DeepCall();
    destEndpoint.setDestServiceName(service);
    destEndpoint.setDestName(serviceInstance);
    destEndpointList.add(destEndpoint);

    for (int i = depth; i > 0; i--) {
      List<Call.DeepCall> newSourceEndpointList = new ArrayList<>();
      for (Call.DeepCall item : sourceEndpointList) {
        newSourceEndpointList
            .addAll(topologyEsService.getServiceInstanceCalls(tenant, item.getSourceServiceName(),
                item.getSourceName(), startTime, endTime, Const.DEST, termParams));
      }
      sourceEndpointList.clear();
      sourceEndpointList.addAll(newSourceEndpointList);

      List<Call.DeepCall> newDestEndpointList = new ArrayList<>();
      for (Call.DeepCall item : destEndpointList) {
        newDestEndpointList
            .addAll(topologyEsService.getServiceInstanceCalls(tenant, item.getDestServiceName(),
                item.getDestName(), startTime, endTime, Const.SOURCE, termParams));
      }
      destEndpointList.clear();
      destEndpointList.addAll(newDestEndpointList);

      calls.addAll(newSourceEndpointList);
      calls.addAll(newDestEndpointList);
    }

    Topology topology = buildServiceInstanceTopo(calls);
    setNodeMetric(tenant, topology.getNodes(), startTime, endTime, termParams);
    return topology;
  }

  @Override
  public Topology getEndpointTopology(String tenant, String service, String endpoint,
      long startTime, long endTime, int depth, Map<String, String> termParams) throws IOException {
    List<Call.DeepCall> calls = new ArrayList<>();
    List<Call.DeepCall> sourceEndpointList = new ArrayList<>();
    Call.DeepCall sourceEndpoint = new Call.DeepCall();
    sourceEndpoint.setSourceServiceName(service);
    sourceEndpoint.setSourceName(endpoint);
    sourceEndpointList.add(sourceEndpoint);

    List<Call.DeepCall> destEndpointList = new ArrayList<>();
    Call.DeepCall destEndpoint = new Call.DeepCall();
    destEndpoint.setDestServiceName(service);
    destEndpoint.setDestName(endpoint);
    destEndpointList.add(destEndpoint);

    for (int i = depth; i > 0; i--) {
      List<Call.DeepCall> newSourceEndpointList = new ArrayList<>();
      for (Call.DeepCall item : sourceEndpointList) {
        newSourceEndpointList
            .addAll(topologyEsService.getEndpointCalls(tenant, item.getSourceServiceName(),
                item.getSourceName(), startTime, endTime, Const.DEST, termParams));
      }
      sourceEndpointList.clear();
      sourceEndpointList.addAll(newSourceEndpointList);

      List<Call.DeepCall> newDestEndpointList = new ArrayList<>();
      for (Call.DeepCall item : destEndpointList) {
        newDestEndpointList
            .addAll(topologyEsService.getEndpointCalls(tenant, item.getDestServiceName(),
                item.getDestName(), startTime, endTime, Const.SOURCE, termParams));
      }
      destEndpointList.clear();
      destEndpointList.addAll(newDestEndpointList);

      calls.addAll(newSourceEndpointList);
      calls.addAll(newDestEndpointList);
    }

    Topology topology = buildEndpointTopo(calls);
    setNodeMetric(tenant, topology.getNodes(), startTime, endTime, termParams);
    return topology;
  }

  @Override
  public Topology getDbTopology(String tenant, String address, long startTime, long endTime,
      Map<String, String> termParams) throws IOException {
    List<Call> dbCalls = topologyEsService.getComponentCalls(tenant, address, startTime, endTime,
        Const.DEST, termParams);

    Topology topology = buildServiceTopo(dbCalls);
    setNodeMetric(tenant, topology.getNodes(), startTime, endTime, termParams);
    return topology;
  }

  @Override
  public Topology getMQTopology(String tenant, String address, long startTime, long endTime,
      Map<String, String> termParams) throws IOException {
    List<Call> calls = new ArrayList<>();
    calls.addAll(topologyEsService.getComponentCalls(tenant, address, startTime, endTime,
        Const.SOURCE, termParams));
    calls.addAll(topologyEsService.getComponentCalls(tenant, address, startTime, endTime,
        Const.DEST, termParams));

    Topology topology = buildServiceTopo(calls);
    setNodeMetric(tenant, topology.getNodes(), startTime, endTime, termParams);
    return topology;
  }

  private Topology buildEndpointTopo(List<Call.DeepCall> calls) {
    Topology topology = new Topology();
    Set<String> nodeIds = new HashSet<>();

    calls.forEach(call -> {
      if (!nodeIds.contains(call.getSourceId())) {
        topology.getNodes().add(buildEndpointNode(call.getSourceId(), call.getSourceServiceName()));
        nodeIds.add(call.getSourceId());
      }
      if (!nodeIds.contains(call.getDestId())) {
        topology.getNodes().add(buildEndpointNode(call.getDestId(), call.getDestServiceName()));
        nodeIds.add(call.getDestId());
      }
    });

    topology.getCalls().addAll(calls);
    return topology;
  }

  private void setNodeMetric(String tenant, List<Node> nodes, long startTime, long endTime,
      Map<String, String> termParams) throws IOException {
    if (nodes == null || nodes.isEmpty()) {
      return;
    }

    List<String> realNode =
        nodes.stream().filter(Node::isReal).map(Node::getName).collect(Collectors.toList());

    String aggField = SpanDO.resource(SpanDO.SERVICE_NAME);
    if (nodes.get(0) instanceof Node.EndpointNode) {
      aggField = SpanDO.NAME;
    } else if (nodes.get(0) instanceof Node.ServiceInstanceNode) {
      aggField = SpanDO.resource(SpanDO.SERVICE_INSTANCE_NAME);
    }

    Map<String, ResponseMetric> nodeMetric = topologyEsService.getServiceAggMetric(tenant, realNode,
        startTime, endTime, aggField, termParams);

    nodes.forEach(node -> {
      ResponseMetric metric = nodeMetric.get(node.getName());
      if (metric != null) {
        node.setMetric(metric);
      }
    });
  }

  private Topology buildServiceInstanceTopo(List<Call.DeepCall> calls) {
    Topology topology = new Topology();
    Set<String> nodeIds = new HashSet<>();
    Set<String> callIds = new HashSet<>();

    calls.forEach(call -> {
      if (!nodeIds.contains(call.getSourceId())) {
        topology.getNodes().add(buildServiceInstanceNode(call.getSourceId(), call.getComponent()));
        nodeIds.add(call.getSourceId());
      }

      if (networkAddressMappingCache.get(call.getDestName()) != null) {
        /*
         * If alias exists, mean this network address is representing a real service.
         */
        NetworkAddressMappingDO networkAddressAlias =
            networkAddressMappingCache.get(call.getDestName());

        String serviceId = IDManager.ServiceID.buildId(networkAddressAlias.getServiceName(), true);
        String destId = IDManager.ServiceInstanceID.buildId(serviceId,
            networkAddressAlias.getServiceInstanceName());

        call.setDestName(networkAddressAlias.getServiceInstanceName());
        call.setDestServiceName(networkAddressAlias.getServiceName());
        call.setDestId(destId);
      }
      if (!nodeIds.contains(call.getDestId())) {
        topology.getNodes().add(buildServiceInstanceNode(call.getDestId(), call.getComponent()));
        nodeIds.add(call.getDestId());
      }

      String relationId = IDManager.ServiceInstanceID.buildRelationId(
          new IDManager.ServiceInstanceID.ServiceInstanceRelationDefine(call.getSourceId(),
              call.getDestId()));

      if (!callIds.contains(relationId)) {
        callIds.add(relationId);
        topology.getCalls().add(call);
      }
    });

    return topology;
  }

  private Topology buildServiceTopo(List<Call> serviceRelationCalls) {
    Topology topology = new Topology();
    Set<String> nodeIds = new HashSet<>();
    Set<String> callIds = new HashSet<>();

    serviceRelationCalls.forEach(call -> {
      if (!nodeIds.contains(call.getSourceId())) {
        topology.getNodes().add(buildServiceNode(call.getSourceId(), call.getComponent()));
        nodeIds.add(call.getSourceId());
      }

      if (networkAddressMappingCache.get(call.getDestName()) != null) {
        /*
         * If alias exists, mean this network address is representing a real service.
         */
        NetworkAddressMappingDO networkAddressAlias =
            networkAddressMappingCache.get(call.getDestName());

        String serviceId = IDManager.ServiceID.buildId(networkAddressAlias.getServiceName(), true);
        call.setDestName(networkAddressAlias.getServiceName());
        call.setDestId(serviceId);
      }

      if (!nodeIds.contains(call.getDestId())) {
        topology.getNodes().add(buildServiceNode(call.getDestId(), call.getComponent()));
        nodeIds.add(call.getDestId());
      }

      String relationId = IDManager.ServiceID.buildRelationId(
          new IDManager.ServiceID.ServiceRelationDefine(call.getSourceId(), call.getDestId()));

      if (!callIds.contains(relationId)) {
        callIds.add(relationId);
        topology.getCalls().add(call);
      }
    });

    return topology;
  }

  private Node buildServiceNode(String serviceId, String component) {
    IDManager.ServiceID.ServiceIDDefinition serviceIDDefinition =
        IDManager.ServiceID.analysisId(serviceId);

    Node serviceNode = new Node();
    serviceNode.setId(serviceId);
    serviceNode.setName(serviceIDDefinition.getName());
    serviceNode.setType(Const.EMPTY_STRING);
    serviceNode.setReal(serviceIDDefinition.isReal());

    if (USERID.equals(serviceId)) {
      serviceNode.setType(Const.USER_SERVICE_NAME.toUpperCase());
    }

    if (!serviceNode.isReal() && StringUtils.isEmpty(serviceNode.getType())) {
      serviceNode.setType(component);
    }
    return serviceNode;
  }

  private Node.ServiceInstanceNode buildServiceInstanceNode(String serviceInstanceId,
      String component) {
    IDManager.ServiceInstanceID.InstanceIDDefinition instanceIDDefinition =
        IDManager.ServiceInstanceID.analysisId(serviceInstanceId);
    IDManager.ServiceID.ServiceIDDefinition serviceIDDefinition =
        IDManager.ServiceID.analysisId(instanceIDDefinition.getServiceId());

    Node.ServiceInstanceNode node = new Node.ServiceInstanceNode();
    node.setId(serviceInstanceId);
    node.setName(instanceIDDefinition.getName());
    node.setType(Const.EMPTY_STRING);
    node.setReal(serviceIDDefinition.isReal());
    node.setServiceName(serviceIDDefinition.getName());

    if (USERID.equals(instanceIDDefinition.getServiceId())) {
      node.setType(Const.USER_SERVICE_NAME.toUpperCase());
    }

    if (!node.isReal() && StringUtils.isEmpty(node.getType())) {
      node.setType(component);
    }
    return node;
  }

  private Node.EndpointNode buildEndpointNode(String endpointId, String serviceName) {
    IDManager.EndpointID.EndpointIDDefinition endpointIDDefinition =
        IDManager.EndpointID.analysisId(endpointId);

    Node.EndpointNode node = new Node.EndpointNode();
    node.setId(endpointId);
    node.setType(Const.EMPTY_STRING);
    node.setReal(true);
    node.setName(endpointIDDefinition.getEndpointName());
    node.setServiceName(serviceName);

    if (USERID.equals(endpointIDDefinition.getServiceId())) {
      node.setType(Const.USER_SERVICE_NAME.toUpperCase());
      node.setReal(false);
    }
    return node;
  }

  private List<Call> filterDepend(List<Call> allCalls, String service, int depth) {
    List<Call> result = new ArrayList<>();

    findSource(result, allCalls, Arrays.asList(service), depth);
    findTarget(result, allCalls, Arrays.asList(service), depth);

    return result;
  }

  private void findSource(List<Call> result, List<Call> allCalls, List<String> services,
      int depth) {
    if (depth == 0 || services.size() == 0) {
      return;
    }
    List<String> sourceServices = new ArrayList<>();
    Iterator<Call> iterator = allCalls.iterator();
    while (iterator.hasNext()) {
      Call call = iterator.next();

      for (String service : services) {
        if (call.getDestName().equals(service)) {
          result.add(call);
          sourceServices.add(call.getSourceName());
          iterator.remove();
        }
      }
    }

    findSource(result, allCalls, sourceServices, depth - 1);
  }

  private void findTarget(List<Call> result, List<Call> allCalls, List<String> services,
      int depth) {
    if (depth == 0 || services.size() == 0) {
      return;
    }
    List<String> targetServices = new ArrayList<>();
    Iterator<Call> iterator = allCalls.iterator();
    while (iterator.hasNext()) {
      Call call = iterator.next();

      for (String service : services) {
        if (call.getSourceName().equals(service)) {
          result.add(call);
          targetServices.add(call.getDestName());
          iterator.remove();
        }
      }
    }

    findTarget(result, allCalls, targetServices, depth - 1);
  }


}
