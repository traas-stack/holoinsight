/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.openai;

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

  @JsonPropertyDescription("告警规则名称")
  private String ruleName;

  @JsonPropertyDescription("告警规则类型，例如：ai代表智能告警、rule代表规则告警、pql代表pql告警，默认为 rule")
  private AlertRuleType ruleType;
  @JsonPropertyDescription("告警规则描述信息")
  private String ruleDescribe;
  @JsonPropertyDescription("监控指标 metric，例如: k8s_cpu_util")
  private String metric;

  @JsonPropertyDescription("告警触发阈值列表，可以包含多组告警触发阈值")
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
