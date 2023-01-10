/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.ceresdb;

import lombok.Data;

/**
 *
 * @author jsy1001de
 * @version 1.0: CreateTenantRequest.java, v 0.1 2022年06月21日 4:45 下午 jinsong.yjs Exp $
 */
@Data
public class CreateTenantRequest {
    /**
     * 租户，唯一键
     */
    public String  name;
    /**
     * token
     */
    public String  token;
    /**
     * 数据存储时长
     */
    public Integer ttlHour;
}