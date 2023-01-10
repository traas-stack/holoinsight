/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.task;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: EntityFactoryHolder.java, v 0.1 2022年03月17日 8:04 下午 jinsong.yjs Exp $
 */
public class EntityFactoryHolder {
    private static final Map<String, StackEntityBuilder> entityFactoryMap = Maps.newConcurrentMap();


    public static StackEntityBuilder getBuilder(String name) {
        synchronized (entityFactoryMap) {
            return entityFactoryMap.get(name);
        }
    }

    public static void setBuilder(String name, StackEntityBuilder builder) {
        synchronized (entityFactoryMap) {
            entityFactoryMap.put(name, builder);
        }
    }

}