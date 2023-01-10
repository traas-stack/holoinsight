/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.ArrayList;

/**
 * @author zanghaibo
 * @time 2022-08-20 10:24 下午
 */

@Data
public class MiniProgramLogQueryRequest {

    /**
     * 服务id
     */
    private String serviceId;

    /**
     * 查询开始时间:s
     */
    private Integer from;

    /**
     * 查询结束时间:s
     */
    private Integer to;

    /**
     * 查询语句
     */
    private String query;

    /**
     * 分页数
     */
    private Integer pageNum;

    /**
     * 分页大小
     */
    private Integer pageSize;

    /**
     * 当前时间范围内的日志总量
     * 默认20防止出现空数据报错的情况
     */
    private Integer count = 20;

    /**
     * 是否倒序显示
     */
    private boolean isReverse = true;

    /**
     * 实例名
     */
    private ArrayList<String> instances = new ArrayList<>();

    /**
     * 查询日志路径列表
     */
    private String logPath;

}
