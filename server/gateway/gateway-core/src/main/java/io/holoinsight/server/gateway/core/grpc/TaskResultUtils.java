/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import io.holoinsight.server.gateway.grpc.WriteMetricsRequestV4;

/**
 * <p>
 * created at 2023/11/27
 *
 * @author xzchaoo
 */
public class TaskResultUtils {
  public static boolean isDetails(WriteMetricsRequestV4.TaskResult tr) {
    return "1".equals(tr.getExtensionOrDefault("details", null));
  }
}
