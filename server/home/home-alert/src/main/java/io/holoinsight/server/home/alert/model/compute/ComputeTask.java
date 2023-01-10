/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.model.compute;

import io.holoinsight.server.home.alert.model.data.InspectConfig;
import lombok.Data;

import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/2/28 2:57 下午
 */
@Data
public class ComputeTask {

    /**
     * traceId
     */
    private String traceId;

    /**
     * 检测时间点
     */
    private long timestamp;

    /**
     * 告警配置
     */
    public List<InspectConfig> inspectConfigs;

    // 后续可以考虑聚合数据源查询

}
