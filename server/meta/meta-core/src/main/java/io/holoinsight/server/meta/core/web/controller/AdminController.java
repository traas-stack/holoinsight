/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.web.controller;

import io.holoinsight.server.meta.common.model.QueryExample;
import io.holoinsight.server.meta.core.service.DBCoreService;
import io.holoinsight.server.common.JsonResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  @Qualifier("mongoDataCoreService")
  private DBCoreService mongoDataCoreService;

  @Autowired
  @Qualifier("sqlDataCoreService")
  private DBCoreService sqlDataCoreService;

  @Value("${holoinsight.meta.readMysql.enabled:false}")
  private boolean readMysqlEnable;

  @PostMapping("/mongodb/query/{collection}")
  public JsonResult<Object> query(@PathVariable("collection") String collection,
      @RequestBody Map<String, Object> condition) {
    QueryExample queryExample = new QueryExample();
    queryExample.getParams().putAll(condition);
    return JsonResult
        .createSuccessResult(getDbCoreService().queryByExample(collection, queryExample));
  }

  private DBCoreService getDbCoreService() {
    DBCoreService coreService;
    if (readMysqlEnable) {
      coreService = sqlDataCoreService;
    } else {
      coreService = mongoDataCoreService;
    }
    return coreService;
  }

}
