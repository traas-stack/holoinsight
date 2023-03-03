/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import io.holoinsight.server.apm.common.utils.IDManager;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Call implements CallType {
  private String id;
  private String sourceId;
  private String sourceName;
  private String destId;
  private String destName;
  private String component;
  private ResponseMetric metric;

  @Getter
  @Setter
  public static class DeepCall extends Call implements CallType {
    private String sourceServiceName;
    private String destServiceName;

    public void buildFromEndpointRelation(String entityId) {
      IDManager.EndpointID.EndpointRelationDefine endpointRelationDefine =
          IDManager.EndpointID.analysisRelationId(entityId);
      IDManager.ServiceID.ServiceIDDefinition sourceService =
          IDManager.ServiceID.analysisId(endpointRelationDefine.getSourceServiceId());
      IDManager.ServiceID.ServiceIDDefinition destService =
          IDManager.ServiceID.analysisId(endpointRelationDefine.getDestServiceId());

      this.setId(entityId);
      this.setSourceId(IDManager.EndpointID.buildId(endpointRelationDefine.getSourceServiceId(),
          endpointRelationDefine.getSource()));
      this.setSourceName(endpointRelationDefine.getSource());
      this.setSourceServiceName(sourceService.getName());

      this.setDestId(IDManager.EndpointID.buildId(endpointRelationDefine.getDestServiceId(),
          endpointRelationDefine.getDest()));
      this.setDestName(endpointRelationDefine.getDest());
      this.setDestServiceName(destService.getName());
    }

    public void buildFromInstanceRelation(String entityId, String component) {
      IDManager.ServiceInstanceID.ServiceInstanceRelationDefine serviceRelationDefine =
          IDManager.ServiceInstanceID.analysisRelationId(entityId);
      IDManager.ServiceInstanceID.InstanceIDDefinition sourceInstanceIDDefinition =
          IDManager.ServiceInstanceID.analysisId(serviceRelationDefine.getSourceId());
      IDManager.ServiceInstanceID.InstanceIDDefinition destInstanceIDDefinition =
          IDManager.ServiceInstanceID.analysisId(serviceRelationDefine.getDestId());

      this.setId(entityId);
      this.setSourceId(serviceRelationDefine.getSourceId());
      this.setSourceName(sourceInstanceIDDefinition.getName());
      this.setSourceServiceName(
          IDManager.ServiceID.analysisId(sourceInstanceIDDefinition.getServiceId()).getName());

      this.setDestId(serviceRelationDefine.getDestId());
      this.setDestName(destInstanceIDDefinition.getName());
      this.setDestServiceName(
          IDManager.ServiceID.analysisId(destInstanceIDDefinition.getServiceId()).getName());
      this.setComponent(component);
    }
  }


  public void buildFromServiceRelation(String entityId, String component) {
    final IDManager.ServiceID.ServiceRelationDefine serviceRelationDefine =
        IDManager.ServiceID.analysisRelationId(entityId);

    this.setId(entityId);
    this.setSourceId(serviceRelationDefine.getSourceId());
    this.setSourceName(IDManager.ServiceID.analysisId(sourceId).getName());
    this.setDestId(serviceRelationDefine.getDestId());
    this.setDestName(IDManager.ServiceID.analysisId(destId).getName());
    this.setComponent(component);
  }

}
