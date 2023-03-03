/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author jiwliu
 * @version : OtelMappings.java, v 0.1 2022年11月11日 17:26 xiangwanpeng Exp $
 */
public class OtlpMappings {

  private static final BiMap<String, String> OTEL_SW_MAPPINGS = HashBiMap.create();

  static {

    OTEL_SW_MAPPINGS.put("tenant", "resource.tenant");
    OTEL_SW_MAPPINGS.put("serviceName", "resource.service.name");
    OTEL_SW_MAPPINGS.put("serviceInstanceName", "resource.service.instance.name");
    OTEL_SW_MAPPINGS.put("endpointName", "name");

    OTEL_SW_MAPPINGS.put("appId", "attributes.appId");
    OTEL_SW_MAPPINGS.put("envId", "attributes.envId");
    OTEL_SW_MAPPINGS.put("stamp", "attributes.stamp");
    OTEL_SW_MAPPINGS.put("spanLayer", "attributes.spanLayer");
    OTEL_SW_MAPPINGS.put("rootErrorCode", "attributes.rootErrorCode");
    OTEL_SW_MAPPINGS.put("errorCode", "attributes.errorCode");
  }

  public static String sw2Otlp(String name) {
    return OTEL_SW_MAPPINGS.getOrDefault(name, name);
  }

  public static String otlp2Sw(String name) {
    return OTEL_SW_MAPPINGS.inverse().getOrDefault(name, name);
  }

}
