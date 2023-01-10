/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.main;

import io.holoinsight.server.home.alert.service.task.AlarmTaskScheduler;
import io.holoinsight.server.home.alert.service.task.CacheAlarmConfig;
import io.holoinsight.server.home.alert.service.task.CacheAlarmTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author wangsiyuan
 * @date 2022/2/22 4:27 下午
 */
@Component
public class AlarmStart {

    private static Logger LOGGER = LoggerFactory.getLogger(AlarmStart.class);

    @Resource
    private CacheAlarmConfig cacheAlarmConfig;

    @Resource
    private CacheAlarmTask cacheAlarmTask;

    @Resource
    private AlarmTaskScheduler alarmTaskScheduler;


    public void start() {
        try {
            //启动获取告警配置缓存
            cacheAlarmConfig.start();

            //启动获取告警任务缓存
            cacheAlarmTask.start();

            //启动执行告警任务
            alarmTaskScheduler.start();
        }catch (Exception e){
            LOGGER.error("fail to start task for {}", e.getMessage(), e);
        }
    }
}
