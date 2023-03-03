/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.engine.model.ServiceInstanceRelationDO;
import io.holoinsight.server.apm.engine.storage.ServiceInstanceRelationStorage;

public class ServiceInstanceRelationEsStorage extends RecordEsStorage<ServiceInstanceRelationDO>
    implements ServiceInstanceRelationStorage {
}
