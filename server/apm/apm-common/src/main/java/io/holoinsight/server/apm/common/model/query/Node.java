/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Node implements NodeType {
  private String id;
  private String name;
  private String type;
  private boolean isReal;
  private ResponseMetric metric;

  @Getter
  @Setter
  public static class EndpointNode extends Node implements NodeType {
    private String serviceName;
  }


  @Getter
  @Setter
  public static class ServiceInstanceNode extends Node implements NodeType {
    private String serviceName;
  }
}
