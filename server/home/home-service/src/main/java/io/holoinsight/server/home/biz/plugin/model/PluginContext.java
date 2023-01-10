/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.plugin.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author masaimu
 * @version 2022-10-31 17:12:00
 */
public class PluginContext {
    private Map<String, Object> context = new HashMap<>();

    public Object get(String key) {
        return context.get(key);
    }

    public Object getOrDefault(String key, Object defaultValue) {
        return context.getOrDefault(key, defaultValue);
    }

    public void put(String key, Object value) {
        this.context.put(key, value);
    }
}
