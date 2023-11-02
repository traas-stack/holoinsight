/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.config;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author jsy1001de
 * @version 1.0: RestAuthUtil.java, v 0.1 2022年06月01日 1:54 下午 jinsong.yjs Exp $
 */
public class RestAuthUtil {

  public static RestAuthUtil singleton = new RestAuthUtil();

  public static final Set<String> NO_AUTH_PATH = Collections.unmodifiableSet(
      new HashSet<>(Arrays.asList("/webapi/sys/config", "/webapi/token/apply", "/webapi/sys/time",
          "/webapi/user/logout", "/webapi/sys/checkservice", "/api/v1/prometheus/write")));

  public static final Set<String> AUTH_PATH =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList("/webapi", "/openapi")));

  public static final List<String> NO_AUTH_PREFIX =
      Collections.unmodifiableList(Arrays.asList("/actuator", "/internal/api/"));

  public boolean isNoAuthRequest(HttpServletRequest req) {
    return NO_AUTH_PATH.contains(req.getServletPath())
        || NO_AUTH_PREFIX.stream().anyMatch(prefix -> req.getServletPath().startsWith(prefix));
  }

  public boolean isAuthRequest(HttpServletRequest req) {
    return AUTH_PATH.stream().anyMatch(prefix -> req.getServletPath().startsWith(prefix));
  }
}
