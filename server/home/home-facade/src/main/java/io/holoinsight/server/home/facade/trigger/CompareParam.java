/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade.trigger;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.holoinsight.server.home.facade.emuns.CompareOperationEnum;
import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/3/10 7:20 下午
 */
@Data
public class CompareParam {

  @JsonPropertyDescription("Alarm trigger threshold operator, for example: \"EQ\" represents equal to, \"NEQ\" represents not equal to, \"GT\" represents greater than, \"GTE\" represents greater than or equal to, \"LT\" represents less than, \"LTE\" represents less than or equal to")
  CompareOperationEnum cmp;

  @JsonPropertyDescription("Alarm trigger threshold")
  Double cmpValue;
}
