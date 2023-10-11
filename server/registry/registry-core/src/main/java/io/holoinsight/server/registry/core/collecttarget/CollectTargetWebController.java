/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.collecttarget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.facade.service.DataClientService;
import io.holoinsight.server.registry.core.template.CollectTemplate;
import io.holoinsight.server.registry.core.template.TemplateService;
import io.holoinsight.server.registry.core.utils.ApiResp;

/**
 * <p>
 * created at 2022/2/28
 *
 * @author zzhb101
 */
@RestController
@RequestMapping("/internal/api/registry/target")
public class CollectTargetWebController {
  @Autowired
  private CollectTargetStorage collectTargetStorage;
  @Autowired
  private TemplateService templateService;
  @Autowired
  private TargetDebugService targetDebugService;
  @Autowired
  private DataClientService dataClientService;

  @GetMapping("/countByEveryAgent")
  public Object countByEveryAgent() {
    return collectTargetStorage.countByEveryAgent();
  }

  @GetMapping("/list")
  public Object list() {
    return collectTargetStorage.list();
  }

  @GetMapping("/listDimsByTemplate")
  public Object listDimsByTemplate(@RequestParam("t") String t) {
    CollectTemplate ct = templateService.fuzzyGet(t);
    if (ct == null) {
      return ApiResp.error("template not found");
    }
    List<CollectTarget> targets = collectTargetStorage.getByTemplateId(ct.getId());
    List<Object> dims = new ArrayList<>(targets.size());
    List<String> uks = new ArrayList<>(targets.size());
    for (CollectTarget target : targets) {
      String dimId = target.getDimId();
      if (dimId.startsWith("dim2:")) {
        uks.add(dimId.substring("dim2:".length()));
      } else {
        HashMap<Object, Object> hash = new HashMap<>();
        hash.put("dimId", dimId);
        dims.add(hash);
      }
    }
    if (uks.size() > 0) {
      QueryExample qe = new QueryExample();
      qe.getParams().put("_uk", uks);
      dims.addAll(
          dataClientService.queryByExample(ct.getCollectRange().getCloudmonitor().getTable(), qe));
    }
    return ApiResp.resource(dims);
  }

  @GetMapping("/listKeysByTemplate")
  public Object listKeysByTemplate(@RequestParam("t") String t) {
    CollectTemplate ct = templateService.fuzzyGet(t);
    if (ct == null) {
      return ApiResp.error("template not found");
    }
    return ApiResp.resource(collectTargetStorage.getByTemplateId(ct.getId()));
  }

  @GetMapping("/listByAgent")
  public Object listByAgent(@RequestParam("a") String a) {
    Map<String, Object> map = new LinkedHashMap<>();
    List<CollectTarget> list = collectTargetStorage.getByAgent(a);
    if (list != null) {
      map.put("targets", list);
      map.put("size", list.size());
    }
    return map;
  }

  @GetMapping("/countByAgent")
  public Object countByAgent(@RequestParam("a") String a) {
    return collectTargetStorage.countByAgent(a);
  }

  @GetMapping("/countByTemplate")
  public ApiResp countByTemplate(@RequestParam("t") String t) {
    CollectTemplate ct = templateService.fuzzyGet(t);
    if (ct == null) {
      return ApiResp.error("template not found");
    }
    return ApiResp.success(collectTargetStorage.countByTemplate(ct.getId()));
  }

  @GetMapping("/get")
  public Object get(@RequestParam("t") String t, @RequestParam("d") String d) {
    Object debug = targetDebugService.get(t, d);
    if (debug == null) {
      return ApiResp.error("not found");
    }
    return debug;
  }
}
