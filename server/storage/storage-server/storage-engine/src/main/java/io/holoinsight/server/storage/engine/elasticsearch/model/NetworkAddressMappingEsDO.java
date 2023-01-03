/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.model;

import io.holoinsight.server.storage.common.model.specification.sw.NetworkAddressMapping;
import io.holoinsight.server.storage.common.model.storage.annotation.Column;
import io.holoinsight.server.storage.common.model.storage.annotation.ModelAnnotation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;

import static io.holoinsight.server.storage.engine.elasticsearch.model.NetworkAddressMappingEsDO.INDEX_NAME;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ModelAnnotation(name = INDEX_NAME)
public class NetworkAddressMappingEsDO extends RecordEsDO {

    public static final String INDEX_NAME            = "holoinsight-network_address_mapping";
    public static final String ADDRESS               = "address";
    public static final String SERVICE_NAME          = "service_name";
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

    public static NetworkAddressMappingEsDO fromNetworkAddressMapping(NetworkAddressMapping networkAddressMapping) {
        NetworkAddressMappingEsDO networkAddressMappingEsDO = new NetworkAddressMappingEsDO();
        BeanUtils.copyProperties(networkAddressMapping, networkAddressMappingEsDO);
        return networkAddressMappingEsDO;
    }

}
