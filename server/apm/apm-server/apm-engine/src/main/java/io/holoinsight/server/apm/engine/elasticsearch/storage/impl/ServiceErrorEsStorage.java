/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.storage.impl;

import io.holoinsight.server.apm.engine.model.ServiceErrorDO;
import io.holoinsight.server.apm.engine.storage.ServiceErrorStorage;

public class ServiceErrorEsStorage extends RecordEsStorage<ServiceErrorDO>
    implements ServiceErrorStorage {
}
