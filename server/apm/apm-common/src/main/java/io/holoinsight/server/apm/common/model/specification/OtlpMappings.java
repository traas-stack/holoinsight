/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.holoinsight.server.apm.common.constants.Const;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Used to be compatible with the OpenTelemetry protocol when querying. <br>
 * For example: <br>
 * Convert between SkyWalking protocol and OpenTelemetry protocol. <br>
 * Use custom tags query without adding prefix {attributes.} <br>
 * etc.
 */
public class OtlpMappings {

  private static final Map<String, BiMap<String, String>> OTLP_SW_MAPPINGS = new HashMap<>();

  private static final String NAME = "name";

  static {
    BiMap<String, String> spanMappings = HashBiMap.create();
    spanMappings.put("tenant", "resource.tenant");
    spanMappings.put("serviceName", "resource.service.name");
    spanMappings.put("serviceInstanceName", "resource.service.instance.name");
    spanMappings.put("endpointName", NAME);
    spanMappings.put("trace_status", "trace_status");
    spanMappings.put("span_id", "span_id");
    spanMappings.put("time_bucket", "time_bucket");
    spanMappings.put("trace_id", "trace_id");
    spanMappings.put("kind", "kind");
    spanMappings.put("parent_span_id", "parent_span_id");
    spanMappings.put("start_time", "start_time");
    spanMappings.put("end_time", "end_time");
    OTLP_SW_MAPPINGS.put("holoinsight-span", spanMappings);
  }

  /**
   * null -> null <br>
   * for holoinsight-span: tenant -> resource.tenant <br>
   * serviceName -> resource.service.name <br>
   * serviceInstanceName -> resource.service.instance.name <br>
   * endpointName -> name <br>
   * attributes.* -> attributes.* <br>
   * resource.* -> resource.* <br>
   * name -> name <br>
   * OTHERWISE: * -> attributes.*
   *
   * @param name
   * @return
   */
  public static String toOtlp(String index, String name) {
    BiMap<String, String> mappings = OTLP_SW_MAPPINGS.get(index);
    if (mappings == null || name == null) {
      return name;
    }
    if (mappings.containsKey(name)) {
      return mappings.get(name);
    }
    if (StringUtils.startsWith(name, Const.OTLP_ATTRIBUTES_PREFIX)
        || StringUtils.startsWith(name, Const.OTLP_RESOURCE_PREFIX)
        || StringUtils.equals(name, NAME)) {
      return name;
    }
    return Const.OTLP_ATTRIBUTES_PREFIX + name;
  }

  /**
   * null -> null <br>
   * for holoinsight-span: resource.tenant -> tenant <br>
   * resource.service.name -> serviceName <br>
   * resource.service.instance.name -> serviceInstanceName <br>
   * name -> endpointName <br>
   * attributes.* -> * <br>
   * OTHERWISE: * -> *
   *
   * @param name
   * @return
   */
  public static String fromOtlp(String index, String name) {
    BiMap<String, String> mappings = OTLP_SW_MAPPINGS.get(index);
    if (mappings == null || name == null) {
      return name;
    }
    mappings = mappings.inverse();
    if (mappings.containsKey(name)) {
      return mappings.get(name);
    }
    if (StringUtils.startsWith(name, Const.OTLP_ATTRIBUTES_PREFIX)) {
      return name.substring(Const.OTLP_ATTRIBUTES_PREFIX.length());
    }
    return name;
  }

}
