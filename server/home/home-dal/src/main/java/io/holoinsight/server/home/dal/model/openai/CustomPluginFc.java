/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.openai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.holoinsight.server.common.J;
import lombok.Data;

import java.util.Arrays;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-06-20 14:07:00
 */
@Data
public class CustomPluginFc {

  @JsonPropertyDescription("Name of log monitoring")
  private String name;

  public static String getJsonSchema() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
      JsonSchema schemaNode = schemaGen.generateSchema(CustomPluginFc.class);
      String str = mapper.writeValueAsString(schemaNode);
      Map<String, Object> map = J.toMap(str);
      map.put("required", Arrays.asList("name"));
      return J.toJson(map);
    } catch (Exception e) {
      return "{}";
    }
  }
}
