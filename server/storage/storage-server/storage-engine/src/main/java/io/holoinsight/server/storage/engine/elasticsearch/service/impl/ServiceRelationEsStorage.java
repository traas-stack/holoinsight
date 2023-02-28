/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.service.impl;

import io.holoinsight.server.common.springboot.ConditionalOnFeature;
import io.holoinsight.server.storage.engine.model.ServiceRelationDO;
import io.holoinsight.server.storage.engine.storage.ServiceRelationStorage;
import org.springframework.stereotype.Service;

@ConditionalOnFeature("trace")
@Service("serviceRelationEsStorage")
public class ServiceRelationEsStorage extends RecordEsStorage<ServiceRelationDO>
    implements ServiceRelationStorage {

}
