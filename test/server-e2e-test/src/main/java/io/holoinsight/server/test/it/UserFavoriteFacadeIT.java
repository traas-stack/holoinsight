/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.Folder;
import io.holoinsight.server.home.dal.model.UserFavorite;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.CustomMatcher;
import org.hamcrest.core.Every;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Stack;
import java.util.function.Supplier;

public class UserFavoriteFacadeIT extends BaseIT {
  Long id;
  String relateId;
  String type;
  String name;
  String url;


  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/userFavorite/query/{id}"); //

  Supplier<Response> queryByFolderId = () -> given() //
      .pathParam("id", Long.valueOf(relateId)) //
      .when() //
      .get("/webapi/folder/query/{id}"); //

  Supplier<Response> queryByRelateId = () -> given() //
      .pathParam("type", type) //
      .pathParam("relateId", relateId) //
      .when() //
      .get("/webapi/userFavorite/queryByRelateId/{type}/{relateId}"); //

  @Order(1)
  @Test
  public void test_user_favorite_create() {
    String folderName = RandomStringUtils.randomAlphabetic(10) + "_folder测试";
    Folder item = new Folder();
    Long parentFolderId = Long.valueOf(1);
    item.setName(folderName);
    item.setParentFolderId(parentFolderId);


    // Create folder
    relateId = (given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/folder/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data.id").toString());
    System.out.println(relateId);

    UserFavorite userFavorite = new UserFavorite();
    type = "folder";
    name = RandomStringUtils.randomAlphabetic(10) + "_userFavoriteTest";
    url = "alibaba.com";
    userFavorite.setRelateId(relateId);
    userFavorite.setType(type);
    userFavorite.setName(name);
    userFavorite.setUrl(url);
    // Create folder
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(userFavorite)))) //
        .when() //
        .post("/webapi/userFavorite/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data.id")).longValue();
    System.out.println(id);

    Response response = queryById.get();
    System.out.println(response.body().print());

    Response response1 = queryByRelateId.get();
    System.out.println(response1.body().print());
    response1 //
        .then() //
        .body("success", IS_TRUE) //
        .body("data.relateId", eq(relateId));

  }

  @Order(2)
  @Test
  public void test_user_favorite_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/userFavorite/delete/{id}").then() //
        .body("success", IS_TRUE).body("data", IS_TRUE); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_FALSE) //
        .body("data", IS_NULL); //

    test_user_favorite_create();
    given() //
        .pathParam("type", type) //
        .pathParam("relateId", relateId) //
        .when() //
        .delete("/webapi/userFavorite/deleteByRelateId/{type}/{relateId}").then() //
        .body("success", IS_TRUE).body("data", IS_TRUE); //
    Response response1 = queryByRelateId.get();
    System.out.println(response1.body().print());
    response1 //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_NULL); //

  }

  @Order(3)
  @Test
  public void test_user_favorite_pageQuery() {
    Stack<Long> ids = new Stack<>();
    UserFavorite item = new UserFavorite();
    item.setName(name);
    item.setType(type);
    item.setUrl(url);
    for (int i = 0; i < 5; i++) {
      String folderName = RandomStringUtils.randomAlphabetic(10) + "_folder测试";
      Folder folder = new Folder();
      Long parentFolderId = Long.valueOf(1);
      folder.setName(folderName);
      folder.setParentFolderId(parentFolderId);

      // Create folder
      relateId = (given() //
          .body(new JSONObject(J.toMap(J.toJson(folder)))) //
          .when() //
          .post("/webapi/folder/create") //
          .then() //
          .body("success", IS_TRUE) //
          .extract() //
          .path("data.id").toString());

      item.setRelateId(relateId);
      Long id = ((Number) given() //
          .body(new JSONObject(J.toMap(J.toJson(item)))) //
          .when() //
          .post("/webapi/userFavorite/create") //
          .then() //
          .body("success", IS_TRUE) //
          .extract() //
          .path("data.id")).longValue(); //
      ids.push(id);
      given() //
          .pathParam("id", Long.valueOf(relateId)) //
          .when() //
          .delete("/webapi/folder/delete/{id}").then() //
          .body("success", IS_TRUE); //
      Response response = queryByFolderId.get();
      response //
          .then() //
          .body("success", IS_FALSE) //
          .body("data", IS_NULL); //
    }

    UserFavorite condition = new UserFavorite();
    condition.setType(url);
    MonitorPageRequest<UserFavorite> pageRequest = new MonitorPageRequest<>();
    pageRequest.setTarget(condition);
    pageRequest.setPageNum(0);
    pageRequest.setPageSize(3);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
        .when() //
        .post("/webapi/userFavorite/pageQuery") //
        .then() //
        .body("success", IS_TRUE) //
        .root("data")
        .body("items", new Every<>(new CustomMatcher<UserFavorite>("page query id equal") {
          @Override
          public boolean matches(Object o) {
            Map<String, Object> item = (Map<String, Object>) o;
            Long queryId = ((Number) item.get("id")).longValue();
            Long id = ids.pop().longValue();
            return queryId.equals(id);
          }
        }));

  }
}
