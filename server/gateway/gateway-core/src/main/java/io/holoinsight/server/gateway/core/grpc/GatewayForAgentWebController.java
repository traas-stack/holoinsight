/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import io.holoinsight.server.common.web.ApiResp;
import io.holoinsight.server.common.web.InternalWebApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * <p>
 * created at 2022/3/8
 *
 * @author sw1136562366
 */
@RestController
@RequestMapping("/internal/api/gateway/agent")
@InternalWebApi
public class GatewayForAgentWebController {
  @Autowired
  private GatewayServerForAgent gatewayServerForAgent;

  /**
   * <p>
   * restart.
   * </p>
   */
  @GetMapping("/restart")
  public ApiResp restart(@RequestParam(value = "delay", defaultValue = "3") int delayStart)
      throws IOException, InterruptedException {
    gatewayServerForAgent.restart(delayStart);
    return ApiResp.success();
  }
}
