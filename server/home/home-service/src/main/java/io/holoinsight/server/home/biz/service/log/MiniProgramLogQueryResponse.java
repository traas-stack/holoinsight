/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.service.log;

import lombok.Data;

import java.util.ArrayList;

/**
 * @author zanghaibo
 * @time 2022-08-20 10:24 下午
 */

@Data
public class MiniProgramLogQueryResponse {

  /**
   * 日志标签
   */
  private ArrayList<String> mKeys = new ArrayList<>();

  /**
   * 日志明细
   */
  private ArrayList<SlsLog> logs = new ArrayList<>();

  /**
   * 查询SQL,或者复杂查询条件
   */
  private String aggQuery = "";

  /**
   * GREP过滤查询条件
   */
  private String whereQuery = "";

  /**
   * 是否包含特殊处理 对应服务器日志查询里的awk 脚本 对应SLS查询里的select 脚本
   */
  private Boolean hasSQL = false;

  /**
   * 查询任务是否完成
   */
  private Boolean isCompleted = false;

  /**
   * 日志总量
   */
  private Integer count;

  /**
   * 查询开始时间:s
   */
  private Integer start;

  /**
   * 查询结束时间:s
   */
  private Integer end;

}
