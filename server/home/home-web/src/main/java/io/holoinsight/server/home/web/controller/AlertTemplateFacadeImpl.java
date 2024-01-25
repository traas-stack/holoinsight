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
import io.holoinsight.server.home.dal.converter.AlertNotificationTemplateConverter;
import io.holoinsight.server.home.dal.mapper.AlertNotificationTemplateMapper;
import io.holoinsight.server.home.dal.model.AlertNotificationTemplate;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.facade.AlertNotificationTemplateDTO;
import io.holoinsight.server.home.facade.AlertTemplateField;
import io.holoinsight.server.home.facade.NotificationTemplate;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.web.common.ManageCallback;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
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
  private AlertNotificationTemplateMapper alertNotificationTemplateMapper;
  @Autowired
  private AlertNotificationTemplateConverter alertNotificationTemplateConverter;
  @Autowired
  private UserOpLogService userOpLogService;

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Long> create(@RequestBody AlertNotificationTemplateDTO templateDTO) {
    final JsonResult<Long> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

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
        templateDTO.setGmtCreate(new Date());
        templateDTO.setGmtModified(new Date());
        AlertNotificationTemplate alertNotificationTemplate =
            alertNotificationTemplateConverter.dtoToDO(templateDTO);
        int insert = alertNotificationTemplateMapper.insert(alertNotificationTemplate);
        log.info(
            "create alertNotificationTemplate tenant {} workspace {} creator {} insert size {}",
            templateDTO.getTenant(), templateDTO.getWorkspace(), templateDTO.getCreator(), insert);

        userOpLogService.append("alert_notification_template", alertNotificationTemplate.getId(),
            OpType.CREATE, mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(templateDTO), null, null, "alert_notification_template_create");
        JsonResult.createSuccessResult(result, alertNotificationTemplate.getId());
      }
    });

    return result;
  }

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Long> update(@RequestBody AlertNotificationTemplateDTO templateDTO) {
    final JsonResult<Long> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        MonitorUser mu = RequestContext.getContext().mu;
        AlertNotificationTemplate originalItem =
            alertNotificationTemplateMapper.selectById(templateDTO.id);
        if (null != mu && StringUtils.isBlank(templateDTO.getModifier())) {
          templateDTO.setModifier(mu.getLoginName());
        }
        templateDTO.setGmtModified(new Date());
        AlertNotificationTemplate alertNotificationTemplate =
            alertNotificationTemplateConverter.dtoToDO(templateDTO);
        int update = alertNotificationTemplateMapper.updateById(alertNotificationTemplate);

        userOpLogService.append("alert_notification_template", templateDTO.getId(), OpType.UPDATE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(originalItem), J.toJson(templateDTO), null,
            "alert_notification_template_update");

        JsonResult.createSuccessResult(result, templateDTO.id);
      }
    });

    return result;
  }

  @GetMapping("/query/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<AlertNotificationTemplateDTO> queryById(@PathVariable("id") Long id) {
    final JsonResult<AlertNotificationTemplateDTO> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        QueryWrapper<AlertNotificationTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("tenant", ms.getTenant());
        queryWrapper.eq("workspace", ms.getWorkspace());
        queryWrapper.last("LIMIT 1");
        AlertNotificationTemplate template =
            alertNotificationTemplateMapper.selectOne(queryWrapper);
        AlertNotificationTemplateDTO dto = null;
        if (template != null) {
          dto = alertNotificationTemplateConverter.doToDTO(template);
        }
        JsonResult.createSuccessResult(result, dto);
      }
    });

    return result;
  }

  @DeleteMapping(value = "/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Integer> deleteById(@PathVariable("id") Long id) {
    final JsonResult<Integer> result = new JsonResult<>();
    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        MonitorScope ms = RequestContext.getContext().ms;
        int rtn = 0;
        AlertNotificationTemplate originalItem = alertNotificationTemplateMapper.selectById(id);
        if (originalItem != null) {
          rtn = alertNotificationTemplateMapper.deleteById(id);
        }

        userOpLogService.append("alert_notification_template", id, OpType.DELETE,
            RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
            J.toJson(originalItem), null, null, "alert_notification_template_delete");

        JsonResult.createSuccessResult(result, rtn);
      }
    });
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<AlertNotificationTemplateDTO>> pageQuery(
      @RequestBody MonitorPageRequest<AlertNotificationTemplateDTO> pageRequest) {
    final JsonResult<MonitorPageResult<AlertNotificationTemplateDTO>> result = new JsonResult<>();
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
        Page<AlertNotificationTemplate> page =
            new Page<>(pageRequest.getPageNum(), pageRequest.getPageSize());
        QueryWrapper<AlertNotificationTemplate> queryWrapper = new QueryWrapper<>();
        if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
          queryWrapper.eq("tenant", ms.tenant);
        }
        if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
          queryWrapper.eq("workspace", ms.workspace);
        }
        if (StringUtils.isNotBlank(pageRequest.getTarget().templateName)) {
          queryWrapper.like("template_name", pageRequest.getTarget().templateName);
        }

        Page<AlertNotificationTemplate> p =
            alertNotificationTemplateMapper.selectPage(page, queryWrapper);
        MonitorPageResult<AlertNotificationTemplateDTO> pageResult = new MonitorPageResult<>();
        List<AlertNotificationTemplateDTO> dtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(p.getRecords())) {
          for (AlertNotificationTemplate template : p.getRecords()) {
            dtoList.add(alertNotificationTemplateConverter.doToDTO(template));
          }
        }
        pageResult.setItems(dtoList);
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

  @PostMapping("/check")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> checkTemplateText(
      @RequestBody AlertNotificationTemplateDTO templateDTO) {
    final JsonResult<Boolean> result = new JsonResult<>();

    facadeTemplate.manage(result, new ManageCallback() {
      @Override
      public void checkParameter() {

      }

      @Override
      public void doManage() {
        if (templateDTO.templateConfig == null
            || StringUtils.isEmpty(templateDTO.templateConfig.text)) {
          JsonResult.createSuccessResult(result, false);
          return;
        }
        JsonResult.createSuccessResult(result, templateDTO.templateConfig.parseText());
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
      Map<String, String> item = new HashMap<>();
      item.put("fieldName", alertTemplateField.getFieldName());
      item.put("describe", alertTemplateField.getDescribe());
      item.put("format", alertTemplateField.getFormat());
      list.add(item);
    }
    return list;
  }
}
