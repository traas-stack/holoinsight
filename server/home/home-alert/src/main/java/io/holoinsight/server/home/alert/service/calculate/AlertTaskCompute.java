/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.calculate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.config.EnvironmentProperties;
import io.holoinsight.server.home.alert.common.G;
import io.holoinsight.server.home.alert.model.compute.ComputeContext;
import io.holoinsight.server.home.alert.model.compute.ComputeTask;
import io.holoinsight.server.home.alert.model.compute.ComputeTaskPackage;
import io.holoinsight.server.home.alert.model.data.InspectConfig;
import io.holoinsight.server.home.alert.model.emuns.EventTypeEnum;
import io.holoinsight.server.home.alert.model.event.AlertNotify;
import io.holoinsight.server.home.alert.model.event.AlertEvent;
import io.holoinsight.server.home.alert.model.event.EventInfo;
import io.holoinsight.server.home.alert.service.data.AlarmDataSet;
import io.holoinsight.server.home.alert.service.data.CacheData;
import io.holoinsight.server.home.alert.service.event.AlertEventService;
import io.holoinsight.server.home.alert.service.task.AlarmTaskExecutor;
import io.holoinsight.server.home.dal.mapper.AlarmHistoryMapper;
import io.holoinsight.server.home.dal.model.AlarmHistory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangsiyuan
 * @date 2022/2/24 4:29 下午
 */
@Service
public class AlertTaskCompute implements AlarmTaskExecutor<ComputeTaskPackage> {

    private static Logger LOGGER = LoggerFactory.getLogger(AlertTaskCompute.class);

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

    @Autowired
    private EnvironmentProperties environmentProperties;

    @Override
    public void process(ComputeTaskPackage computeTaskPackage) {

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
            alertEventService.handleEvent(new AlertEvent(getEventKey(), alarmNotifies));
        }

    }

    private EventTypeEnum getEventKey() {
        String env = this.environmentProperties.getDeploymentSite();
        LOGGER.info("holoinsight.env value: {}", env);
        if(StringUtils.isNotBlank(env) && env.startsWith("ats-cloudrun")){
            return EventTypeEnum.CloudRun;
        }
        return EventTypeEnum.AlarmEvent;
    }

    private List<EventInfo> calculate(ComputeTaskPackage computeTaskPackage) {
        List<EventInfo> eventLists = new ArrayList<>();

        try {
            QueryWrapper<AlarmHistory> condition = new QueryWrapper<>();
            condition.isNull("recover_time");
            //获取未恢复的告警
            List<AlarmHistory> alarmHistoryDOS = alarmHistoryDOMapper.selectList(condition);
            List<String> uniqueIds = alarmHistoryDOS.stream().map(AlarmHistory::getUniqueId).collect(Collectors.toList());

            // 进行计算,生成告警事件
            for (ComputeTask computeTask : computeTaskPackage.getComputeTaskList()) {
                for (InspectConfig inspectConfig : computeTask.getInspectConfigs()) {

                    ComputeContext context = new ComputeContext();
                    context.setTimestamp(computeTask.getTimestamp());
                    context.setInspectConfig(inspectConfig);
                    EventInfo eventList = abstractUniformInspectRunningRule.eval(context);
                    if (eventList != null) {
                        eventLists.add(eventList);
                    } else if (uniqueIds.contains(inspectConfig.getUniqueId())) {
                        eventList = new EventInfo();
                        eventList.setAlarmTime(computeTask.getTimestamp());
                        eventList.setUniqueId(inspectConfig.getUniqueId());
                        eventList.setIsRecover(true);
                        eventLists.add(eventList);
                        eventList.setEnvType(inspectConfig.getEnvType());
                    }
                    LOGGER.info("{} {} {} calculate package {} ,eventList: {}", computeTask.getTraceId(), inspectConfig.getTraceId(), inspectConfig.getUniqueId(), G.get().toJson(inspectConfig), G.get().toJson(eventList));
                }
            }
        } catch (Exception e) {
            LOGGER.error("AlarmTaskCompute Exception for {}", e.getMessage(), e);
        }
        return eventLists;
    }

}
