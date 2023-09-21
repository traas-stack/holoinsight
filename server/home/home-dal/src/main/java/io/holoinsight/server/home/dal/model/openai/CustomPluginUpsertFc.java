/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.openai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.dto.CustomPluginPeriodType;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-06-20 14:07:00
 */
@Data
public class CustomPluginUpsertFc {

  @JsonPropertyDescription("Name of log monitoring")
  private String name;

  @JsonPropertyDescription("The file path of the monitored log")
  private String logPath;

  @JsonPropertyDescription("Keywords included in the log")
  private List<String> keywords;

  @JsonPropertyDescription("The application(s) from which the logs need to be monitored")
  private String appName;

  @JsonPropertyDescription("Monitoring metric, for example: \"k8s_cpu_util\"")
  private String metric;

  @JsonPropertyDescription("Log collection cycle, for example: SECOND means collecting logs once per second, FIVE_SECOND means collecting logs once every five seconds, MINUTE means collecting logs once per minute, HOUR means collecting logs once per hour.")
  private CustomPluginPeriodType periodType;

  public static String getJsonSchema() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
      JsonSchema schemaNode = schemaGen.generateSchema(CustomPluginUpsertFc.class);
      String str = mapper.writeValueAsString(schemaNode);
      Map<String, Object> map = J.toMap(str);
      map.put("required", Arrays.asList("name", "logPath", "appName"));
      return J.toJson(map);
    } catch (Exception e) {
      return "{}";
    }
  }
}
