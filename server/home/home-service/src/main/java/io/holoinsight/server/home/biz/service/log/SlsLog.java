/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zanghaibo
 * @time 2022-08-09 3:44 下午
 */

@Data
public class SlsLog implements Serializable {

    private static final long serialVersionUID = 990784970657839155L;

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
     * 日志路径，830版本中logPath填写默认值"stdout"
     */
    private String logPath;

    /**
     * version
     */
    private String version;

    /**
     * IP
     */
    private String source;

    /**
     * PodName
     */
    private String podName;

    /**
     * 日志内容
     */
    private String content;

    /**
     * 日志时间
     */
    private Integer time;

    /**
     * packId
     */
    private String packId;

    /**
     * packMeta
     */
    private String packMeta;

    /**
     * packShardId
     */
    private Integer packShardId;

    /**
     * packCursor
     */
    private String packCursor;

    /**
     * packNum
     */
    private Integer packNum;

    /**
     * packOffset
     */
    private Integer packOffset;

}
