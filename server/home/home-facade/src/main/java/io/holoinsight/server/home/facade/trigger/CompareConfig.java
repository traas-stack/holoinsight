/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

import lombok.Data;

import java.util.List;

/**
 * @author masaimu
 * @version 2023-02-21 16:22:00
 */
@Data
public class CompareConfig {

  private List<CompareParam> compareParam; // 触发条件
  private String triggerLevel;

  // trigger summary
  private String triggerContent;
}
