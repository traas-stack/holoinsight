/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.receiver.builder;

import io.holoinsight.server.apm.common.model.specification.sw.DetectPoint;
import io.holoinsight.server.apm.common.model.specification.sw.Layer;
import io.opentelemetry.proto.common.v1.KeyValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndpointSourceBuilder {

  protected String traceId;
  protected String tenant;
  protected long timeBucket;
  protected long startTime;
  protected long endTime;
  protected String destServiceName;
  protected Layer destLayer;
  protected String destServiceInstanceName;
  protected String destEndpointName;
  protected int latency;
  protected int traceStatus;
  protected String httpResponseStatusCode;
  protected String rpcStatusCode;
  protected String type;
  protected DetectPoint detectPoint;
  protected final List<String> tags = new ArrayList<>();
  protected final Map<String, String> originalTags = new HashMap<>();

  public void setTag(KeyValue tag) {
    tags.add(tag.getKey().trim() + ":" + tag.getValue().getStringValue().trim());
    originalTags.put(tag.getKey(), tag.getValue().toString());
  }

}
