/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.common;

import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.DataResult;

import java.util.List;

/**
 * @author zanghaibo
 * @time 2022-12-28 8:23 下午
 */
public class AlarmContentGenerator {

  public static String genPqlAlarmContent(String pql, List<DataResult> dataResult) {
    return J.toJson(dataResult);
  }

}
