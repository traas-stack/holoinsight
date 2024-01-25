/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.alert.plugin;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.alert.common.AlarmContentGenerator;
import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.common.TimeRangeUtil;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.model.event.NotifyDataInfo;
import io.holoinsight.server.home.alert.service.converter.DoConvert;
import io.holoinsight.server.home.alert.service.event.AlertHandlerExecutor;
import io.holoinsight.server.home.alert.service.event.RecordSucOrFailNotify;
import io.holoinsight.server.home.common.service.QueryClientService;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryDetailMapper;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryMapper;
import io.holoinsight.server.home.dal.model.AlarmHistory;
import io.holoinsight.server.home.dal.model.AlarmHistoryDetail;
import io.holoinsight.server.home.facade.AlertHistoryExtra;
import io.holoinsight.server.home.facade.AlertNotifyRecordDTO;
import io.holoinsight.server.home.facade.AlertSilenceConfig;
import io.holoinsight.server.home.facade.DataResult;
import io.holoinsight.server.home.facade.InspectConfig;
import io.holoinsight.server.home.facade.trigger.AlertHistoryDetailExtra;
import io.holoinsight.server.home.facade.trigger.DataSource;
import io.holoinsight.server.home.facade.trigger.Trigger;
import io.holoinsight.server.query.grpc.QueryProto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static io.holoinsight.server.home.alert.service.data.load.RuleAlarmLoadData.filterConvert;

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

  @Resource
  private QueryClientService queryClientService;

  @Autowired
  private AlertNotifyHandler alertNotifyHandler;

  private static final String SAVE_HISTORY = "AlertSaveHistoryHandler";

  public void handle(List<AlertNotify> alertNotifies) {
    try {
      // Get alert histories that have not yet been recovered
      QueryWrapper<AlarmHistory> queryWrapper = new QueryWrapper<>();
      queryWrapper.isNull("recover_time");
      List<AlarmHistory> alertHistoryList = alarmHistoryDOMapper.selectList(queryWrapper);
      Map<String, AlarmHistory> alertHistoryMap = alertHistoryList.stream().collect(Collectors
          .toMap(AlarmHistory::getUniqueId, AlarmHistoryDO -> AlarmHistoryDO, (v1, v2) -> v2));

      // Get alert notifications that have not yet been recovered
      List<AlertNotify> alertNotifyList = alertNotifies.stream()
          .filter(alertNotify -> !alertNotify.getIsRecover()).collect(Collectors.toList());
      // Get alert notifications that have been recovered
      List<AlertNotify> alertNotifyRecover =
          alertNotifies.stream().filter(AlertNotify::getIsRecover).collect(Collectors.toList());

      tryQueryLogAnalysis(alertNotifyList);
      tryQueryLogSample(alertNotifyList);
      makeAlertHistory(alertHistoryMap, alertNotifyList);

      makeAlertRecover(alertHistoryMap, alertNotifyRecover);

      // Get alert histories detail to alert notify record
      buildAlertNotifyRecord(alertNotifies, alertHistoryMap);
      LOGGER.info("alert_notification_history_step size [{}]", alertNotifies.size());
    } catch (Exception e) {
      LOGGER.error(
          "[HoloinsightAlertInternalException][AlertSaveHistoryHandler][{}] fail to alert_history_save for {}",
          alertNotifies.size(), e.getMessage(), e);
    }
  }

  private void buildAlertNotifyRecord(List<AlertNotify> alertNotifies,
      Map<String, AlarmHistory> alertHistoryMap) {
    if (CollectionUtils.isEmpty(alertNotifies)) {
      return;
    }
    for (AlertNotify alertNotify : alertNotifies) {
      String uniqueId = alertNotify.getUniqueId();
      AlarmHistory alarmHistory = alertHistoryMap.get(uniqueId);
      AlertNotifyRecordDTO alertNotifyRecord = alertNotify.getAlertNotifyRecord();
      alertNotifyRecord.setHistoryDetailId(alertNotify.getAlarmHistoryDetailId());
      if (Objects.nonNull(alarmHistory)) {
        alertNotifyRecord.setHistoryId(alarmHistory.getId());
      } else {
        alertNotifyRecord.setHistoryId(alertNotify.getAlarmHistoryId());
      }
    }
  }

  private void tryQueryLogSample(List<AlertNotify> alertNotifyList) {
    if (CollectionUtils.isEmpty(alertNotifyList)) {
      return;
    }
    for (AlertNotify alertNotify : alertNotifyList) {
      InspectConfig ruleConfig = alertNotify.getRuleConfig();
      if (ruleConfig == null || !ruleConfig.isLogSampleEnable()) {
        continue;
      }
      if (CollectionUtils.isEmpty(ruleConfig.getMetrics())) {
        continue;
      }
      List<QueryProto.QueryRequest> queryRequests = new ArrayList<>();
      for (Map.Entry<Trigger, List<NotifyDataInfo>> entry : alertNotify.getNotifyDataInfos()
          .entrySet()) {
        Trigger trigger = entry.getKey();
        if (CollectionUtils.isEmpty(trigger.getDatasources())) {
          continue;
        }
        List<QueryProto.Datasource> dsList = new ArrayList<>();
        for (DataSource dataSource : trigger.getDatasources()) {
          QueryProto.Datasource ds =
              buildSampleDatasource(dataSource, trigger, alertNotify.getAlarmTime());
          dsList.add(ds);
        }
        if (!CollectionUtils.isEmpty(dsList)) {
          QueryProto.QueryRequest request = QueryProto.QueryRequest.newBuilder()
              .setTenant(alertNotify.getTenant()).addAllDatasources(dsList).build();
          queryRequests.add(request);
        }
      }
      if (!CollectionUtils.isEmpty(queryRequests)) {
        List<String> logs = new ArrayList<>();
        Long alertTime = alertNotify.getAlarmTime();
        for (QueryProto.QueryRequest queryRequest : queryRequests) {
          QueryProto.QueryResponse response =
              this.queryClientService.queryData(queryRequest, "LOG_ALERT");
          if (response != null && !CollectionUtils.isEmpty(response.getResultsList())) {
            LOGGER.debug("{} log sample result {} request {}", alertNotify.getTraceId(),
                J.toJson(response.getResultsList()), J.toJson(queryRequest));
            for (QueryProto.Result result : response.getResultsList()) {
              Map<String, String> tagMap = result.getTagsMap();
              List<QueryProto.Point> points = result.getPointsList();
              if (CollectionUtils.isEmpty(tagMap) || CollectionUtils.isEmpty(points)) {
                continue;
              }

              for (QueryProto.Point point : points) {
                if (point == null || StringUtils.isEmpty(point.getStrValue())) {
                  continue;
                }
                long timestamp = point.getTimestamp();
                if (alertTime == null || alertTime < timestamp) {
                  // Logs outside the time window
                  continue;
                }
                logs.add(point.getStrValue());
                break;
              }
            }
          }
        }
        if (!CollectionUtils.isEmpty(logs)) {
          alertNotify.setLogSample(logs);
        }
      }
    }
  }

  private void tryQueryLogAnalysis(List<AlertNotify> alertNotifyList) {
    if (CollectionUtils.isEmpty(alertNotifyList)) {
      return;
    }
    for (AlertNotify alertNotify : alertNotifyList) {
      InspectConfig ruleConfig = alertNotify.getRuleConfig();
      if (ruleConfig == null || !ruleConfig.isLogPatternEnable()) {
        continue;
      }
      if (CollectionUtils.isEmpty(alertNotify.getNotifyDataInfos())) {
        continue;
      }
      List<QueryProto.QueryRequest> queryRequests = new ArrayList<>();
      for (Map.Entry<Trigger, List<NotifyDataInfo>> entry : alertNotify.getNotifyDataInfos()
          .entrySet()) {
        Trigger trigger = entry.getKey();
        List<NotifyDataInfo> dataInfos = entry.getValue();
        if (CollectionUtils.isEmpty(trigger.getDatasources())) {
          continue;
        }
        List<QueryProto.Datasource> dsList = new ArrayList<>();
        for (DataSource dataSource : trigger.getDatasources()) {
          QueryProto.Datasource ds =
              buildAnalysisDatasource(dataSource, trigger, alertNotify.getAlarmTime(), dataInfos);
          dsList.add(ds);
        }
        if (!CollectionUtils.isEmpty(dsList)) {
          QueryProto.QueryRequest request = QueryProto.QueryRequest.newBuilder()
              .setTenant(alertNotify.getTenant()).addAllDatasources(dsList).build();
          queryRequests.add(request);
        }
      }
      if (!CollectionUtils.isEmpty(queryRequests)) {
        List<String> logs = new ArrayList<>();
        Long alertTime = alertNotify.getAlarmTime();
        for (QueryProto.QueryRequest queryRequest : queryRequests) {
          QueryProto.QueryResponse response =
              this.queryClientService.queryData(queryRequest, "LOG_ALERT");
          if (response != null && !CollectionUtils.isEmpty(response.getResultsList())) {
            LOGGER.debug("{} log analysis result {} request {}", alertNotify.getTraceId(),
                J.toJson(response.getResultsList()), J.toJson(queryRequest));
            for (QueryProto.Result result : response.getResultsList()) {
              Map<String, String> tagMap = result.getTagsMap();
              List<QueryProto.Point> points = result.getPointsList();
              if (CollectionUtils.isEmpty(tagMap) || !tagMap.containsKey("eventName")) {
                continue;
              }
              if (CollectionUtils.isEmpty(points)) {
                continue;
              }
              for (QueryProto.Point point : points) {
                if (point == null || StringUtils.isEmpty(point.getStrValue())) {
                  continue;
                }
                long timestamp = point.getTimestamp();
                if (alertTime == null || alertTime < timestamp) {
                  // Logs outside the time window
                  continue;
                }
                logs.add(point.getStrValue());
                break;
              }
            }
          }
        }
        if (!CollectionUtils.isEmpty(logs)) {
          alertNotify.setLogAnalysis(logs);
        }
      }
    }
  }

  private QueryProto.Datasource buildSampleDatasource(DataSource dataSource, Trigger trigger,
      Long alarmTime) {
    long start = TimeRangeUtil.getStartTimestamp(alarmTime, dataSource, trigger);
    long end = TimeRangeUtil.getEndTimestamp(alarmTime, dataSource);
    String metric = dataSource.getMetric() + "_logsamples";

    QueryProto.Datasource.Builder builder = QueryProto.Datasource.newBuilder().setStart(start)
        .setEnd(end).setMetric(metric).addAllFilters(filterConvert(dataSource.getFilters()))
        .setAggregator("sample").addAllGroupBy(Collections.singletonList("app"));
    return builder.build();
  }

  private QueryProto.Datasource buildAnalysisDatasource(DataSource dataSource, Trigger trigger,
      Long alarmTime, List<NotifyDataInfo> dataInfos) {
    long start = TimeRangeUtil.getStartTimestamp(alarmTime, dataSource, trigger);
    long end = TimeRangeUtil.getEndTimestamp(alarmTime, dataSource);
    String metric = dataSource.getMetric() + "_analysis";
    List<String> eventNames = new ArrayList<>();
    for (NotifyDataInfo dataInfo : dataInfos) {
      if (CollectionUtils.isEmpty(dataInfo.getTags())) {
        continue;
      }
      String eventName = dataInfo.getTags().get("eventName");
      if (StringUtils.isNotEmpty(eventName)) {
        eventNames.add(eventName);
      }
    }
    QueryProto.Datasource.Builder builder =
        QueryProto.Datasource.newBuilder().setStart(start).setEnd(end).setMetric(metric)
            .setAggregator("known-analysis").addAllGroupBy(Collections.singletonList("eventName"));
    QueryProto.QueryFilter queryFilter;
    if (CollectionUtils.isEmpty(eventNames)) {
      queryFilter = QueryProto.QueryFilter.newBuilder().setType("not_literal").setName("eventName")
          .setValue("__analysis").build();
    } else {
      queryFilter = QueryProto.QueryFilter.newBuilder().setType("literal_or").setName("eventName")
          .setValue(String.join("|", eventNames)).build();
    }
    builder.addAllFilters(Collections.singletonList(queryFilter));
    return builder.build();
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
            if (hasSilenceConfig(alertNotify)) {
              AlertHistoryExtra alertHistoryExtra = getAlertHistoryExtra(alertHistory.getExtra());
              AlertSilenceConfig alertSilenceConfig =
                  getNewAlertSilenceConfig(alertNotify.getRuleConfig(), alertHistoryExtra);
              if (alertSilenceConfig.timeIsUp(alertNotify.getAlarmTime())) {
                alertSilenceConfig.updatePeriod(alertNotify.getAlarmTime());
                alertHistoryExtra.setAlertSilenceConfig(alertSilenceConfig);
                alertHistory.setExtra(J.toJson(alertHistoryExtra));
                LOGGER.info("{} alert rule {} silence has been broken for {}.",
                    alertNotify.getTraceId(), alertNotify.getUniqueId(),
                    J.toJson(alertHistoryExtra.getAlertSilenceConfig()));
              } else {
                LOGGER.info("{} alert rule {} keep silence for {}.", alertNotify.getTraceId(),
                    alertNotify.getUniqueId(), J.toJson(alertSilenceConfig));
              }
              alertNotify.getRuleConfig().setAlertSilenceConfig(alertSilenceConfig);
            }
            alarmHistoryDOMapper.updateById(alertHistory);
          }
          alertNotify.setDuration(alertHistory.getDuration());
        } else {
          // insert new alert history
          AlarmHistory alertHistoryNew = DoConvert.alertHistoryConverter(alertNotify);
          alertHistoryNew.setDuration(0L);
          alertHistoryNew.setApp(getApps(alertNotify));
          alertNotify.setDuration(alertHistoryNew.getDuration());
          if (hasSilenceConfig(alertNotify)) {
            AlertSilenceConfig alertSilenceConfig =
                getNewAlertSilenceConfig(alertNotify.getRuleConfig(), null);
            alertSilenceConfig.updatePeriod(alertNotify.getAlarmTime());
            AlertHistoryExtra alertHistoryExtra = new AlertHistoryExtra();
            alertHistoryExtra.setAlertSilenceConfig(alertSilenceConfig);
            alertHistoryNew.setExtra(J.toJson(alertHistoryExtra));
            alertNotify.getRuleConfig().setAlertSilenceConfig(alertSilenceConfig);
          }
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
          } else {
            genAlertHistoryDetail(alertNotify, historyId);
          }
        }
        RecordSucOrFailNotify.alertNotifyProcessSuc(SAVE_HISTORY, "save history",
            alertNotify.getAlertNotifyRecord());
      } catch (Exception e) {
        RecordSucOrFailNotify.alertNotifyProcessFail(
            alertNotify.getTraceId() + "fail to alert history save for" + e.getMessage(),
            SAVE_HISTORY, "save history", alertNotify.getAlertNotifyRecord());
        LOGGER.error(
            "[HoloinsightAlertInternalException][AlertSaveHistoryHandler][1] {}  fail to alert_history_save for {}",
            alertNotify.getTraceId(), e.getMessage(), e);
      }
    }
  }

  private AlertSilenceConfig getNewAlertSilenceConfig(InspectConfig ruleConfig,
      AlertHistoryExtra alertHistoryExtra) {
    AlertSilenceConfig alertSilenceConfig = ruleConfig.getAlertSilenceConfig();
    if (alertHistoryExtra != null && alertHistoryExtra.alertSilenceConfig != null) {
      AlertSilenceConfig lastSilenceConfig = alertHistoryExtra.getAlertSilenceConfig();
      alertSilenceConfig.setSilencePeriod(lastSilenceConfig.getSilencePeriod());
      alertSilenceConfig.setStep(lastSilenceConfig.getStep());
    }
    return alertSilenceConfig;
  }

  private AlertHistoryExtra getAlertHistoryExtra(String extra) {
    if (StringUtils.isNotEmpty(extra)) {
      return J.fromJson(extra, AlertHistoryExtra.class);
    }
    return new AlertHistoryExtra();
  }

  private boolean hasSilenceConfig(AlertNotify alertNotify) {
    if (alertNotify == null || alertNotify.getRuleConfig() == null
        || alertNotify.getRuleConfig().getAlertSilenceConfig() == null) {
      return false;
    }
    return true;
  }

  private String getApps(AlertNotify alertNotify) {
    if (CollectionUtils.isEmpty(alertNotify.getNotifyDataInfos())) {
      return null;
    }
    Set<String> apps = new HashSet<>();
    for (List<NotifyDataInfo> list : alertNotify.getNotifyDataInfos().values()) {
      for (NotifyDataInfo notifyDataInfo : list) {
        if (CollectionUtils.isEmpty(notifyDataInfo.getTags())) {
          continue;
        }
        String app = notifyDataInfo.getTags().get(this.alertNotifyHandler.getAppKey());
        if (StringUtils.isNotBlank(app)) {
          apps.add(app);
        }
      }
    }
    if (CollectionUtils.isEmpty(apps)) {
      return null;
    }
    String appStr = "," + String.join(",", apps) + ",";
    if (appStr.length() > 1000) {
      return null;
    }
    return appStr;
  }

  private String getApps(List<Map<String, String>> tagList) {
    if (CollectionUtils.isEmpty(tagList)) {
      return null;
    }
    Set<String> apps = new HashSet<>();
    for (Map<String, String> tag : tagList) {
      String app = tag.get(this.alertNotifyHandler.getAppKey());
      if (StringUtils.isNotBlank(app)) {
        apps.add(app);
      }
    }
    if (CollectionUtils.isEmpty(apps)) {
      return null;
    }
    String appStr = "," + String.join(",", apps) + ",";
    if (appStr.length() > 1000) {
      return null;
    }
    return appStr;
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
    LOGGER.info("{} AlarmSaveHistoryDetail {} {} ", alertNotify.getTraceId(),
        alertNotify.getUniqueId(), historyId);
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
      alarmHistoryDetail.setWorkspace(alertNotify.getWorkspace());
      String alarmContentJson = getAlertContentJsonList(notifyDataInfoList);
      alarmHistoryDetail.setAlarmContent(alarmContentJson);
      alarmHistoryDetail.setExtra(buildDetailExtra(alertNotify));
      alarmHistoryDetail.setApp(getApps(tagList));
      alarmHistoryDetailDOMapper.insert(alarmHistoryDetail);
      LOGGER.info("AlarmSaveHistoryDetail {} {} {} ", alertNotify.getTraceId(), historyId,
          alertNotify.getUniqueId());
      alertNotify.setAlarmHistoryId(alarmHistoryDetail.getHistoryId());
      alertNotify.setAlarmHistoryDetailId(alarmHistoryDetail.getId());
    });
  }

  private String buildDetailExtra(AlertNotify alertNotify) {
    AlertHistoryDetailExtra extra = new AlertHistoryDetailExtra();
    if (!CollectionUtils.isEmpty(alertNotify.getLogAnalysis())) {
      extra.logAnalysisContent = alertNotify.getLogAnalysis();
    }
    if (!CollectionUtils.isEmpty(alertNotify.getLogSample())) {
      extra.logSampleContent = alertNotify.getLogSample();
    }
    return J.toJson(extra);
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
        RecordSucOrFailNotify.alertNotifyProcessSuc(SAVE_HISTORY, "alert recover",
            alertNotify.getAlertNotifyRecord());
      } catch (Exception e) {
        RecordSucOrFailNotify.alertNotifyProcessFail(
            alertNotify.getTraceId() + "fail to alert recover for" + e.getMessage(), SAVE_HISTORY,
            "alert recover", alertNotify.getAlertNotifyRecord());
        LOGGER.error(
            "[HoloinsightAlertInternalException][AlertSaveHistoryHandler][1] {}  fail to alert_recover_update for {}",
            alertNotify.getTraceId(), e.getMessage(), e);
      }
    });
  }
}
