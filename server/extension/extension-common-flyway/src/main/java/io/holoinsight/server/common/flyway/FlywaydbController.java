/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.flyway;

import io.holoinsight.server.common.web.ApiResp;
import io.holoinsight.server.common.web.InternalWebApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author jiwliu
 * @date 2023/3/1
 */

@Slf4j
@ResponseBody
@RequestMapping("/internal/api/flyway")
@InternalWebApi
public class FlywaydbController {

  @Autowired
  private FlywayService flywayService;

  @GetMapping("/{action}")
  public Object action(@PathVariable("action") String action) {
    Object data = flywayService.doAction(action);
    return ApiResp.success(data, "success");
  }
}
