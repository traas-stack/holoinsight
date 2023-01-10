/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

/**
 * @author zanghaibo
 * @time 2022-08-26 11:20 上午
 */

@Data
public class AntLogInfo {

  /**
   * project id in DTM DB
   */
  private Integer projectId;

  /**
   * SLS project
   */
  private String projectName;

  /**
   * store ID
   */
  private Integer storeId;

  /**
   * SLS logstore
   */
  private String logstoreName;

  /**
   * 完整路径表示
   */
  private String fullPath;

  /**
   * 目录部分
   */
  private String logPath;

  /**
   * 文件名
   */
  private String filePattern;

  /**
   * 目录下的最大遍历深度
   */
  private Integer maxDepth;

  /**
   * 编码 UTF8 / GBK
   */
  private String encoding;

  /**
   * 文件类型 regular / virtual
   */
  private String type;

}
