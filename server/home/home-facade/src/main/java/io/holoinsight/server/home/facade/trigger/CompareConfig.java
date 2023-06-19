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

  @JsonPropertyDescription("一组告警触发阈值，可以包含一个判断条件，大于10或小于1")
  private CompareParam singleCompareParam; // single compare parameter

  @JsonPropertyDescription("告警级别")
  private String triggerLevel;

  @JsonIgnore
  private String triggerContent;
}
