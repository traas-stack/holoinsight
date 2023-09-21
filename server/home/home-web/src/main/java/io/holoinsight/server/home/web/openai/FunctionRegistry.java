/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.openai;

import com.unfbx.chatgpt.entity.chat.Functions;
import com.unfbx.chatgpt.entity.chat.Parameters;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.dal.model.openai.AlarmRuleFc;
import io.holoinsight.server.home.dal.model.openai.CustomPluginFc;
import io.holoinsight.server.home.dal.model.openai.CustomPluginUpsertFc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author masaimu
 * @version 2023-06-20 13:12:00
 */
@Service
@Slf4j
public class FunctionRegistry {
  @Autowired
  private AlarmRuleFcService alarmRuleFcService;

  @Autowired
  private CustomPluginFcService customPluginFcService;

  public String createAlertRule(Map<String, Object> paramMap) {
    log.info("createAlertRule paramMap {}", J.toJson(paramMap));
    return this.alarmRuleFcService.createAlertRule(paramMap);
  }

  public String queryCustomPlugin(Map<String, Object> paramMap) {
    log.info("queryCustomPlugin paramMap {}", J.toJson(paramMap));
    return this.customPluginFcService.queryCustomPlugin(paramMap);
  }

  public String createCustomPlugin(Map<String, Object> paramMap) {
    log.info("createCustomPlugin paramMap {}", J.toJson(paramMap));
    return this.customPluginFcService.createCustomPlugin(paramMap);
  }


  public List<Functions> getFunctions() {
    Functions createAlertRule = Functions.builder() //
        .name("createAlertRule") //
        .description("Create alarm rule") //
        .parameters(J.fromJson(AlarmRuleFc.getJsonSchema(), Parameters.class)) //
        .build();

    Functions queryCustomPlugin = Functions.builder() //
        .name("queryCustomPlugin") //
        .description("Search for log monitoring configuration") //
        .parameters(J.fromJson(CustomPluginFc.getJsonSchema(), Parameters.class)) //
        .build();

    Functions createCustomPlugin = Functions.builder() //
        .name("createCustomPlugin") //
        .description("Create log monitoring configuration") //
        .parameters(J.fromJson(CustomPluginUpsertFc.getJsonSchema(), Parameters.class)) //
        .build();

    return Arrays.asList(createAlertRule, queryCustomPlugin, createCustomPlugin);
  }
}
