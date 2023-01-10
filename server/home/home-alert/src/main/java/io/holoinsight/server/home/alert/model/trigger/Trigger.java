/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.trigger;

import io.holoinsight.server.home.alert.model.data.DataResult;
import io.holoinsight.server.home.alert.model.data.DataSource;
import io.holoinsight.server.home.alert.model.emuns.FunctionEnum;
import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/14 11:47 上午
 */
@Data
public class Trigger {

    private List<DataSource> datasources; // 数据源

    private String query; // 表达式

    private String aggregator; // 周期聚合函数

    private Long downsample; // 周期时间

    private int stepNum;// 周期个数

    private int triggerStepNum; // 触发周期个数

    private String triggerContent; // 触发详情简述

    private List<DataResult> dataResult; // 数据

    private List<CompareParam> compareParam; // 触发条件

    private FunctionEnum type;
}
