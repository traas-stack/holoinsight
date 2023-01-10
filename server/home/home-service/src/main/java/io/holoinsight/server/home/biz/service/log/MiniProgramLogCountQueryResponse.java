/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

/**
 * @author zanghaibo
 * @time 2022-08-23 6:25 下午
 */

@Data
public class MiniProgramLogCountQueryResponse {

    /**
     * 日志总量
     */
    private Long count;

    /**
     * 租户
     */
    private String appId;

    /**
     * namespace
     */
    private String envId;

    /**
     * 服务名
     */
    private String serviceId;

    /**
     * 查询开始时间:s
     */
    private Integer start;

    /**
     * 查询结束时间:s
     */
    private Integer end;
}
