/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.model.ServiceInstanceRelationDO;
import io.holoinsight.server.storage.engine.storage.ServiceInstanceRelationStorage;
import org.springframework.stereotype.Service;

@ConditionalOnFeature("trace")
@Service("serviceInstanceRelationEsStorage")
public class ServiceInstanceRelationEsStorage extends RecordEsStorage<ServiceInstanceRelationDO>
    implements ServiceInstanceRelationStorage {

}