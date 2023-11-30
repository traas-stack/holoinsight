/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * created at 2023/11/30
 *
 * @author xzchaoo
 */
public interface GatewayMetaService {
  List<Map<String, Object>> queryByExample(String table, Map<String, String> example);
}
