/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import java.util.function.Supplier;

import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import io.restassured.response.Response;

/**
 * <p>
 * Log monitoring folder lifecycle integration test.
 * <p>
 * created at 2023/3/9
 *
 * @author xzchaoo
 */
public class LogMonitoringFolderIT extends BaseIT {
  String name;
  Integer id;
  String tenant;

  Supplier<Response> queryRootFolder = () -> given() //
      .pathParam("parentFolderId", -1) //
      .when() //
      .get("/webapi/folder/queryByParentFolderId/{parentFolderId}"); //

  @Order(1)
  @Test
  public void test_folder_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "中文测试";

    // Create folder
    id = given() //
        .body(new JSONObject().put("name", name).put("parentFolderId", -1)) //
        .when() //
        .post("/webapi/folder/create") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data.name", eq(name)) //
        .extract() //
        .path("data.id"); //

    // Query folders. The result must contain the folder created just before.
    tenant = queryRootFolder.get() //
        .then() //
        .body("success", IS_TRUE) //
        .body("data.find { it.name == '%s' }", withArgs(name), NOT_NULL) //
        .extract() //
        .path("data.find { it.name == '%s' }.tenant", name);
  }


  @Order(2)
  @Test
  public void test_folder_userFavorite_manage() {

    // Test favorite
    given() //
        .body(json() //
            .put("name", name) //
            .put("relateId", id) //
            .put("type", "folder") //
            .put("url", "/log/" + id)) //
        .when() //
        .post("/webapi/userFavorite/create") //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data") //
        .body("relateId", eq(id.toString())) //
        .body("type", eq("folder")) //
        .body("url", eq("/log/" + id)) //
    ; //

    // http://xf:8080/webapi/userFavorite/queryAll?ctoken=undefined
    // Query favorite
    given() //
        .when() //
        .get("/webapi/userFavorite/queryAll") //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data.find { it.name == '%s' }", withArgs(name)) //
        .body(NOT_NULL) //
        .body("relateId", eq(id.toString())) //
        .body("type", eq("folder")) //
        .body("url", eq("/log/" + id)); //

    // Delete favorite
    given() //
        // .body()
        .pathParam("relateId", id) //
        .when() //
        .delete("/webapi/userFavorite/deleteByRelateId/folder/{relateId}") //
        .then() //
        .body("success", IS_TRUE); //

    // Query favorite
    given() //
        .when() //
        .get("/webapi/userFavorite/queryAll") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data.find { it.name == '%s' }", withArgs(name), IS_NULL) //
    ; //
  }

  @Order(3)
  @Test
  public void test_folder_rename() {
    // Rename folder
    given()
        .body(new JSONObject().put("id", id).put("name", name + "X").put("parentFolderId", -1)
            .put("tenant", tenant)) //
        .when() //
        .post("/webapi/folder/update") //
        .then() //
        .body("success", IS_TRUE); //

    // Query folders. The result must contain the renamed folder.
    queryRootFolder.get() //
        .then() //
        .body("success", IS_TRUE) //
        .body("data.find { it.name == '%s' }", withArgs(name + "X"), NOT_NULL); //
  }

  @Order(4)
  @Test
  public void test_folder_delete() {
    // Delete Folder
    given().pathParam("id", id) //
        .when() //
        .delete("/webapi/folder/delete/{id}") //
        .then() //
        .body("success", IS_TRUE); //

    // Query folders. The result must not contain the folder.
    queryRootFolder.get() //
        .then() //
        .body("success", IS_TRUE) //
        .body("data.find { it.name == '%s' }", withArgs(name), IS_NULL) //
        .body("data.find { it.name == '%s' }", withArgs(name + "X"), IS_NULL); //
  }

}
