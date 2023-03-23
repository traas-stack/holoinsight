/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.alert.plugin;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.service.FuseProtector;
import io.holoinsight.server.home.alert.common.AlarmContentGenerator;
import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.model.event.NotifyDataInfo;
import io.holoinsight.server.home.alert.service.converter.DoConvert;
import io.holoinsight.server.home.alert.service.event.AlertHandlerExecutor;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryDetailMapper;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryMapper;
import io.holoinsight.server.home.dal.model.AlarmHistory;
import io.holoinsight.server.home.dal.model.AlarmHistoryDetail;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.trigger.Trigger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.holoinsight.server.common.service.FuseProtector.CRITICAL_AlertSaveHistoryHandler;
import static io.holoinsight.server.common.service.FuseProtector.NORMAL_AlertSaveHistoryDetail;
import static io.holoinsight.server.common.service.FuseProtector.NORMAL_MakeAlertRecover;

/**
 * @author wangsiyuan
 * @date 2022/3/28 9:32 下午
 */
@Service
public class AlertSaveHistoryHandler implements AlertHandlerExecutor {

  private static Logger LOGGER = LoggerFactory.getLogger(AlertSaveHistoryHandler.class);

  @Resource
  private AlarmHistoryMapper alarmHistoryDOMapper;

  @Resource
  private AlarmHistoryDetailMapper alarmHistoryDetailDOMapper;

  public void handle(List<AlertNotify> alertNotifies) {
    try {
      // Get alert histories that have not yet been recovered
      QueryWrapper<AlarmHistory> queryWrapper = new QueryWrapper<>();
      queryWrapper.isNull("recover_time");
      List<AlarmHistory> alertHistoryList = alarmHistoryDOMapper.selectList(queryWrapper);
      Map<String, AlarmHistory> alertHistoryMap = alertHistoryList.stream().collect(Collectors
          .toMap(AlarmHistory::getUniqueId, AlarmHistoryDO -> AlarmHistoryDO, (v1, v2) -> v2));

      // Get alert notifications that have not yet been recovered
      List<AlertNotify> alertNotifyHistory = alertNotifies.stream()
          .filter(alertNotify -> !alertNotify.getIsRecover()).collect(Collectors.toList());
      // Get alert notifications that have been recovered
      List<AlertNotify> alertNotifyRecover =
          alertNotifies.stream().filter(AlertNotify::getIsRecover).collect(Collectors.toList());

      makeAlertHistory(alertHistoryMap, alertNotifyHistory);

      makeAlertRecover(alertHistoryMap, alertNotifyRecover);
    } catch (Exception e) {
      LOGGER.error(
          "[HoloinsightAlertInternalException][AlertSaveHistoryHandler][{}] fail to alert_history_save for {}",
          alertNotifies.size(), e.getMessage(), e);
      FuseProtector.voteCriticalError(CRITICAL_AlertSaveHistoryHandler, e.getMessage());
    }
  }

  private void makeAlertHistory(Map<String, AlarmHistory> alertHistoryMap,
      List<AlertNotify> alertNotifyList) {
    if (CollectionUtils.isEmpty(alertNotifyList)) {
      return;
    }
    for (AlertNotify alertNotify : alertNotifyList) {
      try {
        // Get alert histories that have been not yet recovered
        AlarmHistory alertHistory = alertHistoryMap.get(alertNotify.getUniqueId());
        Long historyId;
        if (alertHistory != null) {
          historyId = alertHistory.getId();
          alertHistory.setDuration(
              (alertNotify.getAlarmTime() - alertHistory.getAlarmTime().getTime()) / 60000);
          if (!alertNotify.isPqlNotify()) {
            alertHistory.setRuleName(alertNotify.getRuleName());
            alertHistory.setAlarmLevel(alertNotify.getAlarmLevel());
            // 设置最新的触发条件
            Set<String> triggerList = G.get().fromJson(alertHistory.getTriggerContent(), Set.class);
            List<NotifyDataInfo> notifyDataInfos = new ArrayList<>();
            alertNotify.getNotifyDataInfos().forEach((key, value) -> {
              notifyDataInfos.addAll(value);
            });
            Set<String> triggerListNew = notifyDataInfos.stream()
                .map(NotifyDataInfo::getTriggerContent).collect(Collectors.toSet());
            triggerListNew = triggerListNew.stream()
                .filter(trigger -> !triggerList.contains(trigger)).collect(Collectors.toSet());
            if (!triggerListNew.isEmpty()) {
              triggerList.addAll(triggerListNew);
              alertHistory.setTriggerContent(G.get().toJson(triggerList));
            }
            alarmHistoryDOMapper.updateById(alertHistory);
          }
          alertNotify.setDuration(alertHistory.getDuration());
        } else {
          // insert new alert history
          AlarmHistory alertHistoryNew = DoConvert.alertHistoryConverter(alertNotify);
          alertHistoryNew.setDuration(0L);
          alertNotify.setDuration(alertHistoryNew.getDuration());
          alarmHistoryDOMapper.insert(alertHistoryNew);
          historyId = alertHistoryNew.getId();
        }
        String traceId = String.join("_", alertNotify.getUniqueId(),
            String.valueOf(alertNotify.getAlarmTime()), String.valueOf(historyId));
        alertNotify.setAlarmTraceId(traceId);

        if (alertNotify.isPqlNotify()) {
          genPqlAlertHistoryDetail(alertNotify, historyId);
        } else {
          Map<Trigger, List<NotifyDataInfo>> notifyDataInfos = alertNotify.getNotifyDataInfos();
          if (CollectionUtils.isEmpty(notifyDataInfos)) {
            LOGGER.warn("{} {} notifyDataInfos is empty, return.", alertNotify.getTraceId(),
                alertNotify.getAlarmTraceId());
            return;
          }
          genAlertHistoryDetail(alertNotify, historyId);
        }
        FuseProtector.voteComplete(NORMAL_AlertSaveHistoryDetail);
      } catch (Exception e) {
        LOGGER.error(
            "[HoloinsightAlertInternalException][AlertSaveHistoryHandler][1] {}  fail to alert_history_save for {}",
            alertNotify.getTraceId(), e.getMessage(), e);
        FuseProtector.voteNormalError(NORMAL_AlertSaveHistoryDetail, e.getMessage());
      }
    }
  }

