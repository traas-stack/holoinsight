/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.common.service;

import io.holoinsight.server.common.J;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 *
 * @author jsy1001de
 * @version 1.0: PropertiesLoaderUtils.java, v 0.1 2022年02月25日 10:11 上午 jinsong.yjs Exp $
 */
@Slf4j
public class PropertiesListenerConfig {
    public static Map<String, String> propertiesMap = new HashMap<>();

    private static void processProperties(Properties props) throws BeansException {
        propertiesMap = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            String keyStr = key.toString();
            try {
                // PropertiesLoaderUtils的默认编码是ISO-8859-1,在这里转码一下
                propertiesMap.put(keyStr, new String(props.getProperty(keyStr).getBytes(StandardCharsets.ISO_8859_1),
                        StandardCharsets.UTF_8));
            } catch (Exception e) {
                log.error("get properties error", e);
            }
        }
        log.info(">>>>allProperty : " + J.toJson(propertiesMap));
    }

    public static void loadAllProperties(String propertyFileName) {
        try {
            Properties properties = PropertiesLoaderUtils.loadAllProperties(propertyFileName);
            processProperties(properties);
        } catch (IOException e) {
            log.error("load properties error", e);
        }
    }

    public static String getProperty(String name) {
        return propertiesMap.get(name);
    }

    public static Map<String, String> getAllProperty() {
        return propertiesMap;
    }
}