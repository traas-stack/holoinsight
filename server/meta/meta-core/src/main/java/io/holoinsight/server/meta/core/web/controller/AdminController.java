/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.web.controller;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.meta.core.service.DBCoreServiceSwitcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version : TestController.java, v 0.1 2022年03月03日 5:07 下午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/internal/api/meta")
public class AdminController {

  @Autowired
  private DBCoreServiceSwitcher dbCoreServiceSwitcher;


  @PostMapping("/meta/query/{collection}")
  public JsonResult<Object> query(@PathVariable("collection") String collection,
      @RequestBody Map<String, Object> condition) {
    QueryExample queryExample = new QueryExample();
    queryExample.getParams().putAll(condition);
    if (condition.containsKey("rowKeys")) {
      Object rowKeys = condition.remove("rowKeys");
      queryExample.setRowKeys((List) rowKeys);
    }
    return JsonResult.createSuccessResult(
        dbCoreServiceSwitcher.dbCoreService().queryByExample(collection, queryExample));
  }

}
