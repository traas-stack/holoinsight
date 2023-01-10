/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * @author wangsiyuan
 * @date 2022/3/29  5:17 下午
 */
@Service
public class MainStart implements InitializingBean {
    private static Logger LOGGER = LoggerFactory.getLogger(MainStart.class);
    @Autowired
    AlarmStart alarmStart;


    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("alarmStart start.");
        alarmStart.start();
    }
}