/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.cluster;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.holoinsight.server.registry.core.utils.ApiResp;

/**
 * <p>
 * created at 2022/4/19
 *
 * @author zzhb101
 */
@RestController
@RequestMapping("/internal/api/registry/cluster")
public class ClusterWebController {
  @Autowired
  private DefaultCluster defaultCluster;

  @GetMapping("/info")
  public ApiResp info() {
    DefaultCluster.State state = defaultCluster.state;
    return ApiResp.success(state.memberMap.keySet());
  }
}
