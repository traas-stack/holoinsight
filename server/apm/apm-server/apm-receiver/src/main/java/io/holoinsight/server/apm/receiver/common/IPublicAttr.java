package io.holoinsight.server.apm.receiver.common;

import io.holoinsight.server.apm.receiver.builder.RelationBuilder;
import io.opentelemetry.proto.common.v1.AnyValue;
import io.opentelemetry.proto.trace.v1.Span;

import java.util.Map;

public interface IPublicAttr {

  void setPublicAttrs(RelationBuilder sourceBuilder, Span span, Map<String, AnyValue> spanAttrMap,
      Map<String, AnyValue> resourceAttrMap);
}
