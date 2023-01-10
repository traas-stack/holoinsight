/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.biz.service.log;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zanghaibo
 * @time 2022-08-10 10:56 上午
 */
public class AntLogUtils {

    public static String buildQuery(String query, ArrayList<String> instances, String logPath) {
        String dest = "*";
        if (!StringUtils.isEmpty(query)) {
            dest = query;
        }

        // 增加实例选择
        if (!CollectionUtils.isEmpty(instances)) {
            for (int index = 0; index < instances.size(); index++) {
                if (index == 0) {
                    dest = new StringBuffer(dest).append(" and ").append(LogConstants.SLS_KEY_HOST_NAME).append(":").append(instances.get(index)).toString();
                } else {
                    dest = new StringBuffer(dest).append(" or ").append(LogConstants.SLS_KEY_HOST_NAME).append(":").append(instances.get(index)).toString();
                }
            }
        }

        // 增加日志路径选择
        dest = new StringBuffer(dest).append(" and ").append(LogConstants.SLS_KEY_PATH).append(":").append(logPath).toString();

        return dest;
    }

    public static String buildAntLogInfoCacheKey(String region, String appId, String envId, String serviceId, String logPath) {
        String app = buildAntLogInfoApp(appId, envId, serviceId);
        return buildAntLogInfoCacheKey(region, app, logPath);
    }

    public static String buildAntLogInfoApp(String appId, String envId, String serviceId) {
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(envId) || StringUtils.isEmpty(serviceId)) {
            throw new InvalidParameterException("buildAntLogInfoApp params can not be null");
        }
        return new StringBuffer().append(appId).append("_").append(envId).append("_").append(serviceId).toString();
    }

    public static String buildAntLogInfoCacheKey(String region, String app, String logPath) {
        if (StringUtils.isEmpty(region) || StringUtils.isEmpty(app) || StringUtils.isEmpty(logPath)) {
            throw new InvalidParameterException("buildAntLogInfoCacheKey params can not be null");
        }
        return new StringBuffer().append(region).append("@").append(app).append("@").append(logPath).toString();
    }

    public static Map<String, String> DefaultAntLogHeaderForCloudRun() {
        Map<String, String> headers = new HashMap<>();
        headers.put("x-antlogs-tenant", "CLOUDRUN");
        return headers;
    }

}
