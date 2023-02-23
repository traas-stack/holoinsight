/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import io.holoinsight.server.home.facade.emuns.BoolOperationEnum;
import io.holoinsight.server.home.facade.trigger.Trigger;
import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/11 6:42 下午
 */
@Data
public class Rule {

  private BoolOperationEnum boolOperation;

  private List<Trigger> triggers;
}
