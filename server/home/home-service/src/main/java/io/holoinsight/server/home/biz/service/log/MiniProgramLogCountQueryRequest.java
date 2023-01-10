/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.ArrayList;

/**
 * @author zanghaibo
 * @time 2022-08-25 2:57 下午
 */

@Data
public class MiniProgramLogCountQueryRequest {

    /**
     * 服务id
     */
    private String serviceId;

    /**
     * 请求
     */
    private String query;

    /**
     * 实例
     */
    private ArrayList<String> instances = new ArrayList<>();

    /**
     * 日志路径
     */
    private String logPath;

    /**
     * 查询开始时间:s
     */
    private Integer from;

    /**
     * 查询结束时间:s
     */
    private Integer to;

}
