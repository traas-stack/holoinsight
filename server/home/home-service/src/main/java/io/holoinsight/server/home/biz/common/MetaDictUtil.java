/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.biz.common;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.service.SuperCacheService;
import io.holoinsight.server.home.common.service.SpringContext;
import io.holoinsight.server.home.common.util.scope.IdentityType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

  public static List<String> getList(String type, String key) {
    List<String> value = MetaDictUtil.getValue(type, key, new TypeToken<List<String>>() {});
    if (CollectionUtils.isEmpty(value))
      return new ArrayList<>();
    return value;
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

  public static String getSystemNotice() {
    return MetaDictUtil.getStringValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.SYSTEM_NOTICE);
  }


  public static Boolean getUlaClose() {

    Boolean value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.ULA_CLOSE,
        new TypeToken<Boolean>() {});
    if (null == value) {
      return false;
    }
    return value;
  }

  public static Boolean isDefaultApmDisplayMenu() {

    Boolean value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG, MetaDictKey.DISPLAY_MENU_APM,
        new TypeToken<Boolean>() {});
    if (null == value) {
      return false;
    }
    return value;
  }

  public static Boolean isApmMaterialized() {

    Boolean value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG,
        MetaDictKey.IS_APM_MATERIALIZED, new TypeToken<Boolean>() {});
    if (null == value) {
      return false;
    }
    return value;
  }

  public static List<String /* 任务名 */> getIgnoreTasks() {
    return MetaDictUtil.getValue(MetaDictType.MANAGE_TASK, MetaDictKey.IGNORE_TASK_LIST,
        new TypeToken<List<String>>() {});
  }

  public static List<String> getTokenUrlWriteList() {
    List<String> value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG,
        MetaDictKey.TOKEN_URL_WRITE_LIST, new TypeToken<List<String>>() {});
    if (CollectionUtils.isEmpty(value))
      return new ArrayList<>();
    return value;
  }

  public static List<String> getTokenUrlNoAuth() {
    List<String> value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG,
        MetaDictKey.TOKEN_URL_NO_AUTH, new TypeToken<List<String>>() {});
    if (CollectionUtils.isEmpty(value))
      return new ArrayList<>();
    return value;
  }

  public static List<String> getResourceKeys() {
    List<String> value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG,
        MetaDictKey.RESOURCE_KEYS, new TypeToken<List<String>>() {});
    if (CollectionUtils.isEmpty(value))
      return Collections.singletonList("tenant");
    return value;
  }

  public static Boolean isMeteringHoloinsightSubmitOpen() {
    Boolean value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG,
        MetaDictKey.METERING_HOLOINSIGHT_SUBMIT_OPEN, new TypeToken<Boolean>() {});
    if (null == value) {
      return false;
    }
    return value;
  }

  public static Map<String, String> getLogTimeLayoutMap() {
    Map<String, String> value = MetaDictUtil.getValue(MetaDictType.GLOBAL_CONFIG,
        MetaDictKey.LOG_TIME_LAYOUT, new TypeToken<Map<String, String>>() {});
    if (CollectionUtils.isEmpty(value))
      return new HashMap<>();
    return value;
  }
}
