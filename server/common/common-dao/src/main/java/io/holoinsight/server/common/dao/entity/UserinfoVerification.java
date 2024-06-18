/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author masaimu
 * @version 2023-06-07 19:01:00
 */
@Data
@TableName("userinfo_verification")
public class UserinfoVerification {
  /**
   * id
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  private String code;

  private String verificationContent;

  private String contentType;

  public String status;

  private Long expireTimestamp;

  private String tenant;

  private String workspace;

  public String creator;

  private String modifier;
}
