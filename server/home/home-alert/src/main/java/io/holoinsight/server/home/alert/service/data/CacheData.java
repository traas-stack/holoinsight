/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.alert.service.data;

import io.holoinsight.server.home.facade.InspectConfig;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wangsiyuan
 * @date 2022/4/11 9:01 下午
 */
@Data
@Service
public class CacheData {

  private Map<String, InspectConfig> uniqueIdMap = new HashMap<>();

  private Map<String, Object> configMap = new HashMap<>();

}
