/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.ctl;

import java.util.Map;

public interface ProductStatusService {

  boolean switchOn();

  boolean productClosed(MonitorProductCode productCode, Map<String, String> tags);

}
