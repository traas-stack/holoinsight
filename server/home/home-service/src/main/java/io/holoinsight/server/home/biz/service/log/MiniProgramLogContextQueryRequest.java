/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

/**
 * @author zanghaibo
 * @time 2022-08-21 11:16 下午
 */

@Data
public class MiniProgramLogContextQueryRequest {

  /**
   * 服务id
   */
  private String serviceId;

  /**
   * packId
   */
  private String packID;

  /**
   * packMeta
   */
  private String packMeta;

  /**
   * 往后查多少行，最多50
   */
  private Integer backLines;

  /**
   * 往前查多少行, 最多50
   */
  private Integer forwardLines;

  /**
   * 日志路径
   */
  private String logPath;
}
