/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.service.FuseProtector;
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
import org.apache.commons.lang3.StringUtils;
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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.holoinsight.server.common.service.FuseProtector.CRITICAL_AlertTaskCompute;

/**
 * @author wangsiyuan
 * @date 2022/2/24 16:29
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
  private AlarmHistoryMapper alertHistoryDOMapper;

  @Resource
  private CacheData cacheData;

  @Override
  public void process(ComputeTaskPackage computeTaskPackage) {
    try {
      // get metric data
      alarmDataSet.loadData(computeTaskPackage);

      // alert detection
      List<EventInfo> eventLists = calculate(computeTaskPackage);

      // alert notification
      if (!CollectionUtils.isEmpty(eventLists)) {
        // 数据转换
        Map<String, InspectConfig> inspectConfigMap = cacheData.getUniqueIdMap();
        List<AlertNotify> alarmNotifies = new ArrayList<>();
        for (EventInfo eventInfo : eventLists) {
          if (eventInfo == null || StringUtils.isEmpty(eventInfo.getUniqueId())) {
            continue;
          }
          InspectConfig inspectConfig = inspectConfigMap.get(eventInfo.getUniqueId());
          if (inspectConfig == null) {
            continue;
          }
          alarmNotifies.add(AlertNotify.eventInfoConver(eventInfo, inspectConfig));
        }
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
      FuseProtector.voteCriticalError(CRITICAL_AlertTaskCompute, e.getMessage());
    } finally {
      FuseProtector.voteComplete(CRITICAL_AlertTaskCompute);
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
      // get unrecovered alert history
      List<AlarmHistory> alarmHistoryDOS = alertHistoryDOMapper.selectList(condition);
      Set<String> uniqueIds = new HashSet<>();
      for (AlarmHistory alarmHistory : alarmHistoryDOS) {
        uniqueIds.add(alarmHistory.getUniqueId());
      }
      int parallelSize = computeTaskPackage.getInspectConfigs().size();
      CountDownLatch latch = new CountDownLatch(parallelSize);
      // detection, generate alert event
      for (InspectConfig inspectConfig : computeTaskPackage.getInspectConfigs()) {
        calculator.execute(() -> {
          try {
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
          } finally {
            latch.countDown();
          }
        });
      }
      latch.await();
    } catch (Exception e) {
      LOGGER.error("AlertTaskCompute Exception", e);
    }
    return eventLists;
  }

}
