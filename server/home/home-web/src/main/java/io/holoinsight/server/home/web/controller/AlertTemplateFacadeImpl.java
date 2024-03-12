/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.converter.AlertTemplateConverter;
import io.holoinsight.server.home.dal.mapper.AlertTemplateMapper;
import io.holoinsight.server.home.dal.model.AlertTemplate;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.facade.AlertTemplateDTO;
import io.holoinsight.server.home.facade.AlertTemplateField;
import io.holoinsight.server.home.facade.NotificationTemplate;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import io.holoinsight.server.home.web.security.LevelAuthorizationAccess;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * alert template facade
 *
 * @author masaimu
 * @version 2023-02-21 16:43:00
 */
@Slf4j
@RestController
@RequestMapping("/webapi/alertTemplate")
public class AlertTemplateFacadeImpl extends BaseFacade {

  @Resource
  private AlertTemplateMapper alertTemplateMapper;
  @Autowired
  private AlertTemplateConverter alertTemplateConverter;
  @Autowired
  private UserOpLogService userOpLogService;

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!templateDTO"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlertTemplateFacadeImplChecker")
  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<String> create(@RequestBody AlertTemplateDTO templateDTO) {
    final JsonResult<String> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {}

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        if (null != mu && StringUtils.isBlank(templateDTO.getCreator())) {
          templateDTO.setCreator(mu.getLoginName());
        }
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          templateDTO.setTenant(ms.tenant);
        }
        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          templateDTO.setWorkspace(ms.workspace);
        }
        templateDTO.setUuid(UUID.randomUUID().toString().replace("-", ""));
        templateDTO.setGmtCreate(new Date());
        templateDTO.setGmtModified(new Date());
        AlertTemplate alertTemplate = alertTemplateConverter.dtoToDO(templateDTO);
        int insert = alertTemplateMapper.insert(alertTemplate);
        log.info(
            "create alertNotificationTemplate tenant {} workspace {} creator {} insert size {}",
            templateDTO.getTenant(), templateDTO.getWorkspace(), templateDTO.getCreator(), insert);

        userOpLogService.append("alert_template", alertTemplate.getUuid(), OpType.CREATE,
            mu.getLoginName(), ms.getTenant(), ms.getWorkspace(), J.toJson(templateDTO), null, null,
            "alert_template_create");
        JsonResult.createSuccessResult(result, alertTemplate.getUuid());
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!templateDTO"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlertTemplateFacadeImplChecker")
  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<String> update(@RequestBody AlertTemplateDTO templateDTO) {
    final JsonResult<String> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        AlertTemplate originalItem = alertTemplateMapper.selectById(templateDTO.uuid);
        if (null != mu && StringUtils.isBlank(templateDTO.getModifier())) {
          templateDTO.setModifier(mu.getLoginName());
        }
        templateDTO.setGmtModified(new Date());
        AlertTemplate alertTemplate = alertTemplateConverter.dtoToDO(templateDTO);
        alertTemplateMapper.updateById(alertTemplate);

        userOpLogService.append("alert_template", templateDTO.getUuid(), OpType.UPDATE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(originalItem), J.toJson(templateDTO), null, "alert_template_update");

        JsonResult.createSuccessResult(result, templateDTO.uuid);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!uuid"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlertTemplateFacadeImplChecker")
  @GetMapping("/query/{uuid}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlertTemplateDTO> queryById(@PathVariable("uuid") String uuid) {
    final JsonResult<AlertTemplateDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        QueryWrapper<AlertTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uuid", uuid);
        queryWrapper.eq("tenant", ms.getTenant());
        queryWrapper.eq("workspace", ms.getWorkspace());
        queryWrapper.last("LIMIT 1");
        AlertTemplate template = alertTemplateMapper.selectOne(queryWrapper);
        AlertTemplateDTO dto = null;
        if (template != null) {
          dto = alertTemplateConverter.doToDTO(template);
        }
        JsonResult.createSuccessResult(result, dto);
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!uuid"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlertTemplateFacadeImplChecker")
  @DeleteMapping(value = "/delete/{uuid}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Integer> deleteById(@PathVariable("uuid") String uuid) {
    final JsonResult<Integer> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        int rtn = 0;
        AlertTemplate originalItem = alertTemplateMapper.selectById(uuid);
        if (originalItem != null) {
          rtn = alertTemplateMapper.deleteById(uuid);
        }

        userOpLogService.append("alert_template", uuid, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(originalItem), null, null, "alert_template_delete");

        JsonResult.createSuccessResult(result, rtn);
      }
    });
    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!pageRequest"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlertTemplateFacadeImplChecker")
  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlertTemplateDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlertTemplateDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlertTemplateDTO>> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          pageRequest.getTarget().setTenant(ms.tenant);
        }
        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          pageRequest.getTarget().setWorkspace(ms.workspace);
        }
        Page<AlertTemplate> page = new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        QueryWrapper<AlertTemplate> queryWrapper = new QueryWrapper<>();
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          queryWrapper.eq("tenant", ms.tenant);
        }
        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          queryWrapper.eq("workspace", ms.workspace);
        }
        if (StringUtils.isNotBlank(pageRequest.getTarget().templateName)) {
          queryWrapper.like("template_name", pageRequest.getTarget().templateName);
        }
        queryWrapper.orderByDesc("gmt_modified");

        Page<AlertTemplate> p = alertTemplateMapper.selectPage(page, queryWrapper);
        MonitorPageResult<AlertTemplateDTO> pageResult = new MonitorPageResult<>();
        List<AlertTemplateDTO> dtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(p.getRecords())) {
          for (AlertTemplate template : p.getRecords()) {
            dtoList.add(alertTemplateConverter.doToDTO(template));
          }
        }
        pageResult.setItems(dtoList);
        pageResult.setTotalCount(p.getTotal());
        pageResult.setTotalPage(p.getPages());
        JsonResult.createSuccessResult(result, pageResult);
      }
    });

    return result;
  }

  @GetMapping("/defaulTemplate")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<NotificationTemplate> check() {
    final JsonResult<NotificationTemplate> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        JsonResult.createSuccessResult(result,
            NotificationTemplate.defaultMiniappDingtalkTemplate(null));
      }
    });

    return result;
  }

  @LevelAuthorizationAccess(paramConfigs = {"PARAMETER" + ":$!templateDTO"},
      levelAuthorizationCheckeClass = "io.holoinsight.server.home.web.security.custom.AlertTemplateFacadeImplChecker")
  @PostMapping("/check")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<AlertTemplateDTO> checkTemplateText(@RequestBody AlertTemplateDTO templateDTO) {
    final JsonResult<AlertTemplateDTO> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        NotificationTemplate template = templateDTO.getTemplateConfig();
        if (template == null) {
          result.setMessage("templateConfig is null");
          return;
        }

        if (StringUtils.isNotEmpty(template.text)) {
          if (template.parseText()) {
            result.setData(templateDTO);
          }
          result.setSuccess(template.parseText());
        } else if (!CollectionUtils.isEmpty(template.fieldMap)) {
          template.text = template.getTemplateJson();
          result.setData(templateDTO);
        }
      }
    });

    return result;
  }

  @GetMapping("/listAvailableFields")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
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
      if (alertTemplateField.isCompatible()) {
        continue;
      }
      Map<String, String> item = new HashMap<>();
      item.put("fieldName", alertTemplateField.getFieldName());
      item.put("describe", alertTemplateField.getDescribe());
      item.put("format", alertTemplateField.getFormat());
      list.add(item);
    }
    return list;
  }
}
