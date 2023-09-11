/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.event;

import lombok.Data;

import java.util.Map;

@Data
public class Event {
  private String tenant;
  private String id;
  private String name;
  private String type;
  private long timestamp;
  private Map<String, String> tags;
}
