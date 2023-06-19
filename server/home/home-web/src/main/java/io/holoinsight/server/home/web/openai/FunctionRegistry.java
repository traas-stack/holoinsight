/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.openai;

import com.unfbx.chatgpt.entity.chat.Functions;
import com.unfbx.chatgpt.entity.chat.Parameters;
import io.holoinsight.server.common.J;
import io.holoinsight.server.home.facade.openai.AlarmRuleFc;
import io.holoinsight.server.home.facade.openai.CustomPluginFc;
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


  public List<Functions> getFunctions() {
    Functions createAlertRule = Functions.builder() //
        .name("createAlertRule") //
        .description("创建告警规则") //
        .parameters(J.fromJson(AlarmRuleFc.getJsonSchema(), Parameters.class)) //
        .build();

    Functions queryCustomPlugin = Functions.builder() //
        .name("queryCustomPlugin") //
        .description("查找日志监控配置") //
        .parameters(J.fromJson(CustomPluginFc.getJsonSchema(), Parameters.class)) //
        .build();

    return Arrays.asList(createAlertRule, queryCustomPlugin);
  }
}
