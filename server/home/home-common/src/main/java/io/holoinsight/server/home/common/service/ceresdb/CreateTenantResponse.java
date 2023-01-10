/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service.ceresdb;

import lombok.Data;

/**
 *
 * @author jsy1001de
 * @version 1.0: CreateTenantResponse.java, v 0.1 2022年06月21日 5:02 下午 jinsong.yjs Exp $
 */
@Data
public class CreateTenantResponse {
    public String        name;
    public CeresDBTenant tenant;

    @Data
    public static class CeresDBTenant {
        public Integer duration;
        public String  token;
    }
}