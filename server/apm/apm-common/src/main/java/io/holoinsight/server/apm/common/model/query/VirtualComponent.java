/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import io.holoinsight.server.apm.common.constants.Const;
import io.holoinsight.server.apm.common.utils.IDManager;
import lombok.Data;

@Data
public class VirtualComponent {
  private String address;
  private String component;
  private String type;
  private ResponseMetric metric;

  public void buildFromServiceRelation(String entityId, String component, String sourceOrDest) {
    IDManager.ServiceID.ServiceRelationDefine serviceRelationDefine =
        IDManager.ServiceID.analysisRelationId(entityId);

    String addressId;
    if (Const.SOURCE.equals(sourceOrDest)) {
      addressId = serviceRelationDefine.getDestId();
    } else {
      addressId = serviceRelationDefine.getSourceId();
    }

    String address = IDManager.ServiceID.analysisId(addressId).getName();
    this.setAddress(address);

    this.setComponent(component);
  }
}
