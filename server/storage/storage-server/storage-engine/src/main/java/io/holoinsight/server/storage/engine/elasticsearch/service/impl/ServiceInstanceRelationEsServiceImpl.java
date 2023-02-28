/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.elasticsearch.model.ServiceInstanceRelationEsDO;
import io.holoinsight.server.storage.engine.ServiceInstanceRelationStorage;
import org.springframework.stereotype.Service;

@ConditionalOnFeature("trace")
@Service("serviceInstanceRelationEsServiceImpl")
public class ServiceInstanceRelationEsServiceImpl
    extends RecordEsService<ServiceInstanceRelationEsDO> implements ServiceInstanceRelationStorage {

}
