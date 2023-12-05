/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author masaimu
 * @version 2023-06-07 11:33:00
 */
@Data
@Table(name = "userinfo")
public class Userinfo {
  /**
   * id
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  @Column(name = "gmt_create")
  private Date gmtCreate;

  @Column(name = "gmt_modified")
  private Date gmtModified;

  /**
   * nickname
   */
  @Column(name = "nickname")
  public String nickname;

  /**
   * phone number
   */
  @Column(name = "phone_number")
  public String phoneNumber;

  /**
   * phone number alias
   */
  @Column(name = "phone_number_alias")
  public String phoneNumberAlias;

  /**
   * email
   */
  @Column(name = "email")
  public String email;

  /**
   * email alias
   */
  @Column(name = "email_alias")
  public String emailAlias;

  @Column(name = "deleted")
  public Boolean deleted;

  @Column(name = "status")
  public String status;

  @Column(name = "creator")
  public String creator;

  @Column(name = "modifier")
  private String modifier;

  @Column(name = "tenant")
  private String tenant;

  @Column(name = "workspace")
  private String workspace;

  @Column(name = "uid")
  private String uid;

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
    this.phoneNumberAlias = mask(phoneNumber);
  }

  public void setEmail(String email) {
    this.email = email;
    this.emailAlias = mask(email);
  }

  public static String mask(String phoneNumber) {
    if (StringUtils.isBlank(phoneNumber)) {
      return phoneNumber;
    }
    int len = phoneNumber.length();
    if (len < 7) {
      return "";
    }
    int maskLen = Math.min(len - 7, 4);
    StringBuilder maskedPhoneNumber = new StringBuilder(phoneNumber.substring(0, 3));
    for (int i = 0; i < maskLen; i++) {
      maskedPhoneNumber.append("*");
    }
    maskedPhoneNumber.append(phoneNumber.substring(3 + maskLen, len));
    return maskedPhoneNumber.toString();
  }
}
