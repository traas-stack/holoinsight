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

  @JsonPropertyDescription("告警触发阈值判断符，例如：EQ等于、NEQ不等于、GT大于、GTE大于等于、LT小于、LTE小于等于")
  CompareOperationEnum cmp;

  @JsonPropertyDescription("告警触发阈值")
  Double cmpValue;
}
