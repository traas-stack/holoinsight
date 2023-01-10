/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * bizops错误码相关模型
 */
@Data
public class PositionBizRuleDTO implements Serializable {

  private static final long serialVersionUID = -138649146771977434L;
  /**
   * id：主键id
   */
  private Long id;

  /**
   * 创建时间
   */
  private Date gmtCreate;

  /**
   * 修改时间
   */
  private Date gmtModified;

  /**
   * 账号的下的站点信息
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
   * 返回值类型：只有Http服务类型才会用到该字段，其中Return表示直接从返回值中取，ModelMap表示从ModelMap中取
   */
  private String responseType;

  /**
   * 从modelmap里取的属性，只有选择ModelMap才需要该字段
   */
  private String responseProperty;

  /**
   * 业务错误码相关配置：分为两部分即区分业务是否成功的配置和错误码获取的配置
   * 格式：success###boolean####false&&&status###int####1000@@@errorCode 即&&&区分多个业务是否成功的配置、###区分字段名
   * 字段类型 字段值，@@@区分业务成功与否的配置和错误码获取的配置
   */
  private String errorCodeConfig;

  /**
   * 规则全局生效与否，T代表生效、F代表不生效
   */
  private String globalOpen;
}
