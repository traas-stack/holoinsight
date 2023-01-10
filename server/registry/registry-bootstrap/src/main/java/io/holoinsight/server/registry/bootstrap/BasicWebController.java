/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.bootstrap;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author zzhb101
 */
@RestController
@RequestMapping("/internal/api/registry/basic")
public class BasicWebController {
  @GetMapping("/git")
  public Object git() throws IOException {
    Properties properties = new Properties();
    InputStream is =
        getClass().getClassLoader().getResourceAsStream("cloudmonitor-registry-git.properties");
    if (is != null) {
      properties.load(is);
    } else {
      properties.put("message", "fail to find git.properties in classpath");
    }
    return properties;
  }

  @GetMapping("/ip")
  public Object ip() throws IOException {
    return InetAddress.getLocalHost().getHostAddress();
  }
}
