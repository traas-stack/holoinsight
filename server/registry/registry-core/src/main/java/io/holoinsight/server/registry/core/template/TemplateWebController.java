/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.holoinsight.server.registry.core.utils.ApiResp;

/**
 * <p>
 * created at 2022/3/2
 *
 * @author zzhb101
 */
@RestController
@RequestMapping("/internal/api/registry/template")
public class TemplateWebController {
  @Autowired
  private CollectTemplateSyncer collectTemplateSyncer;
  @Autowired
  private TemplateStorage templateStorage;
  @Autowired
  private TemplateService templateService;

  /**
   * 立即触发所有templates的维护
   *
   * @return
   */
  @GetMapping("/maintainAll")
  public ApiResp maintainAll() {
    long begin = System.currentTimeMillis();
    int size = collectTemplateSyncer.maintainAll();
    long end = System.currentTimeMillis();
    Map<String, Object> m = new HashMap<>();
    m.put("size", size);
    m.put("cost", end - begin);
    return ApiResp.success(m);
  }

  /**
   * 立即触发一次fullSync
   *
   * @return
   */
  @GetMapping("/fullSync")
  public ApiResp fullSync() throws Exception {
    collectTemplateSyncer.publicFullSync();
    return ApiResp.success();
  }

  @GetMapping("/list")
  public ApiResp list() {
    Map<String, List<String>> m = new HashMap<>();
    for (CollectTemplate t : templateStorage.readonlyLoop()) {
      m.computeIfAbsent(t.getTenant(), i -> new ArrayList<>()) //
          .add(String.format("%d/%s", t.getId(), t.getTableName()));
    }
    return ApiResp.success(m);
  }

  @GetMapping("/get")
  public ApiResp get(@RequestParam("t") String t) {
    return ApiResp.resource(templateService.fuzzyGet(t));
  }

}
