/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.trigger;

import io.holoinsight.server.home.alert.model.emuns.CompareOperationEnum;
import lombok.Data;

/**
 * @author wangsiyuan
 * @date 2022/3/10 7:20 下午
 */
@Data
public class CompareParam {

    CompareOperationEnum cmp;

    Double cmpValue;
}
