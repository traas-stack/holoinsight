/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.engine.storage;

import io.holoinsight.server.storage.common.model.query.Service;

import java.io.IOException;
import java.util.List;

public interface ServiceOverviewStorage {

  List<Service> getServiceList(String tenant, long startTime, long endTime) throws IOException;
}
