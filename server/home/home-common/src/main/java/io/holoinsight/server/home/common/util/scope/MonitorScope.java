/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.StringUtil;
import org.springframework.util.StringUtils;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorScope.java, v 0.1 2022年03月14日 5:10 下午 jinsong.yjs Exp $
 */
public class MonitorScope {
  public static final String defaultTenantId = "-1";

  public String tenant;
  public String workspace;
  public String accessId;
  public String accessKey;

  public String getWorkspace() {
    if (StringUtil.isBlank(workspace)) {
      return null;
    }
    return workspace;
  }

  public void setWorkspace(String workspace) {
    this.workspace = workspace;
  }

  public String getTenant() {
    if (StringUtil.isBlank(tenant)) {
      return null;
    }
    return tenant;
  }

  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  /**
   * 获取 tenantId, 如果值不合法则返回默认值
   */
  public String getTenantIdOrDefault() {
    String tenant = getTenant();
    if (StringUtil.isBlank(tenant)) {
      tenant = defaultTenantId;
    }
    return tenant;
  }

  public String getTenantIdOrException() {
    String tenant = getTenant();
    if (StringUtil.isBlank(tenant)) {
      throw new MonitorException("tenant is null");
    }
    return tenant;
  }

  public static boolean legalValue(String value) {
    return !StringUtils.isEmpty(value) && !"-1".equals(value);
  }
}
