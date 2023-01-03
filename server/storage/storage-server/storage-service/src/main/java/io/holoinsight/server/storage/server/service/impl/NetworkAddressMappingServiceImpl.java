/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.server.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.elasticsearch.model.NetworkAddressMappingEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.service.NetworkAddressMappingEsService;
import io.holoinsight.server.storage.server.service.NetworkAddressMappingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@ConditionalOnFeature("trace")
public class NetworkAddressMappingServiceImpl implements NetworkAddressMappingService {

    @Autowired
    private NetworkAddressMappingEsService networkAddressMappingEsService;

    @Override
    public void insert(List<NetworkAddressMappingEsDO> addressMappingList) throws IOException {
        networkAddressMappingEsService.batchInsert(addressMappingList);
    }

    @Override
    public List<NetworkAddressMappingEsDO> loadByTime(long timeBucketInMinute) throws IOException {
        return networkAddressMappingEsService.loadByTime(timeBucketInMinute);
    }
}
