/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.data;

import io.holoinsight.server.home.alert.service.data.load.AIAlarmLoadData;
import io.holoinsight.server.home.alert.service.data.load.RuleAlarmLoadData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wangsiyuan
 * @date 2022/10/11 7:22 下午
 */
@Service
public class LoadDataFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(LoadDataFactory.class);

  @Resource
  private AIAlarmLoadData aiAlarmLoadData;

  @Resource
  private RuleAlarmLoadData ruleAlarmLoadData;

  public AlarmLoadData getLoadDataService(String type) {
    if (type.equals("RULE")) {
      return ruleAlarmLoadData;
    } else if (type.equals("AI")) {
      return aiAlarmLoadData;
    }
    return ruleAlarmLoadData;
  }


}
