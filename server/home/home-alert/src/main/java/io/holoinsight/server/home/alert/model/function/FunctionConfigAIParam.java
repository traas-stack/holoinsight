/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.function;

import io.holoinsight.server.home.facade.trigger.Trigger;
import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/3/17 9:51 下午
 */
@Data
public class FunctionConfigAIParam extends FunctionConfigParam {

  private String tenant;

  private Trigger trigger;

}
