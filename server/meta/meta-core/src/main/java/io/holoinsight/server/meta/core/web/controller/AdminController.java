/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.core.web.controller;

import io.holoinsight.server.meta.dal.service.MongoDbHelper;
import io.holoinsight.server.common.JsonResult;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * @author jsy1001de
 * @version : TestController.java, v 0.1 2022年03月03日 5:07 下午 jinsong.yjs Exp $
 */
@RestController
@RequestMapping("/webapi/admin")
public class AdminController {

  @Autowired
  private MongoDbHelper mongoDbHelper;


  @RequestMapping("/mongodb/query/{collection}")
  public JsonResult<Object> query(@PathVariable("collection") String collection) {

    List<Document> all = mongoDbHelper.findAll(Document.class, collection);
    return JsonResult.createSuccessResult(all);
  }

}
