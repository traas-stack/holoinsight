/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.Data;

import java.util.List;

/**
 * @author masaimu
 * @version 2023-02-21 16:22:00
 */
@Data
public class CompareConfig {

  @JsonIgnore
  private List<CompareParam> compareParam; // compare parameter composition

  @JsonPropertyDescription("A set of alarm trigger thresholds, which can contain a judgment condition, for example, greater than 10 or less than 1")
  private CompareParam singleCompareParam; // single compare parameter

  @JsonPropertyDescription("Alarm severity level")
  private String triggerLevel;

  @JsonIgnore
  private String triggerContent;
}
