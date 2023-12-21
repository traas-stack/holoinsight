/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.dal.transformer;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.NotifyErrorMsg;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: AlertNotifyErrorCodeMapper.java, Date: 2023-12-20 Time: 13:50
 */
public class AlertNotifyErrorCodeMapper {
  public static String asString(List<NotifyErrorMsg> notifyErrorNode) {
    return J.toJson(notifyErrorNode);
  }

  public static List<NotifyErrorMsg> asObj(String notifyErrorNode) {
    Type t = new TypeToken<List<NotifyErrorMsg>>() {}.getType();
    return J.fromJson(notifyErrorNode, t);
  }
}
