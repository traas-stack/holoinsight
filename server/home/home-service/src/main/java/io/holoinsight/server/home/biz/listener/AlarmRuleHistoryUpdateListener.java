/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import io.holoinsight.server.home.common.util.EventBusHolder;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryDetailMapper;
import io.holoinsight.server.home.dal.model.AlarmHistoryDetail;
import io.holoinsight.server.home.facade.AlarmHistoryDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author jsy1001de
 * @version 1.0: AlarmRuleUpdateListener.java, Date: 2023-06-09 Time: 09:57
 */
@Component
@Slf4j
public class AlarmRuleHistoryUpdateListener {

  @Resource
  private AlarmHistoryDetailMapper detailMapper;

  @PostConstruct
  void register() {
    EventBusHolder.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onEvent(AlarmHistoryDTO alarmHistoryDTO) {

    if (alarmHistoryDTO.isDeleted()) {
      deleteAlarmHistoryDetail(alarmHistoryDTO.getId(), alarmHistoryDTO.getUniqueId());
    }
  }

  private void deleteAlarmHistoryDetail(Long alarmHistoryId, String uniqueId) {
    QueryWrapper<AlarmHistoryDetail> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("unique_id", uniqueId);
    queryWrapper.eq("history_id", alarmHistoryId);
    int count = this.detailMapper.delete(queryWrapper);
    log.info("delete alarm history detail size {}", count);
  }
}
