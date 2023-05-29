/** Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved. */
package io.holoinsight.server.apm.engine.tatris.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xiangwanpeng
 * @version : Mappings.java, v 0.1 2023年02月24日 19:48 xiangwanpeng Exp $
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Mappings {
    private String dynamic;
    private List<Map<String, DynamicTemplate>> dynamicTemplates;
    private Map<String, Property> properties;

    public boolean containsAllFields(Mappings mappings) {
        if (this.properties.size() < mappings.getProperties().size()) {
            return false;
        }
        return mappings.getProperties().entrySet().stream()
                .allMatch(item -> Objects.equals(properties.get(item.getKey()), item.getValue()));
    }

    /**
     * Append new fields and update. Properties combine input and exist, update property's attribute, won't remove old
     * one. Source will be updated to the input.
     */
    public void appendNewFields(Mappings mappings) {
        properties.putAll(mappings.getProperties());
    }

    /** Returns the properties that not exist in the input fields. */
    public Map<String, Property> diffFields(Mappings mappings) {
        return this.properties.entrySet().stream().filter(e -> !mappings.getProperties().containsKey(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
