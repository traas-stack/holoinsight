/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util;

/**
 *
 * @author jsy1001de
 * @version 1.0: TenantMetaUtil.java, v 0.1 2022年03月16日 10:36 上午 jinsong.yjs Exp $
 */
public class TenantMetaUtil {

    public static String genTableName(String tenant, String key) {
        return String.format("tenant_%s_%s", tenant, key);
    }

    public static String genTenantServerTableName(String tenant) {
        return String.format("%s_server", tenant);
    }

    public static String genTenantAppTableName(String tenant) {
        return String.format("%s_app", tenant);
    }
}