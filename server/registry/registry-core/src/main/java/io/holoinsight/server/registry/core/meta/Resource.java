/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.meta;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2022/7/5
 *
 * @author zzhb101
 */
@Getter
@Setter
public class Resource {
  /* 通用字段 begin */
  private String name;
  private String namespace;
  /**
   * k8s labels
   */
  private Map<String, String> labels;
  /**
   * k8s annotations
   */
  private Map<String, String> annotations;
  /* 通用字段 end */

  /* 特有字段 begin */
  /**
   * pod, 取自 app.kubernetes.io/name 标签, 如果没有则取 app 标签
   */
  private String app;
  /**
   * pod/node/service(可能没有)
   */
  private String ip;
  private String hostname;
  private String hostIP;
  /* 特有字段 end */

  private Map<String, String> spec;
}
