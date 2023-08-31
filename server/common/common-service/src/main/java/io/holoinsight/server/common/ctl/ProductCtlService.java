/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.ctl;

public interface ProductCtlService {

  boolean switchOn();

  boolean productClosed(MonitorProductCode productCode, String uniqueId);


}
