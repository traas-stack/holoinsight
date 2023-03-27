/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.specification;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.holoinsight.server.apm.common.constants.Const;
import org.apache.commons.lang3.StringUtils;

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
  }

  public static String sw2Otlp(String name) {
    if (name == null) {
      return null;
    }
    if (OTEL_SW_MAPPINGS.containsKey(name)) {
      return OTEL_SW_MAPPINGS.get(name);
    }
    if (!StringUtils.startsWith(name, Const.OTLP_ATTRIBUTES_PREFIX)) {
      return Const.OTLP_ATTRIBUTES_PREFIX + name;
    }
    return name;
  }

  public static String otlp2Sw(String name) {

    if (name == null) {
      return null;
    }
    if (OTEL_SW_MAPPINGS.inverse().containsKey(name)) {
      return OTEL_SW_MAPPINGS.inverse().get(name);
    }
    if (StringUtils.startsWith(name, Const.OTLP_ATTRIBUTES_PREFIX)) {
      return name.substring(Const.OTLP_ATTRIBUTES_PREFIX.length());
    }
    return name;
  }

}
