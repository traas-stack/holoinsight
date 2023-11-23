/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.task.coordinator;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-09-28 11:37:00
 */
@Data
public class OrderConfig {
  private Map<String, String> orderedMap = new HashMap<>();
  private boolean enable;
}
