/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.engine.elasticsearch.installer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jiwliu
 * @version : Mappings.java, v 0.1 2022年10月12日 10:21 xiangwanpeng Exp $
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mappings {

  private List<Map<String, DynamicTemplate>> dynamicTemplates;

  private Map<String, Object> properties = new HashMap<>();

  public boolean containsAllFields(Mappings mappings) {
    if (this.properties.size() < mappings.getProperties().size()) {
      return false;
    }
    return mappings.getProperties().entrySet().stream()
        .allMatch(item -> Objects.equals(properties.get(item.getKey()), item.getValue()));
  }

  /**
   * Append new fields and update. Properties combine input and exist, update property's attribute,
   * won't remove old one. Source will be updated to the input.
   */
  public void appendNewFields(Mappings mappings) {
    properties.putAll(mappings.getProperties());
  }

  /**
   * Returns the properties that not exist in the input fields.
   */
  public Map<String, Object> diffFields(Mappings mappings) {
    return this.properties.entrySet().stream()
        .filter(e -> !mappings.getProperties().containsKey(e.getKey()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

}
