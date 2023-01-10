/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.ArrayList;

/**
 * @author zanghaibo
 * @time 2022-08-21 11:21 下午
 */

@Data
public class MiniProgramLogContextQueryResponse {

    /**
     * 日志明细
     */
    private ArrayList<SlsLog> logs = new ArrayList<>();

    /**
     * 查询是否完成
     */
    private boolean                      isCompleted      = false;

    /**
     * 查询范围内日志总量
     */
    private Integer count;

    /**
     * 往后查多少行，最多50
     */
    private Integer backLines;

    /**
     * 往前查多少行, 最多50
     */
    private Integer forwardLines;
}
