/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.facade.AlertTemplateField;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * alert template facade
 * 
 * @author masaimu
 * @version 2023-02-21 16:43:00
 */
@RestController
@RequestMapping("/webapi/alertTemplate")
public class AlertTemplateFacadeImpl extends BaseFacade {

  @GetMapping("/listAvailableFields")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<List<Map<String, String>>> listAvailableFields() {
    final JsonResult<List<Map<String, String>>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        result.setData(getAllFields());
      }
    });
    return result;
  }

  static List<Map<String, String>> getAllFields() {
    List<Map<String, String>> list = new ArrayList<>();
    for (AlertTemplateField alertTemplateField : AlertTemplateField.values()) {
      Map<String, String> item = new HashMap<>();
      item.put("fieldName", alertTemplateField.getFieldName());
      item.put("describe", alertTemplateField.getDescribe());
      item.put("format", alertTemplateField.getFormat());
      list.add(item);
    }
    return list;
  }
}
