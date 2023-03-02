/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.web.impl;

import io.holoinsight.server.storage.web.TopologyApi;
import io.holoinsight.server.storage.common.model.query.QueryTopologyRequest;
import io.holoinsight.server.storage.common.model.query.Topology;
import io.holoinsight.server.storage.server.service.TopologyService;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@Slf4j
public class TopologyApiController implements TopologyApi {

  @Autowired
  private TopologyService topologyService;

  @Override
  public ResponseEntity<Topology> queryTenantTopology(QueryTopologyRequest request)
      throws IOException {
    String tenant;
    if (!Strings.isNullOrEmpty(request.getTenant())) {
      tenant = request.getTenant();
    } else {
      throw new IllegalArgumentException("The condition must contains tenant.");
    }

    Topology tenantTopology = topologyService.getTenantTopology(tenant, request.getStartTime(),
        request.getEndTime(), request.getTermParams());

    return ResponseEntity.ok(tenantTopology);
  }

  @Override
  public ResponseEntity<Topology> queryServiceTopology(QueryTopologyRequest request)
      throws IOException {
    String tenant = request.getTenant();
    String service = request.getServiceName();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(service)) {
      throw new IllegalArgumentException("The condition must contains tenant and service.");
    }

    int depth = request.getDepth() <= 0 ? 2 : request.getDepth();

    Topology serviceTopology = topologyService.getServiceTopology(tenant, service,
        request.getStartTime(), request.getEndTime(), depth, request.getTermParams());

    return ResponseEntity.ok(serviceTopology);
  }

  @Override
  public ResponseEntity<Topology> queryServiceInstanceTopology(QueryTopologyRequest request)
      throws IOException {
    String tenant = request.getTenant();
    String service = request.getServiceName();
    String serviceInstanceName = request.getServiceInstanceName();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(service)
        || Strings.isNullOrEmpty(serviceInstanceName)) {
      throw new IllegalArgumentException(
          "The condition must contains tenant and service and serviceInstanceName.");
    }

    int depth = request.getDepth() <= 0 ? 2 : request.getDepth();

    Topology serviceInstanceTopology =
        topologyService.getServiceInstanceTopology(tenant, service, serviceInstanceName,
            request.getStartTime(), request.getEndTime(), depth, request.getTermParams());

    return ResponseEntity.ok(serviceInstanceTopology);
  }

  @Override
  public ResponseEntity<Topology> queryEndpointTopology(QueryTopologyRequest request)
      throws IOException {
    String tenant = request.getTenant();
    String service = request.getServiceName();
    String endpoint = request.getEndpointName();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(service)
        || Strings.isNullOrEmpty(endpoint)) {
      throw new IllegalArgumentException(
          "The condition must contains tenant and service and endpoint.");
    }

    int depth = request.getDepth() <= 0 ? 2 : request.getDepth();

    Topology endpointTopology = topologyService.getEndpointTopology(tenant, service, endpoint,
        request.getStartTime(), request.getEndTime(), depth, request.getTermParams());

    return ResponseEntity.ok(endpointTopology);
  }

  @Override
  public ResponseEntity<Topology> queryDbTopology(QueryTopologyRequest request) throws IOException {
    String tenant = request.getTenant();
    String address = request.getAddress();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(address)) {
      throw new IllegalArgumentException("The condition must contains tenant and address.");
    }

    Topology dbTopology = topologyService.getDbTopology(tenant, address, request.getStartTime(),
        request.getEndTime(), request.getTermParams());

    return ResponseEntity.ok(dbTopology);
  }

  @Override
  public ResponseEntity<Topology> queryMQTopology(QueryTopologyRequest request) throws IOException {
    String tenant = request.getTenant();
    String address = request.getAddress();

    if (Strings.isNullOrEmpty(tenant) || Strings.isNullOrEmpty(address)) {
      throw new IllegalArgumentException("The condition must contains tenant and address.");
    }

    Topology mqTopology = topologyService.getMQTopology(tenant, address, request.getStartTime(),
        request.getEndTime(), request.getTermParams());

    return ResponseEntity.ok(mqTopology);
  }


}