  private void genPqlAlertHistoryDetail(AlertNotify alertNotify, Long historyId) {
    AlarmHistoryDetail alarmHistoryDetail = new AlarmHistoryDetail();
    alarmHistoryDetail.setAlarmTime(new Date(alertNotify.getAlarmTime()));
    alarmHistoryDetail.setHistoryId(historyId);
    alarmHistoryDetail.setUniqueId(alertNotify.getUniqueId());
    alarmHistoryDetail.setTenant(alertNotify.getTenant());
    alarmHistoryDetail.setGmtCreate(new Date());
    alarmHistoryDetail.setEnvType(alertNotify.getEnvType());
    alarmHistoryDetail.setDatasource(alertNotify.getPqlRule().getPql());
    List<Map<String, String>> tagList = alertNotify.getPqlRule().getDataResult().stream()
        .map(DataResult::getTags).collect(Collectors.toList());
    alarmHistoryDetail.setTags(G.get().toJson(tagList));
    alarmHistoryDetail.setAlarmContent(AlarmContentGenerator.genPqlAlarmContent(
        alertNotify.getPqlRule().getPql(), alertNotify.getPqlRule().getDataResult()));
    alarmHistoryDetailDOMapper.insert(alarmHistoryDetail);
    alertNotify.setAlarmHistoryId(alarmHistoryDetail.getHistoryId());
    alertNotify.setAlarmHistoryDetailId(alarmHistoryDetail.getId());
  }

  private void genAlertHistoryDetail(AlertNotify alertNotify, Long historyId) {
    alertNotify.getNotifyDataInfos().forEach((trigger, notifyDataInfoList) -> {
      List<Map<String, String>> tagList = new ArrayList<>();
      notifyDataInfoList.forEach(notifyDataInfo -> {
        tagList.add(notifyDataInfo.getTags());
      });
      // 根据告警历史id插入详情
      AlarmHistoryDetail alarmHistoryDetail = new AlarmHistoryDetail();
      alarmHistoryDetail.setAlarmTime(new Date(alertNotify.getAlarmTime()));
      alarmHistoryDetail.setHistoryId(historyId);
      alarmHistoryDetail.setUniqueId(alertNotify.getUniqueId());
      alarmHistoryDetail.setTenant(alertNotify.getTenant());
      alarmHistoryDetail.setGmtCreate(new Date());
      alarmHistoryDetail.setEnvType(alertNotify.getEnvType());
      alarmHistoryDetail.setDatasource(G.get().toJson(trigger));
      alarmHistoryDetail.setTags(G.get().toJson(tagList));
      String alarmContentJson = getAlertContentJsonList(notifyDataInfoList);
      alarmHistoryDetail.setAlarmContent(alarmContentJson);
      alarmHistoryDetailDOMapper.insert(alarmHistoryDetail);
      LOGGER.info("AlarmSaveHistoryDetail {} {} {} ", alertNotify.getTraceId(), historyId,
          alertNotify.getUniqueId());
      alertNotify.setAlarmHistoryId(alarmHistoryDetail.getHistoryId());
      alertNotify.setAlarmHistoryDetailId(alarmHistoryDetail.getId());
    });
  }

  private String getAlertContentJsonList(List<NotifyDataInfo> notifyDataInfoList) {
    if (CollectionUtils.isEmpty(notifyDataInfoList)) {
      return null;
    }
    List<String> list = new ArrayList<>();
    for (int i = 0; i < notifyDataInfoList.size(); i++) {
      NotifyDataInfo item = notifyDataInfoList.get(i);
      String content = item.getTriggerContent() + ", 当前值 " + item.getCurrentValue();
      String dims = getDimStr(item.getTags());
      if (StringUtils.isNotBlank(dims)) {
        content = content + ", " + dims;
      }
      list.add(content);
    }
    return JSONObject.toJSONString(list);
  }

  public static String getDimStr(Map<String, String> tags) {
    if (CollectionUtils.isEmpty(tags)) {
      return StringUtils.EMPTY;
    }
    StringBuilder res = new StringBuilder("维度: ");
    for (Map.Entry<String, String> entry : tags.entrySet()) {
      res.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
    }
    return res.substring(0, res.length() - 2);
  }

  private void makeAlertRecover(Map<String, AlarmHistory> alertHistoryDOMap,
      List<AlertNotify> alertNotifyRecover) {
    alertNotifyRecover.parallelStream().forEach(alertNotify -> {
      try {
        AlarmHistory alertHistory = alertHistoryDOMap.get(alertNotify.getUniqueId());
        alertHistory.setRecoverTime(new Date(alertNotify.getAlarmTime()));
        alarmHistoryDOMapper.updateById(alertHistory);
        FuseProtector.voteComplete(NORMAL_MakeAlertRecover);
      } catch (Exception e) {
        LOGGER.error(
            "[HoloinsightAlertInternalException][AlertSaveHistoryHandler][1] {}  fail to alert_recover_update for {}",
            alertNotify.getTraceId(), e.getMessage(), e);
        FuseProtector.voteNormalError(NORMAL_MakeAlertRecover, e.getMessage());
      }
    });
  }
}
