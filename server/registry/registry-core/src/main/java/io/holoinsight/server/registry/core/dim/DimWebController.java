/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.dim;

import java.util.List;
import java.util.Map;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import io.holoinsight.server.registry.core.utils.ApiResp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * <p>
 * created at 2022/4/15
 *
 * @author zzhb101
 */
@RestController
@RequestMapping("/internal/api/registry/dim")
public class DimWebController {
  @Autowired
  private DataClientService dataClientService;

  @GetMapping("/queryAll")
  public Object queryAll(@RequestParam("tableName") String tableName) {
    return ApiResp.resource(dataClientService.queryAll(tableName));
  }

  @GetMapping("/queryById")
  public Object queryById(@RequestParam("tableName") String tableName,
      @RequestParam("id") String id) {
    QueryExample qe = new QueryExample();
    qe.getParams().put("_uk", id);
    List<Map<String, Object>> list = dataClientService.queryByExample(tableName, qe);
    if (list.size() == 1) {
      return list.get(0);
    }
    return list;
  }

  @GetMapping("/queryByIp")
  public Object queryByIp(@RequestParam("tableName") String tableName,
      @RequestParam("ip") String ip) {
    QueryExample qe = new QueryExample();
    qe.getParams().put("ip", ip);
    List<Map<String, Object>> list = dataClientService.queryByExample(tableName, qe);
    if (list.size() == 1) {
      return list.get(0);
    }
    return list;
  }
}
