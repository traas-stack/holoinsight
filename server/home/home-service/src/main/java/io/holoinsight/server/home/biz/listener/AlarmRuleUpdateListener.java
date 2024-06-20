/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.listener;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;
import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.EventBusHolder;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.AlarmHistory;
import io.holoinsight.server.common.dao.entity.AlarmMetric;
import io.holoinsight.server.common.dao.entity.AlarmRule;
import io.holoinsight.server.common.dao.entity.dto.AlarmRuleDTO;
import io.holoinsight.server.common.dao.entity.dto.alarm.AlarmRuleConf;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.DataSource;
import io.holoinsight.server.common.dao.entity.dto.alarm.trigger.Trigger;
import io.holoinsight.server.common.dao.mapper.AlarmHistoryMapper;
import io.holoinsight.server.common.dao.mapper.AlarmRuleMapper;
import io.holoinsight.server.common.service.AlarmMetricService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author jsy1001de
 * @version 1.0: AlarmRuleUpdateListener.java, Date: 2023-06-09 Time: 09:57
 */
@Component
@Slf4j
public class AlarmRuleUpdateListener {

  @Autowired
  private AlarmMetricService alarmMetricService;
  @Resource
  private AlarmHistoryMapper alarmHistoryMapper;
  @Resource
  private AlarmRuleMapper alarmRuleMapper;

  @PostConstruct
  void register() {
    EventBusHolder.register(this);
  }

  @Subscribe
  @AllowConcurrentEvents
  public void onEvent(AlarmRuleDTO alarmRuleDTO) {

    log.info("AlarmRuleUpdateListener status {} bool {}", alarmRuleDTO.getStatus(),
        alarmRuleDTO.getStatus() == 0);
    if (alarmRuleDTO.getStatus() == 0) {
      AlarmRule alarmRule = alarmRuleMapper.selectById(alarmRuleDTO.getId());
      if (alarmRule == null) {
        log.error("cannot find alarm rule for id {}", alarmRuleDTO.getId());
        return;
      }
      deleteAlarmHistory(alarmRuleDTO.getId(), alarmRule.getRuleType());
    }

    if (CollectionUtils.isEmpty(alarmRuleDTO.getRule())) {
      return;
    }
    AlarmRuleConf alarmRuleConf =
        J.fromJson(J.toJson(alarmRuleDTO.getRule()), new TypeToken<AlarmRuleConf>() {}.getType());

    if (CollectionUtils.isEmpty(alarmRuleConf.getTriggers())) {
      return;
    }

    for (Trigger trigger : alarmRuleConf.getTriggers()) {
      if (CollectionUtils.isEmpty(trigger.getDatasources())) {
        continue;
      }

      for (DataSource dataSource : trigger.getDatasources()) {
        if (StringUtils.isBlank(dataSource.getMetric()))
          continue;
        AlarmMetric alarmMetric = new AlarmMetric();

        alarmMetric.setMetricTable(dataSource.getMetric());
        alarmMetric.setRuleId(alarmRuleDTO.getId());
        alarmMetric.setRuleType(alarmRuleDTO.getRuleType());
        alarmMetric.setTenant(alarmRuleDTO.getTenant());
        alarmMetric.setWorkspace(alarmRuleDTO.getWorkspace());
        alarmMetric.setDeleted(0 == alarmRuleDTO.getStatus());

        AlarmMetric db = alarmMetricService.queryByMetric(alarmMetric.getRuleId(),
            alarmMetric.getMetricTable(), alarmMetric.getTenant(), alarmMetric.getWorkspace());
        if (null == db) {
          alarmMetricService.save(alarmMetric);
        } else {
          alarmMetric.setId(db.getId());
          alarmMetricService.updateById(alarmMetric);
        }
      }
    }
  }

  private void deleteAlarmHistory(Long alarmRuleId, String ruleType) {
    QueryWrapper<AlarmHistory> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("unique_id", String.join("_", ruleType, String.valueOf(alarmRuleId)));
    List<AlarmHistory> alarmHistoryList = this.alarmHistoryMapper.selectList(queryWrapper);
    log.info("select history empty {}, unique_id {}", CollectionUtils.isEmpty(alarmHistoryList),
        String.join("_", ruleType, String.valueOf(alarmRuleId)));
    if (CollectionUtils.isEmpty(alarmHistoryList)) {
      return;
    }
    int count = 0;
    for (AlarmHistory alarmHistory : alarmHistoryList) {
      AlarmHistory deletingHistory = new AlarmHistory();
      deletingHistory.setId(alarmHistory.getId());
      deletingHistory.setDeleted(true);
      count += this.alarmHistoryMapper.updateById(deletingHistory);
    }
    log.info("need mark deleting alert history size {}, real deleted size {}",
        alarmHistoryList.size(), count);
  }
}
