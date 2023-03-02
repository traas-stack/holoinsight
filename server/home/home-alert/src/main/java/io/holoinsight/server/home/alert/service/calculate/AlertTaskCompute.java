/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.home.alert.common.AlertStat;
import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.compute.ComputeContext;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.model.event.EventInfo;
import io.holoinsight.server.home.alert.service.data.AlarmDataSet;
import io.holoinsight.server.home.alert.service.data.CacheData;
import io.holoinsight.server.home.alert.service.event.AlertEventService;
import io.holoinsight.server.home.alert.service.task.AlarmTaskExecutor;
import io.holoinsight.server.home.common.exception.HoloinsightAlertIllegalArgumentException;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryMapper;
import io.holoinsight.server.home.dal.model.AlarmHistory;
import io.holoinsight.server.home.facade.InspectConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author wangsiyuan
 * @date 2022/2/24 4:29 下午
 */
@Service
public class AlertTaskCompute implements AlarmTaskExecutor<ComputeTaskPackage> {

  private static Logger LOGGER = LoggerFactory.getLogger(AlertTaskCompute.class);

  ThreadPoolExecutor calculator = new ThreadPoolExecutor(20, 20, 10, TimeUnit.SECONDS,
      new ArrayBlockingQueue<>(1000), r -> new Thread(r, "Calculator"));

  @Resource
  private AlarmDataSet alarmDataSet;

  @Resource
  private AbstractUniformInspectRunningRule abstractUniformInspectRunningRule;

  @Resource
  private AlertEventService alertEventService;

  @Resource
  private AlarmHistoryMapper alarmHistoryDOMapper;

  @Resource
  private CacheData cacheData;

  @Override
  public void process(ComputeTaskPackage computeTaskPackage) {
    try {
      // 获取数据
      alarmDataSet.loadData(computeTaskPackage);

      // 进行计算
      List<EventInfo> eventLists = calculate(computeTaskPackage);

      // 发送网关处理
      if (!CollectionUtils.isEmpty(eventLists)) {
        // 数据转换
        Map<String, InspectConfig> inspectConfigMap = cacheData.getUniqueIdMap();
        List<AlertNotify> alarmNotifies = eventLists.stream()
            .map(e -> AlertNotify.eventInfoConver(e, inspectConfigMap.get(e.getUniqueId())))
            .collect(Collectors.toList());
        alertEventService.handleEvent(alarmNotifies);
      }
    } catch (HoloinsightAlertIllegalArgumentException e) {
      LOGGER.error(
          "[HoloinsightAlertIllegalArgumentException][AlertTaskCompute][{}] fail to execute alert task compute for {}",
          computeTaskPackage.inspectConfigs.size(), e.getMessage(), e);
    } catch (Throwable e) {
      LOGGER.error(
          "[HoloinsightAlertInternalException][AlertTaskCompute][{}] fail to execute alert task compute for {}",
          computeTaskPackage.inspectConfigs.size(), e.getMessage(), e);
    } finally {
      Map<String, Map<String, Long>> counterMap = AlertStat.statRuleTypeCount(computeTaskPackage);
      for (Map.Entry<String /* tenant */, Map<String /* ruleType */, Long>> entry : counterMap
          .entrySet()) {
        for (Map.Entry<String, Long> item : entry.getValue().entrySet()) {
          LOGGER.info("[ALERT_COMPUTE][{}][{}] finish to compute alert {}", entry.getKey(),
              item.getKey(), item.getValue());
        }
      }
    }
  }

  private List<EventInfo> calculate(ComputeTaskPackage computeTaskPackage) {
    List<EventInfo> eventLists = new ArrayList<>();

    if (CollectionUtils.isEmpty(computeTaskPackage.getInspectConfigs())) {
      return Collections.emptyList();
    }

    try {
      QueryWrapper<AlarmHistory> condition = new QueryWrapper<>();
      condition.isNull("recover_time");
      // 获取未恢复的告警
      List<AlarmHistory> alarmHistoryDOS = alarmHistoryDOMapper.selectList(condition);
      Set<String> uniqueIds = new HashSet<>();
      for (AlarmHistory alarmHistory : alarmHistoryDOS) {
        uniqueIds.add(alarmHistory.getUniqueId());
      }

      // 进行计算,生成告警事件
      for (InspectConfig inspectConfig : computeTaskPackage.getInspectConfigs()) {
        calculator.execute(() -> {
          ComputeContext context = new ComputeContext();
          context.setTimestamp(computeTaskPackage.getTimestamp());
          context.setInspectConfig(inspectConfig);
          EventInfo eventList = abstractUniformInspectRunningRule.eval(context);
          if (eventList != null) {
            eventLists.add(eventList);
          } else if (uniqueIds.contains(inspectConfig.getUniqueId())) {
            eventList = new EventInfo();
            eventList.setAlarmTime(computeTaskPackage.getTimestamp());
            eventList.setUniqueId(inspectConfig.getUniqueId());
            eventList.setIsRecover(true);
            eventLists.add(eventList);
            eventList.setEnvType(inspectConfig.getEnvType());
          }
          LOGGER.info("{} {} {} calculate package {} ,eventList: {}",
              computeTaskPackage.getTraceId(), inspectConfig.getTraceId(),
              inspectConfig.getUniqueId(), G.get().toJson(inspectConfig),
              G.get().toJson(eventList));
        });
      }
    } catch (Exception e) {
      LOGGER.error("AlarmTaskCompute Exception for {}", e.getMessage(), e);
    }
    return eventLists;
  }

}
