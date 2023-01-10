/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.trace.common;

/**
 * <p>
 * Transform class.
 * </p>
 *
 * @author sw1136562366
 */
public class Transform {

  /**
   * <p>
   * serviceInstanceTransform.
   * </p>
   */
  public static String serviceInstanceTransform(String service, String serviceInstance) {
    if (serviceInstance.contains("@")) {
      // only get ip
      return serviceInstance.split("@")[1];
    }
    return serviceInstance;
  }

}
