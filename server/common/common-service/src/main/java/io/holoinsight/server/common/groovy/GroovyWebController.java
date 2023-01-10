/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.groovy;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import io.holoinsight.server.common.web.InternalWebApi;

/**
 * 供内部使用, 可以动态执行任意脚本
 * <p>
 * created at 2022/3/4
 *
 * @author xzchaoo
 */
@ResponseBody
@RequestMapping("/internal/api/groovy")
@InternalWebApi
public class GroovyWebController {
  @Autowired
  private ApplicationContext ctx;
  @Autowired
  private GroovyConfig config;

  @PostMapping("/execute")
  public Object execute(@RequestHeader("token") String token, @RequestBody String body,
      HttpServletRequest request) {
    String remoteAddr = request.getRemoteAddr();
    if (!"127.0.0.1".equals(remoteAddr) && !"0:0:0:0:0:0:0:1".equals(remoteAddr)) {
      return "forbidden";
    }
    if (StringUtils.isEmpty(config.getBasic().getToken())
        || !token.equals(config.getBasic().getToken())) {
      return "forbidden";
    }
    Binding binding = new Binding();
    binding.setVariable("ctx", ctx);
    GroovyShell shell = new GroovyShell(createGCL(), binding);
    return shell.evaluate(body);
  }

  private GroovyClassLoader createGCL() {
    CompilerConfiguration cfg = new CompilerConfiguration();
    ClassLoader tccl = Thread.currentThread().getContextClassLoader();
    GroovyClassLoader gcl = new GroovyClassLoader(tccl, cfg);
    return gcl;
  }

}
