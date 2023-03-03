/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonSubTypes({@JsonSubTypes.Type(value = Node.class, name = "node"),
    @JsonSubTypes.Type(value = Node.EndpointNode.class, name = "endpointNode"),
    @JsonSubTypes.Type(value = Node.ServiceInstanceNode.class, name = "serviceInstanceNode"),})
public interface NodeType {
}
