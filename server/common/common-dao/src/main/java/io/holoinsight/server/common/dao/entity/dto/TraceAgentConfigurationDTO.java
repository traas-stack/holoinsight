/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.common.dao.entity.dto;

import io.holoinsight.server.common.Const;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author jsy1001de
 * @version 1.0: TraceAgentConfigurationDTO.java, Date: 2024-06-14 Time: 16:41
 */
@Data
@AllArgsConstructor
public class TraceAgentConfigurationDTO {
  private String tenant;
  private String service;
  private String type;
  private String language;
  private Map<String, Object> configuration;
  private String creator;
  private String modifier;

  private Date gmtCreate;

  private Date gmtModified;


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
