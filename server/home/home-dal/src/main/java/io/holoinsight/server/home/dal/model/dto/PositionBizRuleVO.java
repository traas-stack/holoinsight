/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 前端页面VO
 */
@Data
public class PositionBizRuleVO implements Serializable {

  private static final long serialVersionUID = 4541940716760943696L;

  /**
   * 账号下的站点信息
   */
  public String appId;

  /**
   * 环境
   */
  public String envId;

  /**
   * 服务名
   */
  public String appName;

  /**
   * 接口类型：这里分为Rpc类型和Http类型
   */
  private String interfaceType;

  /**
   * 服务接口
   */
  private String interfaceName;

  /**
   * 规则全局生效与否，T代表生效、F代表不生效
   */
  private String globalOpen;

  /**
   * 返回值类型：只有Http服务类型才会用到该字段，其中Return表示直接从返回值中取，ModelMap表示从ModelMap中取
   */
  private String responseType;

  /**
   * 从modelmap里取的属性，只有选择ModelMap才需要该字段
   */
  private String responseProperty;


  /**
   * 业务结果是否失败,至少配置一个
   */
  private List<BizResultConfig> bizResult;

  /**
   * 业务错误码相关配置
   */
  private String errorCode;

}
