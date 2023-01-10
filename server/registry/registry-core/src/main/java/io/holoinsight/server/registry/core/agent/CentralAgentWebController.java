/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.agent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * created at 2022/6/21
 *
 * @author zzhb101
 */
@RestController
@RequestMapping("/internal/api/registry/agent/central")
public class CentralAgentWebController {
  @Autowired
  private CentralAgentService centralAgentService;

  @GetMapping("/state")
  public Object getState() {
    return centralAgentService.getState();
  }
}
