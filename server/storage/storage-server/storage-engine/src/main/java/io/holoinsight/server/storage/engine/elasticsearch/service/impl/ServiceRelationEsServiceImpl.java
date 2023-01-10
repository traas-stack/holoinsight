/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.elasticsearch.model.ServiceRelationEsDO;
import io.holoinsight.server.storage.engine.elasticsearch.service.RecordEsService;
import io.holoinsight.server.storage.engine.elasticsearch.service.ServiceRelationEsService;
import org.springframework.stereotype.Service;

@ConditionalOnFeature("trace")
@Service
public class ServiceRelationEsServiceImpl extends RecordEsService<ServiceRelationEsDO>
    implements ServiceRelationEsService {

}
