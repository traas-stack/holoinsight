/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.openai;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.biz.service.CustomPluginService;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.mapper.CustomPluginMapper;
import io.holoinsight.server.home.dal.model.CustomPlugin;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-06-20 13:55:00
 */
@Service
@Slf4j
public class CustomPluginFcService {

  @Resource
  private CustomPluginMapper customPluginMapper;

  @Autowired
  private CustomPluginService customPluginService;

  public String queryCustomPlugin(Map<String, Object> paramMap) {
    log.info("paramMap: " + J.toJson(paramMap));
    String name = (String) paramMap.get("name");

    QueryWrapper<CustomPlugin> queryWrapper = new QueryWrapper<>();
    MonitorScope ms = RequestContext.getContext().ms;
    if (ms != null) {
      queryWrapper.eq("tenant", ms.getTenant());
    }

    List<CustomPlugin> customPlugins = this.customPluginMapper.selectList(queryWrapper);

    if (StringUtils.isBlank(name)) {
      return "The name of log monitoring is empty";
    }
    String original = "[" + name + "]";

    List<String> combinations = new ArrayList<>();
    Map<String, CustomPlugin> map = new HashMap<>();
    for (CustomPlugin item : customPlugins) {
      String descKey = "[" + item.name + "]";
      combinations.add(descKey + "\n");
      map.put(descKey, item);
    }

    String key = OpenAiService.getInstance().getSimilarKey(original, combinations);
    CustomPlugin customPlugin = map.get(key);
    if (customPlugin == null) {
      return null;
    }
    return J.toJson(customPlugin);
  }
}
