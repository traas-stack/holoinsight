/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.event;

import io.holoinsight.server.home.alert.model.event.AlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author wangsiyuan
 * @date 2022/3/28 8:58 下午
 */
@Service
public class AlertEventService implements AlertEventExecutor<AlertEvent> {

    private static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(100);

    private static final Logger logger = LoggerFactory.getLogger(AlertEventService.class);

    @Autowired
    private AlertServiceRegistry alertServiceRegistry;

    /**
     * 生成的告警Graph
     */
    public void handleEvent(AlertEvent alertEvent) {
        try {
            executorService.schedule(() -> {
                try {
                    // 获取数据处理管道
                    List<? extends AlertHandlerExecutor> pipeline = this.alertServiceRegistry.getAlertEventHanderList();

                    if (CollectionUtils.isEmpty(pipeline)) {
                        logger.error(String.format("[%s] pipeline is empty.", alertEvent.getEventTypeEnum()));
                        return;
                    }

                    for (AlertHandlerExecutor handler : pipeline) {
                        try {
                            // 当前处理器处理数据，并返回是否继续向下处理
                            handler.handle(alertEvent.getAlarmNotifies());
                        } catch (Throwable ex) {
                            logger.error(String.format("HandleException,handler=%s", handler.getClass().getSimpleName()), ex);
                        }
                    }
                } catch (Throwable e) {
                    logger.error("fail to handle alertEvent: {}", alertEvent, e);
                }
            }, 0, TimeUnit.MICROSECONDS);
        } catch (Throwable e) {
            logger.error("[HandleEventSchedulerError]", e);
        }
    }
}
