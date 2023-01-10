/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

/**
 * @author zanghaibo
 * @time 2022-09-06 4:52 下午
 */
public class LogConstants {

  /**
   * sls标签配置
   */
  public static final String SLS_KEY_HOST_NAME = "__tag__:__hostname__";

  public static final String SLS_KEY_PATH = "__tag__:__path__";

  public static final String SLS_KEY_PACK_ID = "__tag__:__pack_id__";

  public static final String SLS_KEY_PACK_SHARD_ID = "__pack_shard_id__";

  public static final String SLS_KEY_PACK_CURSOR = "__pack_cursor__";

  public static final String SLS_KEY_PACK_META = "__pack_meta__";

  public static final String SLS_KEY_PACK_NUM = "__pack_num__";

  public static final String SLS_KEY_OFFSET = "__pack_offset__";

  // stdout的日志内容标签
  public static final String SLS_KEY_CONTENT = "log";

  // 无盘采集的日志内容标签，content覆盖log内容
  public static final String SLS_KEY_CUSTOM_CONTENT = "content";

  public static final String SLS_QUERY_META = " | with_pack_meta";

  public static final String SLS_QUERY_COUNT = " | select count(*)";

  /**
   * antlogs
   */

  public static final String CACHE_SPLITTER = "@";



  /**
   * 小程序托管云配置
   */
  public static final String MINI_PROGRAM_LOG_REGION = "cr";

  public static final String MINI_PROGRAM_LOG_PATH_STDOUT = "/virtual/stdout/json.log";

  public static final String MINI_PROGRAM_LOG_CUSTOM_PATH = "/**/*.log";

  public static final String MINI_PROGRAM_REGEX_APP_ID = "^[\\w-]+$";

}
