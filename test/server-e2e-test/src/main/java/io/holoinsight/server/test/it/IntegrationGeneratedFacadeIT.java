/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.test.it;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.IntegrationGeneratedDTO;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class IntegrationGeneratedFacadeIT extends BaseIT {
  // Long id;
  // String name;
  // String item;
  //
  // Supplier<Response> queryByName = () -> given() //
  // .pathParam("name", name) //
  // .when() //
  // .get("/webapi/integrationGenerated/query/{name}"); //

  @Order(1)
  @Test
  public void test_integration_generate_update() {
    // IntegrationGeneratedDTO generatedDTO = new IntegrationGeneratedDTO();
    // id = Long.valueOf(1);
    // name = "demo-server";
    // item = "logpattern";
    // generatedDTO.setId(id);
    // generatedDTO.setName(name);
    // generatedDTO.setItem(item);
    // generatedDTO.setProduct("LogPattern");
    // generatedDTO.setCustom(false);
    // Map<String, Object> config = new HashMap<>();
    // generatedDTO.setConfig(config);
    //
    // // update
    // given() //
    // .body(new JSONObject(J.toMap(J.toJson(generatedDTO)))) //
    // .when() //
    // .post("/webapi/integrationGenerated/update") //
    // .then() //
    // .body("success", IS_TRUE) //
    // .body("data", IS_TRUE); //
    //
    // Response response = queryByName.get();
    // response //
    // .then() //
    // .body("success", IS_TRUE) //
    // .rootPath("data.find { it.id == %d }", withArgs(id.intValue())) //
    // .body("item", eq(item));
  }

}
