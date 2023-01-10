/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.util.Date;

/**
 * @author wangsiyuan
 * @date 2022/7/12 8:30 下午
 */
@Data
public class AlarmDingDingRobotDTO {

    /**
     * id
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date gmtCreate;

    /**
     * 修改时间
     */
    private Date gmtModified;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 修改者
     */
    private String modifier;

    /**
     * 群名称
     */
    private String groupName;

    /**
     * 机器人url
     */
    private String robotUrl;

    /**
     * 租户id
     */
    private String tenant;

    /**
     * 额外信息
     */
    private String extra;

}
