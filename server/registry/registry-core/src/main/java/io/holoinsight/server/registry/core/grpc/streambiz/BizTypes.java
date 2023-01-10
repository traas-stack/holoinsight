/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.registry.core.grpc.streambiz;

/**
 * <p>
 * created at 2022/3/3
 *
 * @author zzhb101
 */
public final class BizTypes {
  public static final int ECHO = 0;
  public static final int UNSUPPORTED = -1;
  public static final int BIZ_ERROR = -2;
  public static final int RPC_ERROR = -3;
  public static final int LIST_FILES = 10;
  public static final int LIST_FILES_RESP = 11;
  public static final int PREVIEW_FILE = 20;
  public static final int PREVIEW_FILE_RESP = 21;
  public static final int SPLIT_LOG = 30;
  public static final int SPLIT_LOG_RESP = 31;
  public static final int INSPECT = 40;
  public static final int INSPECT_RESP = 41;
  public static final int MATCH_FILES = 50;
  public static final int MATCH_FILES_RESP = 51;
  public static final int DRY_RUN = 60;
  public static final int DRY_RUN_RESP = 61;
  public static final int HTTP_PROXY = 70;
  public static final int HTTP_PROXY_RESP = 71;

  /**
   * Dry run request and response biz code
   */
  public static final int CHECK_TASK = 80;
  public static final int CHECK_TASK_RESP = 81;
}
