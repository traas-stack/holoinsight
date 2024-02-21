/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.service;

import com.google.gson.reflect.TypeToken;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.dao.entity.MetricInfo;
import io.holoinsight.server.query.grpc.QueryProto.QueryRequest;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: SuperCache.java, v 0.1 2022年03月21日 8:26 下午 jinsong.yjs Exp $
 */
public class SuperCache {

  public Map<String /* type */, Map<String /* k */, MetaDataDictValue>> metaDataDictValueMap;
  public Map<String, QueryRequest> expressionMetricList;

  public Map<String /* metric table */, MetricInfo> metricInfoMap;

  public String getStringValue(String type, String k) {

    Map<String, MetaDataDictValue> kMap = this.metaDataDictValueMap.get(type);

    if (null == kMap) {
      return null;
    }

    MetaDataDictValue meta = kMap.get(k);

    if (null == meta) {
      return null;
    }
    return meta.getDictValue();
  }

  public List<String> getListValue(String type, String k, List<String> defaultValues) {

    Map<String, MetaDataDictValue> kMap = this.metaDataDictValueMap.get(type);

    if (null == kMap) {
      return defaultValues;
    }

    MetaDataDictValue meta = kMap.get(k);

    if (null == meta || StringUtils.isBlank(meta.getDictValue())) {
      return defaultValues;
    }
    return J.fromJson(meta.getDictValue(), new TypeToken<List<String>>() {}.getType());
  }
}
