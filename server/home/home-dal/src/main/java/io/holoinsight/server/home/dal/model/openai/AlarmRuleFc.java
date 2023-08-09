/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.openai;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.trigger.CompareConfig;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-06-20 11:16:00
 */
@Data
public class AlarmRuleFc {

  @JsonPropertyDescription("The name of alarm rule")
  private String ruleName;

  @JsonPropertyDescription("The type of alarm rule, for example: \"ai\" represents intelligent alarm, \"rule\" represents rule alarm, \"pql\" represents PQL alarm, and the default is \"rule\"")
  private AlertRuleType ruleType;
  @JsonPropertyDescription("Alarm rule description information")
  private String ruleDescribe;
  @JsonPropertyDescription("Monitoring metric, for example: \"k8s_cpu_util\"")
  private String metric;

  @JsonPropertyDescription("List of alarm trigger thresholds, which can contain multiple sets of alarm trigger thresholds")
  private List<CompareConfig> compareConfigs;

  public static String getJsonSchema() {
    try {
      ObjectMapper mapper = new ObjectMapper();
      JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(mapper);
      JsonSchema schemaNode = schemaGen.generateSchema(AlarmRuleFc.class);
      String str = mapper.writeValueAsString(schemaNode);
      Map<String, Object> map = J.toMap(str);
      map.put("required", Arrays.asList("ruleName", "compareConfigs"));
      return J.toJson(map);
    } catch (Exception e) {
      return "{}";
    }
  }
}
