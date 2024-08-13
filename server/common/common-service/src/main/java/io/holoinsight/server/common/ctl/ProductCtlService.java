/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.ctl;

import java.util.Map;
import java.util.Set;

public interface ProductCtlService {

  boolean switchOn();

  boolean isMetricInWhiteList(String metric);

  boolean productClosed(MonitorProductCode productCode, Map<String, String> tags);

  Map<String, Set<String>> productCtl(MonitorProductCode code, Map<String, String> tags,
      String action) throws Exception;

  Map<String, Boolean> productStatus(Map<String, String> tags) throws Exception;

}
