/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author masaimu
 * @version 2023-06-07 19:01:00
 */
@Data
@Table(name = "userinfo_verification")
public class UserinfoVerification {
  /**
   * id
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  @Column(name = "gmt_create")
  private Date gmtCreate;

  @Column(name = "gmt_modified")
  private Date gmtModified;

  @Column(name = "code")
  private String code;

  @Column(name = "verification_content")
  private String verificationContent;

  @Column(name = "content_type")
  private String contentType;

  @Column(name = "status")
  public String status;

  @Column(name = "expire_timestamp")
  private Long expireTimestamp;

  @Column(name = "tenant")
  private String tenant;

  @Column(name = "workspace")
  private String workspace;

  @Column(name = "creator")
  public String creator;

  @Column(name = "modifier")
  private String modifier;
}
