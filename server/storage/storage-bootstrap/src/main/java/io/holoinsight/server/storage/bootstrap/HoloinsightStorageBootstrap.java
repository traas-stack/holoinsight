/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.storage.bootstrap;

import io.holoinsight.server.common.ContextHolder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class HoloinsightStorageBootstrap {
    public static void main(String[] args) {
        try {
            ContextHolder.ctx = SpringApplication.run(HoloinsightStorageBootstrap.class, args);
        } catch (Throwable e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}