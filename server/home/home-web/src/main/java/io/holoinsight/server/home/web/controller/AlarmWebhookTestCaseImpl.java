/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.web.InternalWebApi;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <p>
 * </p>
 * <p>
 * Date: 2023-03-27 Time: 20:04
 * </p>
 *
 * @author jsy1001de
 */
@RestController
@RequestMapping("/internal/api/home/alarmWebhook")
@InternalWebApi
@Slf4j
public class AlarmWebhookTestCaseImpl extends BaseFacade {
  @PostMapping("/testCase")
  @ResponseBody
  public JsonResult<Boolean> testCase(@RequestBody Map<String, Object> alarmMessageBody) {
    final JsonResult<Boolean> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(alarmMessageBody, "alarmMessageBody");
        ParaCheckUtil.checkParaNotNull(alarmMessageBody.get("ruleName"), "ruleName");
        ParaCheckUtil.checkParaNotNull(alarmMessageBody.get("alarmLevel"), "alarmLevel");
        ParaCheckUtil.checkParaNotNull(alarmMessageBody.get("alarmContent"), "alarmContent");
        ParaCheckUtil.checkParaNotNull(alarmMessageBody.get("alarmTenant"), "alarmTenant");
        ParaCheckUtil.checkParaNotNull(alarmMessageBody.get("alarmUrl"), "alarmUrl");
        ParaCheckUtil.checkParaNotNull(alarmMessageBody.get("ruleId"), "ruleId");
      }

      @Override
      public void doManage() {
        log.info("alarmWebhook,testCase, {}", J.toJson(alarmMessageBody));
        JsonResult.createSuccessResult(result, true);
      }
    });

    return result;
  }

}
