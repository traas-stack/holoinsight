/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.elasticsearch.model.EndpointRelationEsDO;
import io.holoinsight.server.storage.engine.EndpointRelationStorage;
import org.springframework.stereotype.Service;

@ConditionalOnFeature("trace")
@Service
public class EndpointRelationEsServiceImpl extends RecordEsService<EndpointRelationEsDO>
    implements EndpointRelationStorage {

}
