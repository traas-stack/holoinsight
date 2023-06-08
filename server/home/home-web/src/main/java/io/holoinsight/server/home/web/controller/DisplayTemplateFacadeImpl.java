/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.biz.service.DisplayTemplateService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.ResultCodeEnum;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.dal.model.dto.DisplayTemplateDTO;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.common.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author jsy1001de
 * @version 1.0: DisplayTemplateFacadeImpl.java, v 0.1 2022年12月06日 上午10:34 jinsong.yjs Exp $
 */
@Slf4j
@RestController
@RequestMapping("/webapi/displaytemplate")
public class DisplayTemplateFacadeImpl extends BaseFacade {
  @Autowired
  private DisplayTemplateService displayTemplateService;

  @GetMapping(value = "/query/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<DisplayTemplateDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<DisplayTemplateDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(id, "id");
      }

      @Override
      public void doManage() {
        DisplayTemplateDTO template = displayTemplateService.queryById(id);

        if (null == template) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, template);
      }
    });
    return result;
  }

  @GetMapping(value = "/query/{type}/{refId}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<DisplayTemplateDTO> queryByTypeRefId(@PathVariable("refId") Long refId,
      @PathVariable("type") String type) {
    final JsonResult<DisplayTemplateDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {
        ParaCheckUtil.checkParaNotNull(refId, "refId");
        ParaCheckUtil.checkParaNotNull(type, "type");
      }

      @Override
      public void doManage() {
        Map<String, Object> columnMap = new HashMap<>();
        columnMap.put("ref_id", refId);
        columnMap.put("type", type);
        List<DisplayTemplateDTO> displayTemplates = displayTemplateService.findByMap(columnMap);

        if (CollectionUtils.isEmpty(displayTemplates)) {
          throw new MonitorException(ResultCodeEnum.CANNOT_FIND_RECORD, "can not find record");
        }
        JsonResult.createSuccessResult(result, displayTemplates.get(0));
      }
    });
    return result;
  }
}
