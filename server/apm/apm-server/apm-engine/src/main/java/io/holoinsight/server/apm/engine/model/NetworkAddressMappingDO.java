/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.model;

import io.holoinsight.server.apm.common.model.specification.sw.NetworkAddressMapping;
import io.holoinsight.server.apm.common.model.storage.annotation.Column;
import io.holoinsight.server.apm.common.model.storage.annotation.ModelAnnotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;

import static io.holoinsight.server.apm.engine.model.NetworkAddressMappingDO.INDEX_NAME;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ModelAnnotation(name = INDEX_NAME)
public class NetworkAddressMappingDO extends RecordDO {

  public static final String INDEX_NAME = "holoinsight-network_address_mapping";
  public static final String ADDRESS = "address";
  public static final String SERVICE_NAME = "service_name";
  public static final String SERVICE_INSTANCE_NAME = "service_instance_name";

  @Id
  private String id;
  @Column(name = ADDRESS)
  private String address;
  @Column(name = SERVICE_NAME)
  private String serviceName;
  @Column(name = SERVICE_INSTANCE_NAME)
  private String serviceInstanceName;

  @Override
  public String indexName() {
    return INDEX_NAME;
  }

  public static NetworkAddressMappingDO fromNetworkAddressMapping(
      NetworkAddressMapping networkAddressMapping) {
    NetworkAddressMappingDO networkAddressMappingEsDO = new NetworkAddressMappingDO();
    BeanUtils.copyProperties(networkAddressMapping, networkAddressMappingEsDO);
    networkAddressMappingEsDO.setTimestamp(networkAddressMapping.getEndTime());
    return networkAddressMappingEsDO;
  }

}
