/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event.alertManagerEvent;

import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.service.event.AlertHandlerExecutor;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryMapper;
import io.holoinsight.server.home.dal.model.AlarmHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author wangsiyuan
 * @date 2022/3/28 9:32 下午
 */
@Service
public class AlertManagerSaveHistoryHandler implements AlertHandlerExecutor {

  private static Logger LOGGER = LoggerFactory.getLogger(AlertManagerSaveHistoryHandler.class);

  @Resource
  private AlarmHistoryMapper alarmHistoryDOMap;

  public void handle(List<AlertNotify> alarmNotifies) {
    try {
      alarmNotifies.forEach(alarmNotify -> {
        AlarmHistory alertHistoryDO = new AlarmHistory();
        alertHistoryDO.setGmtCreate(new Date());
        alertHistoryDO.setTenant(alarmNotify.getTenant());
        alertHistoryDO.setUniqueId(alarmNotify.getUniqueId());
        alertHistoryDO.setRuleName(alarmNotify.getRuleName());
        alertHistoryDO.setAlarmTime(new Date(alarmNotify.getAlarmTime()));
        alertHistoryDO.setAlarmLevel(alarmNotify.getAlarmLevel());
        alarmHistoryDOMap.insert(alertHistoryDO);
      });

      LOGGER.info("AlertManagerSaveHistoryHandler SUCCESS {} ", G.get().toJson(alarmNotifies));
    } catch (Exception e) {
      LOGGER.error("AlertManagerSaveHistoryHandler Exception", e);
    }
  }
}
