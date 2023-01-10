/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.config;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.holoinsight.server.common.web.ApiResp;
import io.holoinsight.server.common.web.InternalWebApi;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.xzchaoo.commons.basic.config.spring.AbstractConfig;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author xzchaoo
 */
@ResponseBody
@RequestMapping("/internal/api/config")
@InternalWebApi
public class ConfigWebController {
  @Autowired(required = false)
  private List<AbstractConfig> children = new ArrayList<>();

  @Autowired
  private ConfigService configService;

  /**
   * 打印出自身动态配置
   *
   * @return
   */
  @GetMapping("/tree")
  public Object tree() {
    Map<String, AbstractConfig> m = new LinkedHashMap<>();
    for (AbstractConfig c : children) {
      m.put(c.getClass().getSimpleName(), c);
    }
    return m;
  }

  @GetMapping("/pset")
  public Object pset(@RequestParam("key") String key, @RequestParam("value") String value) {
    if (StringUtils.isAnyEmpty(key, value)) {
      throw new IllegalArgumentException("key or value is empty");
    }
    configService.set(key, value);
    return ApiResp.success();
  }

  @GetMapping("/pdel")
  public Object pdel(@RequestParam("key") String key) {
    configService.delete(key);
    return ApiResp.success();
  }

}
