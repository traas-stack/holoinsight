/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute;

import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/2/28 8:00 下午
 */
@Data
public class ComputeTaskPackage {

    private String type = "alarmRule"; // 不同的计算流程（spark、pql）

    private List<ComputeTask> computeTaskList;

}
