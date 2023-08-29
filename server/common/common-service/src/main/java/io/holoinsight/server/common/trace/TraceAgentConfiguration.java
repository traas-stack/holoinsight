/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.trace;

import io.holoinsight.server.common.Const;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Dynamic configuration items, save the dynamic configuration of the agent corresponding to the
 * service.
 *
 * @author sw1136562366
 */
@Setter
@Getter
@ToString
public class TraceAgentConfiguration {
  private String tenant;
  private String service;
  private String type;
  private String language;
  private Map<String, String> configuration;
  /**
   * The uuid is calculated by the dynamic configuration of the service.
   */
  private volatile String uuid;

  public TraceAgentConfiguration(String tenant, String service, String type, String language,
      Map<String, String> configuration, String uuid) {
    this.tenant = tenant;
    this.service = service;
    this.type = type;
    this.language = language;
    this.configuration = configuration;
    this.uuid = uuid;
  }

  /**
   * <p>
   * getCacheKey.
   * </p>
   */
  public String getCacheKey() {
    List<String> cacheKeys = new ArrayList() {
      {
        add(tenant);
        add(service);
      }
    };
    if (!StringUtils.isEmpty(type)) {
      cacheKeys.add(type);
    }
    if (!StringUtils.isEmpty(language)) {
      cacheKeys.add(language);
    }
    return StringUtils.join(cacheKeys, Const.TRACE_AGENT_CONFIG_KEY_JOINER);
  }
}
