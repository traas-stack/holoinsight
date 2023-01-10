/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.common;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.common.service.SpringContext;
import io.holoinsight.server.home.common.util.scope.IdentityType;
import io.holoinsight.server.home.dal.model.MetaDataDictValue;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: MetaDictUtil.java, v 0.1 2022年05月19日 7:00 下午 jinsong.yjs Exp $
 */
@Slf4j
public class MetaDictUtil {

  @SuppressWarnings("unchecked")
  private static <T> T convertValue(String value, final TypeToken<T> resultClass) {
    try {
      return (T) J.fromJson(value, resultClass.getType());
    } catch (Exception e) {
      log.error("parse value: " + value + ", type: " + resultClass.getType() + ", catch exception, "
          + e.getMessage(), e);
      return null;
    }
  }

  // 注意：当找不到type 或 k 的时候，返回null
  public static <T> T getValue(String type, String k, final TypeToken<T> resultClass) {
    SuperCacheService superCacheService = SpringContext.getBean(SuperCacheService.class);
    Map<String, Map<String, MetaDataDictValue>> metaDataDictValueMap =
        superCacheService.getSc().metaDataDictValueMap;
    Map<String, MetaDataDictValue> kMap = metaDataDictValueMap.get(type);

    if (null == kMap) {
      return null;
    }

    MetaDataDictValue meta = kMap.get(k);

    if (null == meta) {
      return null;
    }
    return convertValue(meta.getDictValue(), resultClass);
  }

  public static String getStringValue(String type, String k) {

    SuperCacheService superCacheService = SpringContext.getBean(SuperCacheService.class);
    Map<String, Map<String, MetaDataDictValue>> metaDataDictValueMap =
        superCacheService.getSc().metaDataDictValueMap;
    Map<String, MetaDataDictValue> kMap = metaDataDictValueMap.get(type);

    if (null == kMap) {
      return null;
    }

    MetaDataDictValue meta = kMap.get(k);

    if (null == meta) {
      return null;
    }
    return meta.getDictValue();
  }

  public static String getUlaType() {

    String value = MetaDictUtil.getStringValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.ULA);
    if (null == value) {
      return IdentityType.MONITOR.name();
    }
    return value;
  }

  public static String getUlaHost() {
    return MetaDictUtil.getStringValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.ULA_HOST);
  }

  public static String getIamHost() {
    return MetaDictUtil.getStringValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.ULA_IAM_HOST);
  }

  public static String getJiGuangHost() {
    return MetaDictUtil.getStringValue(MetaDictType.MARKETPLACE_CONFIG,
        MetaDictKey.MARKETPLACE_JIGUANG_HOST);
  }


  public static Boolean getUlaClose() {

    Boolean value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.ULA_CLOSE,
        new TypeToken<Boolean>() {});
    if (null == value) {
      return false;
    }
    return value;
  }

  public static Boolean isTest() {

    Boolean value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.IS_TEST,
        new TypeToken<Boolean>() {});
    if (null == value) {
      return false;
    }
    return value;
  }

  public static Boolean isQueryTest() {

    Boolean value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.IS_QUERY_TEST,
        new TypeToken<Boolean>() {});
    if (null == value) {
      return false;
    }
    return value;
  }

  public static Boolean isMicroAppCheckSkip() {

    Boolean value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG,
        MetaDictKey.IS_MICROAPP_CHECK_SKIP, new TypeToken<Boolean>() {});
    if (null == value) {
      return false;
    }
    return value;
  }

  public static String getMiniProgramSlsEndPoint() {
    String accessId =
        MetaDictUtil.getStringValue(MetaDictType.MNP_SLS_CONFIG, MetaDictKey.MNP_SLS_ENDPOINT);
    if (accessId == null) {
      return "";
    }
    return accessId;
  }

  public static String getMiniProgramSlsAccessID() {
    String accessId =
        MetaDictUtil.getStringValue(MetaDictType.MNP_SLS_CONFIG, MetaDictKey.MNP_SLS_ACCESS_ID);
    if (accessId == null) {
      return "";
    }
    return accessId;
  }

  public static String getMiniProgramSlsAccessKey() {
    String accessKey =
        MetaDictUtil.getStringValue(MetaDictType.MNP_SLS_CONFIG, MetaDictKey.MNP_SLS_ACCESS_KEY);
    if (accessKey == null) {
      return "";
    }
    return accessKey;
  }

  public static String getMiniProgramAntLogUrl() {
    String accessId =
        MetaDictUtil.getStringValue(MetaDictType.MNP_SLS_CONFIG, MetaDictKey.MNP_ANTLOG_URL);
    if (accessId == null) {
      return "";
    }
    return accessId;
  }

  public static String getMiniProgramAntLogAccessID() {
    String accessId =
        MetaDictUtil.getStringValue(MetaDictType.MNP_SLS_CONFIG, MetaDictKey.MNP_ANTLOG_ACCESS_ID);
    if (accessId == null) {
      return "";
    }
    return accessId;
  }

  public static String getMiniProgramAntLogAccessKey() {
    String accessKey =
        MetaDictUtil.getStringValue(MetaDictType.MNP_SLS_CONFIG, MetaDictKey.MNP_ANTLOG_ACCESS_KEY);
    if (accessKey == null) {
      return "";
    }
    return accessKey;
  }

  public static List<String /* 任务名 */> getIgnoreTasks() {
    return MetaDictUtil.getValue(MetaDictType.MANAGE_TASK, MetaDictKey.IGNORE_TASK_LIST,
        new TypeToken<List<String>>() {});
  }
}
