/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

import org.apache.commons.lang3.StringUtils;

import io.holoinsight.server.registry.core.utils.Dict;

/**
 * <p>
 * created at 2022/3/9
 *
 * @author zzhb101
 */
@Data
public class AgentJson {
  private String ip;
  private String hostname;
  private String version;
  private String os;
  private String arch;
  private Map<String, String> labels = new HashMap<>();
  private AgentK8sConfig k8s = null;
  private String app;
  private String mode;
  /**
   * 存储 agent 与服务端的建联信息
   */
  private ConnectionInfo connectionInfo;

  public void reuseStrings() {
    version = Dict.get(version);
    os = Dict.get(os);
    arch = Dict.get(arch);
    app = Dict.get(app);
    mode = Dict.get(mode);
  }

  public void setMode(String mode) {
    if (StringUtils.isEmpty(mode)) {
      mode = Agent.MODE_SIDECAR;
    }
    this.mode = mode;
  }

  /**
   * 一 internalGet 开头 而不是 get 开头, 防止被意外json序列化
   *
   * @return
   */
  public String internalGetConnectingRegistry() {
    if (connectionInfo != null) {
      return connectionInfo.getRegistry();
    }
    return null;
  }
}
