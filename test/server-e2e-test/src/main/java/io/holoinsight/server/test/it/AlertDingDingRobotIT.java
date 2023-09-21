/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.AlarmDingDingRobotDTO;
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

/**
 * <p>
 * </p>
 * <p>
 * Date: 2023-03-27 Time: 18:15
 * </p>
 *
 * @author jsy1001de
 */
public class AlertDingDingRobotIT extends BaseIT {
  String name;
  Long id;
  String tenant;

  Supplier<Response> queryById = () -> given() //
      .pathParam("id", id) //
      .when() //
      .get("/webapi/alarmDingDingRobot/query/{id}"); //

  @Order(1)
  @Test
  public void test_rule_create() {
    name = RandomStringUtils.randomAlphabetic(10) + "dingding robot测试";
    AlarmDingDingRobotDTO item = new AlarmDingDingRobotDTO();
    item.setGroupName(name);
    item.setRobotUrl("https://oapi.dingtalk.com/robot/send?access_token=xxxxxxxxx");

    // Create folder
    id = ((Number) given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/alarmDingDingRobot/create") //
        .then() //
        .body("success", IS_TRUE) //
        .extract() //
        .path("data")).longValue();
    System.out.println(id);
    Response response = queryById.get();
    System.out.println(response.body().print());
    tenant = response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("groupName", eq(name)) //
        .extract() //
        .path("data.tenant");
    System.out.println(tenant);
  }

  @Order(2)
  @Test
  public void test_rule_update() {
    name = name + "_v2";
    AlarmDingDingRobotDTO item = new AlarmDingDingRobotDTO();
    item.setId(id);
    item.setGroupName(name);
    item.setRobotUrl("https://oapi.dingtalk.com/robot/send?access_token=xxxxxxxxx");
    item.setTenant(tenant);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(item)))) //
        .when() //
        .post("/webapi/alarmDingDingRobot/update") //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_TRUE); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .root("data").body("groupName", eq(name));
  }

  @Order(3)
  @Test
  public void test_rule_delete() {
    given() //
        .pathParam("id", id) //
        .when() //
        .delete("/webapi/alarmDingDingRobot/delete/{id}").then() //
        .body("success", IS_TRUE); //
    Response response = queryById.get();
    System.out.println(response.body().print());
    response //
        .then() //
        .body("success", IS_TRUE) //
        .body("data", IS_NULL); //
  }

  @Order(4)
  @Test
  public void test_rule_pageQuery() {
    Stack<Long> ids = new Stack<>();
    for (int i = 0; i < 10; i++) {
      Long id = ((Number) given() //
          .body(new JSONObject(J.toMap(J.toJson(buildItem("hit_title_" + i))))) //
          .when() //
          .post("/webapi/alarmDingDingRobot/create") //
          .then() //
          .body("success", IS_TRUE) //
          .extract() //
          .path("data")).longValue(); //
      ids.push(id);
    }
    for (int i = 0; i < 10; i++) {
      given() //
          .body(new JSONObject(J.toMap(J.toJson(buildItem("miss_title_" + i))))) //
          .when() //
          .post("/webapi/alarmDingDingRobot/create") //
          .then() //
          .body("success", IS_TRUE);
    }
    AlarmDingDingRobotDTO condition = new AlarmDingDingRobotDTO();
    condition.setGroupName("hit_title");
    MonitorPageRequest<AlarmDingDingRobotDTO> pageRequest = new MonitorPageRequest<>();
    pageRequest.setTarget(condition);
    pageRequest.setPageNum(0);
    pageRequest.setPageSize(3);
    given() //
        .body(new JSONObject(J.toMap(J.toJson(pageRequest)))) //
        .when() //
        .post("/webapi/alarmDingDingRobot/pageQuery") //
        .then() //
        .body("success", IS_TRUE) //
        .root("data")
        .body("items", new Every<>(new CustomMatcher<AlarmDingDingRobotDTO>("page query id equal") {
          @Override
          public boolean matches(Object o) {
            Map<String, Object> item = (Map<String, Object>) o;
            Long queryId = ((Number) item.get("id")).longValue();
            Long id = ids.pop().longValue();
            return queryId.equals(id);
          }
        }));
  }

  private AlarmDingDingRobotDTO buildItem(String title) {
    AlarmDingDingRobotDTO item = new AlarmDingDingRobotDTO();
    item.setGroupName(title);
    item.setRobotUrl("https://oapi.dingtalk.com/robot/send?access_token=xxxxxxxxx");

    return item;
  }
}
