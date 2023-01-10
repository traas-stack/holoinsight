/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.gateway.core.grpc;

import io.grpc.Context;

/**
 * <p>
 * created at 2022/2/25
 *
 * @author sw1136562366
 */
public class GrpcApiKeyUtils {
  private static final Context.Key<String> API_KEY = Context.key("apikey");

  /**
   * <p>
   * get.
   * </p>
   */
  public static String get(Context ctx) {
    return API_KEY.get(ctx);
  }

  /**
   * <p>
   * set.
   * </p>
   */
  public static Context set(Context ctx, String apikey) {
    return ctx.withValue(API_KEY, apikey);
  }
}
