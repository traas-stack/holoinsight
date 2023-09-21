/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.facade;

import lombok.Data;

import java.util.Date;

/**
 * @author masaimu
 * @version 2023-06-07 18:40:00
 */
@Data
public class UserinfoDTO {

  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  public String nickname;

  public String phoneNumberAlias;

  public String emailAlias;

  public Boolean deleted;

  public String status;

  private String creator;

  private String modifier;

  private String tenant;

  private String workspace;

  private String uid;

  private Long userinfoVerificationId;

  private String userinfoVerificationCode;
}
