/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.meta;

import java.util.List;

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
public class FullSyncRequest {
  private String apikey;
  private String workspace;
  private String cluster;
  /**
   * namespace/pod/service/ingress
   */
  private String type;
  private List<Resource> resources;
}
