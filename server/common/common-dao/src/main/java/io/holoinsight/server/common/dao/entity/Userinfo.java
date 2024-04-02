/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author masaimu
 * @version 2023-06-07 11:33:00
 */
@Data
@TableName("userinfo")
public class Userinfo {
  /**
   * id
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  private Date gmtCreate;

  private Date gmtModified;

  /**
   * nickname
   */
  public String nickname;

  /**
   * phone number
   */
  public String phoneNumber;

  /**
   * phone number alias
   */
  public String phoneNumberAlias;

  /**
   * email
   */
  public String email;

  /**
   * email alias
   */
  public String emailAlias;

  public Boolean deleted;

  public String status;

  public String creator;

  private String modifier;

  private String tenant;

  private String workspace;

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
