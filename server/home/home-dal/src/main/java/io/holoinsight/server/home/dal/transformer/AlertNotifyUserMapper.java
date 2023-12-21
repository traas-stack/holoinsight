/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.dal.transformer;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.NotifyUser;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: AlertNotifyUserMapper.java, Date: 2023-12-20 Time: 13:50
 */
public class AlertNotifyUserMapper {
  public static String asString(List<NotifyUser> notifyUser) {
    return J.toJson(notifyUser);
  }

  public static List<NotifyUser> asObj(String notifyUser) {
    Type t = new TypeToken<List<NotifyUser>>() {}.getType();
    return J.fromJson(notifyUser, t);
  }
}
