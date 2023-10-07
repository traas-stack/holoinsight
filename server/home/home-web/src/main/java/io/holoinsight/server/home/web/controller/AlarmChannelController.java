/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangsiyuan
 * @date 2022/7/14 9:36 下午
 */

@RestController
@RequestMapping("/webapi/v1/alarm/channel")
public class AlarmChannelController {

  @GetMapping("/sendMessage")
  @ResponseBody
  public void sendMessage(@RequestParam String type) {
    throw new UnsupportedOperationException("can not send alert message by facade.");
  }
}
