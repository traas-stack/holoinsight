/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.common.model.query;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Topology {
  private final List<Node> nodes;
  private final List<Call> calls;

  public Topology() {
    this.nodes = new ArrayList<>();
    this.calls = new ArrayList<>();
  }
}
