/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.holoinsight.server.apm.common.constants.Const;
import org.apache.commons.lang3.StringUtils;

/**
 * Used to be compatible with the OpenTelemetry protocol when querying. <br>
 * For example: <br>
 * Convert between SkyWalking protocol and OpenTelemetry protocol. <br>
 * Use custom tags query without adding prefix {attributes.} <br>
 * etc.
 */
public class OtlpMappings {

  private static final BiMap<String, String> OTLP_SW_MAPPINGS = HashBiMap.create();

  static {
    OTLP_SW_MAPPINGS.put("tenant", "resource.tenant");
    OTLP_SW_MAPPINGS.put("serviceName", "resource.service.name");
    OTLP_SW_MAPPINGS.put("serviceInstanceName", "resource.service.instance.name");
    OTLP_SW_MAPPINGS.put("endpointName", "name");
  }

  /**
   * null -> null <br>
   * tenant -> resource.tenant <br>
   * serviceName -> resource.service.name <br>
   * serviceInstanceName -> resource.service.instance.name <br>
   * endpointName -> name <br>
   * attributes.* -> attributes.* <br>
   * resource.* -> resource.* <br>
   * OTHERWISE: * -> attributes.*
   *
   * @param name
   * @return
   */
  public static String toOltp(String name) {
    if (name == null) {
      return null;
    }
    if (OTLP_SW_MAPPINGS.containsKey(name)) {
      return OTLP_SW_MAPPINGS.get(name);
    }
    if (StringUtils.startsWith(name, Const.OTLP_ATTRIBUTES_PREFIX)
        || StringUtils.startsWith(name, Const.OTLP_RESOURCE_PREFIX)) {
      return name;
    }
    return Const.OTLP_ATTRIBUTES_PREFIX + name;
  }

  /**
   * null -> null <br>
   * resource.tenant -> tenant <br>
   * resource.service.name -> serviceName <br>
   * resource.service.instance.name -> serviceInstanceName <br>
   * name -> endpointName <br>
   * attributes.* -> * <br>
   * OTHERWISE: * -> *
   *
   * @param name
   * @return
   */
  public static String fromOtlp(String name) {

    if (name == null) {
      return null;
    }
    if (OTLP_SW_MAPPINGS.inverse().containsKey(name)) {
      return OTLP_SW_MAPPINGS.inverse().get(name);
    }
    if (StringUtils.startsWith(name, Const.OTLP_ATTRIBUTES_PREFIX)) {
      return name.substring(Const.OTLP_ATTRIBUTES_PREFIX.length());
    }
    return name;
  }

}
