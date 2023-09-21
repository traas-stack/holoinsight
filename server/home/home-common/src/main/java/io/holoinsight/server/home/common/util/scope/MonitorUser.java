/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.util.scope;

import lombok.Data;

import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: MonitorUser.java, v 0.1 2022年03月14日 5:10 下午 jinsong.yjs Exp $
 */
@Data
public class MonitorUser {
  public static String MONITOR_USER = "MONITOR_USER";
  // 来自于哪里
  private IdentityType identityType;

  // 用户上的权限，这里是为了权限点的爆炸为顶层人民设计的
  boolean superViewer;
  boolean superAdmin;

  private Long exprie;

  // 以下用户明细，酌情添加
  String userId; // 用户唯一id
  String empId; // 工号
  String userName; // 用户名称
  String email; // Email
  String loginName; // 登录名，
  String mobile; // 电话

  String authToken; // authToken，
  String loginTenant; // 当前登陆租户

  String sourceIp; // 源用户ip，用户校验

  String headImgUrl;

  Set<PowerConstants> powerConstants;

  public boolean isSuper() {
    return isSuperAdmin() || isSuperViewer();
  }

  public static MonitorUser adminUser;

  static {
    adminUser = new MonitorUser();
    adminUser.identityType = IdentityType.INNER;
    adminUser.loginName = "admin";
    adminUser.email = "admin";
    adminUser.authToken = "admin";
    // 特权用户，拥有监控最高权限
    adminUser.superAdmin = true;
  }

  public static MonitorUser newTokenUser(String token) {
    MonitorUser tokenUser = new MonitorUser();
    tokenUser.identityType = IdentityType.OUTTOKEN;
    tokenUser.loginName = token;
    tokenUser.email = token;
    tokenUser.authToken = token;
    tokenUser.superAdmin = false;
    return tokenUser;
  }
}
