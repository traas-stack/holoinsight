/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

/**
 * @author zanghaibo
 * @time 2022-08-07 11:40 上午
 */
@Data
public class AntLogApi<T> {
    private Boolean success;
    private String errorType;
    private String errorCode;
    private String errorMessage;
    private Integer showType;
    private String traceId;
    private T data;
}
