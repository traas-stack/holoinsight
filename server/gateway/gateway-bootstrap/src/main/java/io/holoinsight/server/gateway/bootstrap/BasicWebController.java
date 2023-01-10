/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.bootstrap;

import io.holoinsight.server.common.web.ApiResp;
import io.holoinsight.server.common.web.InternalWebApi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author sw1136562366
 */
@RestController
@RequestMapping("/internal/api/gateway/basic")
@InternalWebApi
public class BasicWebController {
  /**
   * 给出该实例的运行时基本信息
   *
   * @return
   */
  @GetMapping("/info")
  public Object info() {
    // 1. 各种重要资源的加载数量
    return ApiResp.success();
  }

  @GetMapping("/ip")
  public Object ip() throws UnknownHostException {
    // 1. 各种重要资源的加载数量
    return InetAddress.getLocalHost().getHostAddress();
  }

  @GetMapping("/ip2")
  public Object ip2() throws UnknownHostException {
    // 1. 各种重要资源的加载数量
    return InetAddress.getLocalHost().getHostAddress();
  }

  @GetMapping("/git")
  public Object git() throws IOException {
    Properties properties = new Properties();
    InputStream is =
        getClass().getClassLoader().getResourceAsStream("cloudmonitor-gateway-git.properties");
    if (is != null) {
      properties.load(is);
    } else {
      properties.put("message", "fail to find git.properties in classpath");
    }
    return properties;
  }
}
