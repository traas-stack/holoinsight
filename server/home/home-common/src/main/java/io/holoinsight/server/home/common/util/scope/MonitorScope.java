/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import io.holoinsight.server.home.common.util.StringUtil;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorScope.java, v 0.1 2022年03月14日 5:10 下午 jinsong.yjs Exp $
 */
public class MonitorScope {

  public String tenant;
  public String workspace;
  public String environment;
  public String accessId;
  public String accessKey;
  public Object accessConfig;
  public List<String> accreditWsList = new ArrayList<>();

  public String getWorkspace() {
    if (StringUtil.isBlank(workspace)) {
      return null;
    }
    return workspace;
  }

  public String getEnvironment() {
    if (StringUtil.isBlank(environment)) {
      return "SERVER";
    }
    return environment;
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

  public void setAccreditWsList(List<String> accreditWsList) {
    this.accreditWsList = accreditWsList;
  }

  public List<String> getAccreditWsList() {
    return accreditWsList;
  }

  public Object getAccessConfig() {
    return accessConfig;
  }

  public static boolean legalValue(String value) {
    return !StringUtils.isEmpty(value) && !"-1".equals(value);
  }
}
