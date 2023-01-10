/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author jsy1001de
 * @version 1.0: Env.java, v 0.1 2022年03月07日 5:17 下午 jinsong.yjs Exp $
 */
public class Env {

  private static final Logger logger = LoggerFactory.getLogger(Env.class);

  private static volatile String env;
  private static volatile String app;
  private static volatile String localIp;
  private static volatile String cacheMode;

  private static final String SYSTEM_PROP_ENV_KEY = "metaservice.env";
  private static final String SYSTEM_PROP_APP_KEY = "metaservice.app";
  private static final String SYSTEM_PROP_CACHE_MODE = "cacheMode";


  private static final String UNKNOWN = "UNKNOWN";

  public static final String ENV_PROD = "prod";
  public static final String ENV_DEV = "dev";

  public static final String MODE_LOCAL = "local";
  public static final String MODE_REMOTE = "remote";

  static {
    env = System.getProperty(SYSTEM_PROP_ENV_KEY);

    app = System.getProperty(SYSTEM_PROP_APP_KEY);

    cacheMode = System.getProperty(SYSTEM_PROP_CACHE_MODE);
    try {
      localIp = InetAddress.getLocalHost().getHostAddress();
    } catch (UnknownHostException e) {
      localIp = UNKNOWN;
    }
    logger.info("dim env init, env={}, app={}, localIp={}", env, app, localIp);
  }

  public static void setEnv(String str) {
    env = str;
  }

  public static void setApp(String str) {
    app = str;
  }


  public static String env() {
    if (StringUtils.isEmpty(env)) {
      return ENV_PROD;
    }
    return env;
  }

  public static String app() {
    if (StringUtils.isEmpty(app)) {
      return UNKNOWN;
    }
    return app;
  }

  public static String localIp() {
    if (StringUtils.isEmpty(localIp)) {
      return UNKNOWN;
    }
    return localIp;
  }

  public static boolean isDevEnv() {
    return ENV_DEV.equalsIgnoreCase(env());
  }

  public static boolean isProdEnv() {
    return ENV_PROD.equalsIgnoreCase(env());
  }

  public static boolean isRemote() {
    return MODE_REMOTE.equalsIgnoreCase(cacheMode);
  }
}
