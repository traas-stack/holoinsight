/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.Folder;
import io.holoinsight.server.home.web.controller.model.FolderRequest;
import io.holoinsight.server.home.web.controller.model.FolderRequestCmd;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.CustomMatcher;
import org.hamcrest.core.Every;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class FolderFacadeIT extends BaseIT {
  String name;
  Long id;
  Long parentFolderId;
  String tenant;

  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/folder/query/{id}"); //

  Supplier<Response> queryByParentFolderId = () -> given() //
      .pathParam("parentFolderId", parentFolderId) //
      .when() //
      .get("/webapi/folder/queryByParentFolderId/{parentFolderId}"); //

  @Order(1)
  @Test
  public void test_folder_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "_folder测试";
    Folder item = new Folder();
    parentFolderId = Long.valueOf(1);
    item.setName(name);
    item.setParentFolderId(parentFolderId);


    // Create folder
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/folder/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data.id")).longValue();
    System.out.println(id);
    Response response = queryById.get();
    System.out.println(response.body().print());
    tenant = response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("name", eq(name)) //
        .extract() //
        .path("data.tenant");
    System.out.println(tenant);

    Response res = queryByParentFolderId.get();
    System.out.println(res.body().print());
    res //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data.find { it.name == '%s' }", withArgs(name)) //
        .body(NOT_NULL) //
        .body("parentFolderId", eq(parentFolderId.intValue())); //


  }

  @Order(2)
  @Test
  public void test_folder_update() {
    name = name + "_v02";
    Folder item = new Folder();
    parentFolderId = Long.valueOf(2);
    item.setId(id);
    item.setName(name);
    item.setTenant(tenant);
    item.setParentFolderId(parentFolderId);
    // update
    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/folder/update") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_TRUE); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("name", eq(name)).body("parentFolderId", eq(parentFolderId.intValue()));
  }

  @Order(3)
  @Test
  public void test_folder_query_and_updateParentFolderId() {
    // update
    Folder item = new Folder();
    item.setId(id);
    parentFolderId = Long.valueOf(1);
    item.setParentFolderId(parentFolderId);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/folder/updateParentFolderId") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_TRUE); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("parentFolderId", eq(parentFolderId.intValue()));

  }

  @Order(4)
  @Test
  public void test_folder_path() {
    List<FolderRequestCmd> folderRequestCmdList = new ArrayList<>();
    FolderRequestCmd folderRequestCmd = new FolderRequestCmd();
    folderRequestCmd.folderId = id;
    folderRequestCmd.customPluginId = Long.valueOf(-1);
    folderRequestCmdList.add(folderRequestCmd);
    FolderRequest item = new FolderRequest();
    item.requests = folderRequestCmdList;
    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/folder/path") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", NOT_NULL); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", NOT_NULL); //
  }

  @Order(5)
  @Test
  public void test_folder_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/folder/delete/{id}").then() //
        .body("success", IS_TRUE); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_FALSE) //
        .body("data", IS_NULL); //
  }


}
