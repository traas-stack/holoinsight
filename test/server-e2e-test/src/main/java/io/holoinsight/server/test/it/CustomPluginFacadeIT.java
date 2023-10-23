/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.CustomPluginDTO;
import io.holoinsight.server.home.dal.model.dto.CustomPluginPeriodType;
import io.holoinsight.server.home.dal.model.dto.CustomPluginStatus;
import io.holoinsight.server.home.dal.model.dto.conf.CustomPluginConf;
import io.holoinsight.server.home.dal.model.dto.conf.LogParse;
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

public class CustomPluginFacadeIT extends BaseIT {
  String name;
  Long id;
  Long parentFolderId;
  String tenant;
  String creator;

  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/customPlugin/query/{id}"); //

  Supplier<Response> queryByNameLikeAndTenant = () -> given() //
      .pathParam("name", name) //
      .when() //
      .get("/webapi/customPlugin/queryByNameLikeAndTenant/{name}"); //

  Supplier<Response> queryByCreatorAndTenant = () -> given() //
      .pathParam("creator", creator) //
      .when() //
      .get("/webapi/customPlugin/queryByCreatorAndTenant/{creator}"); //

  @Order(1)
  @Test
  public void test_custom_plugin_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "_customPlugin测试";
    CustomPluginDTO item = new CustomPluginDTO();
    CustomPluginConf customPluginConf = new CustomPluginConf();
    customPluginConf.logParse = new LogParse();
    parentFolderId = Long.valueOf(-1);
    item.setName(name);
    item.setParentFolderId(parentFolderId);
    item.setPluginType("custom");
    item.setStatus(CustomPluginStatus.ONLINE);
    item.setPeriodType(CustomPluginPeriodType.MINUTE);
    item.setConf(customPluginConf);


    // Create custom_plugin
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/customPlugin/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data.id")).longValue();
    System.out.println(id);
    Response response = queryById.get();
    creator = response.path("data.creator");
    System.out.println(response.body().print());
    tenant = response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("name", eq(name)) //
        .extract() //
        .path("data.tenant");
    System.out.println(tenant);

  }

  @Order(2)
  @Test
  public void test_custom_plugin_update() {
    name = name + "_v02";
    CustomPluginDTO item = new CustomPluginDTO();
    item.setId(id);
    CustomPluginConf customPluginConf = new CustomPluginConf();
    customPluginConf.logParse = new LogParse();
    parentFolderId = Long.valueOf(-1);
    item.setName(name);
    item.setParentFolderId(parentFolderId);
    item.setPluginType("custom");
    item.setStatus(CustomPluginStatus.OFFLINE);
    item.setPeriodType(CustomPluginPeriodType.MINUTE);
    item.setConf(customPluginConf);
    item.setTenant(tenant);
    // update
    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/customPlugin/update") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", NOT_NULL); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("name", eq(name));
  }

  // @Order(3)
  // @Test
  // public void test_custom_plugin_updateParentFolderId() {
  // CustomPluginDTO item = new CustomPluginDTO();
  // item.setId(id);
  // parentFolderId = Long.valueOf(-1);
  // item.setParentFolderId(parentFolderId);
  // given() //
  // .body(new JSONObject(J.toMap(J.toJson(item)))) //
  // .when() //
  // .post("/webapi/customPlugin/updateParentFolderId") //
  // .then() //
  // .body("success", IS_TRUE) //
  // .body("data", IS_TRUE); //
  // Response response = queryById.get();
  // System.out.println(response.body().print());
  // response //
  // .then() //
  // .body("success", IS_TRUE) //
  // .root("data").body("parentFolderId", eq(parentFolderId.intValue()));
  //
  //
  // }

  @Order(4)
  @Test
  public void test_custom_plugin_queryByT() {
    Response res1 = queryByNameLikeAndTenant.get();
    Response res2 = queryByCreatorAndTenant.get();

    res1 //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data.find { it.name == '%s' }", withArgs(name)) //
        .body(NOT_NULL); //

    res2 //
        .then() //
        .body("success", IS_TRUE) //
        .rootPath("data.find { it.name == '%s' }", withArgs(name)) //
        .body("creator", eq(creator));
  }

  @Order(5)
  @Test
  public void test_custom_plugin_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/customPlugin/delete/{id}").then() //
        .body("success", IS_TRUE).body("data", IS_NULL); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_FALSE) //
        .body("data", IS_NULL); //
  }

  @Order(6)
  @Test
  public void test_custom_plugin_pageQuery() {
    Stack<Long> ids = new Stack<>();
    CustomPluginDTO item = new CustomPluginDTO();
    CustomPluginConf customPluginConf = new CustomPluginConf();
    customPluginConf.logParse = new LogParse();
    item.setName(name);
    item.setParentFolderId(parentFolderId);
    item.setPluginType("custom");
    item.setStatus(CustomPluginStatus.ONLINE);
    item.setPeriodType(CustomPluginPeriodType.MINUTE);
    item.setConf(customPluginConf);
    for (int i = 0; i < 10; i++) {
      Long id = ((Number) given() //
          .body(new JSONObject(J.toMap(J.toJson(item)))) //
          .when() //
          .post("/webapi/customPlugin/create") //
          .then() //
          .body("success", IS_TRUE) //
          .extract() //
          .path("data.id")).longValue(); //
      ids.push(id);
    }

    CustomPluginDTO condition = new CustomPluginDTO();
    condition.setStatus(CustomPluginStatus.ONLINE);
    MonitorPageRequest<CustomPluginDTO> pageRequest = new MonitorPageRequest<>();
    pageRequest.setTarget(condition);
    pageRequest.setPageNum(0);
    pageRequest.setPageSize(3);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
        .when() //
        .post("/webapi/customPlugin/pageQuery") //
        .then() //
        .body("success", IS_TRUE) //
        .root("data")
        .body("items", new Every<>(new CustomMatcher<CustomPluginDTO>("page query id equal") {
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
