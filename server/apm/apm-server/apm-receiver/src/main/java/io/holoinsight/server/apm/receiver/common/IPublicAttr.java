/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.common;

import io.holoinsight.server.apm.receiver.builder.RPCTrafficSourceBuilder;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.Map;

public interface IPublicAttr {

  RPCTrafficSourceBuilder setPublicAttrs(RPCTrafficSourceBuilder sourceBuilder, Span span,
      Map<String, String> spanAttrMap, Map<String, String> resourceAttrMap);
}
