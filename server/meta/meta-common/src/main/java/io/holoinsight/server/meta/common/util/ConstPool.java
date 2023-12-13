/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.meta.common.util;

/**
 *
 * @author jsy1001de
 * @version 1.0: ConstPool.java, v 0.1 2022年03月07日 4:09 下午 jinsong.yjs Exp $
 */
public class ConstPool {

  /**
   * TableService端口
   */
  public static final int GRPC_PORT_TABLE = 5740;

  /**
   * DataService端口
   */
  public static final int GRPC_PORT_DATA = 5741;

  /**
   * TableService
   */
  public static final int TABLE_SERVICE_THREAD_SIZE = 16;

  // gprc服务端超时时间
  public static final int GRPC_WITH_DEADLINE_AFTER_MS_0 = 10 * 1000;

  // gprc服务端超时时间
  public static final int GRPC_WITH_DEADLINE_AFTER_MS_1 = 3 * 60 * 1000;

  // gprc服务端超时时间
  public static final int GRPC_WITH_DEADLINE_AFTER_MS_2 = 10 * 60 * 1000;

  // grpc接收批量更新数量上限
  public static final int GRPC_UPDATE_MAX_SIZE = 1000;

  // grpc接收批量写入数量上限
  public static final int GRPC_INSERT_MAX_SIZE = 6000;

  // grpc接收根据pk批量查询数量上限
  public static final int GRPC_QUERY_BY_PKS_MAX_SIZE = 4000;

  // grpc stream返回数量上限
  public static final int GRPC_QUERY_RETURN_BATCH_SIZE = 50000;


  /**
   * DimWriter
   */
  public static final int DIM_WRITER_THREAD_SIZE = 16;

  /**
   * DimWriter
   */
  public static final int REAL_DIM_WRITER_THREAD_SIZE = 64;

  public static final String COMMON_DICT_DOMAIN = "common";

}
