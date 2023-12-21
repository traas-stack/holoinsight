/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.dal.transformer;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.NotifyStage;

import java.lang.reflect.Type;
import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: AlertNotifyStageMapper.java, Date: 2023-12-20 Time: 13:50
 */
public class AlertNotifyStageMapper {
  public static String asString(List<NotifyStage> notifyStage) {
    return J.toJson(notifyStage);
  }

  public static List<NotifyStage> asObj(String extra) {
    Type t = new TypeToken<List<NotifyStage>>() {}.getType();
    return J.fromJson(extra, t);
  }
}
