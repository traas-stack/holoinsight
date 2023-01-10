/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorEnv.java, v 0.1 2022年06月06日 2:02 下午 jinsong.yjs Exp $
 */
@Deprecated
public class MonitorEnv {

    static String currentEnv;

    public static void setCurrentEnv(String env) {
        currentEnv = env;
    }

    public static void setCurrentEnv(ProdEnv env) {
        currentEnv = env.name();
    }

    public static String getCurrentEnv() {
        return currentEnv;
    }

    public static boolean isAwsProdEnv() {
        return getCurrentEnv().equals(ProdEnv.AWS_PROD.name());
    }

    public static boolean isSaasFactoryEnv() {
        return getCurrentEnv().equalsIgnoreCase(ProdEnv.AWS_PROD.name())
               || getCurrentEnv().equalsIgnoreCase(ProdEnv.AWS_DEV.name())
               || getCurrentEnv().equalsIgnoreCase(ProdEnv.ALIYUN_DEV.name())
               || getCurrentEnv().equalsIgnoreCase(ProdEnv.ALIYUN_PROD.name())

                ;
    }

    public static boolean isCloudRunEnv() {
        return getCurrentEnv().equalsIgnoreCase(ProdEnv.CLOUDRUN.name());
    }

    public enum ProdEnv {
                         DEV, AWS_PROD, AWS_DEV, ALIYUN_DEV, ALIYUN_PROD, CLOUDRUN, ATS_EG
    }
}