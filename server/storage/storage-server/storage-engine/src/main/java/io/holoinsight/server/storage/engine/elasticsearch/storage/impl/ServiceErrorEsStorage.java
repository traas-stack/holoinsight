/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.elasticsearch.storage.impl;

import io.holoinsight.server.storage.engine.model.ServiceErrorDO;
import io.holoinsight.server.storage.engine.storage.ServiceErrorStorage;

public class ServiceErrorEsStorage extends RecordEsStorage<ServiceErrorDO>
    implements ServiceErrorStorage {
}
