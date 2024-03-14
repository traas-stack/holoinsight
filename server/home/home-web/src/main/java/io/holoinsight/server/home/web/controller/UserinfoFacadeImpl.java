/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.biz.ula.ULAFacade;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.converter.UserinfoConverter;
import io.holoinsight.server.home.dal.mapper.UserinfoMapper;
import io.holoinsight.server.home.dal.mapper.UserinfoVerificationMapper;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.Userinfo;
import io.holoinsight.server.home.dal.model.UserinfoVerification;
import io.holoinsight.server.home.facade.UserinfoDTO;
import io.holoinsight.server.home.facade.page.MonitorPageRequest;
import io.holoinsight.server.home.facade.page.MonitorPageResult;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author masaimu
 * @version 2023-06-07 18:42:00
 */
@Slf4j
@RestController
@RequestMapping("/webapi/userinfo")
public class UserinfoFacadeImpl extends BaseFacade {

  @Resource
  private UserinfoMapper userinfoMapper;
  @Resource
  private UserinfoVerificationMapper userinfoVerificationMapper;

  @Autowired
  private UserinfoConverter userinfoConverter;

  @Autowired
  private UserOpLogService userOpLogService;

  @Autowired
  private ULAFacade ulaFacade;

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Long> save(@RequestBody UserinfoDTO userinfoDTO) {
    String requestId = UUID.randomUUID().toString();
    final JsonResult<Long> result = new JsonResult<>();
    try {
      facadeTemplate.manage(result, new ManageCallback() {
        @Override
        public void checkParameter() {
          ParaCheckUtil.checkParaNotBlank(userinfoDTO.getNickname(), "nickname");
          if (StringUtils.isNotBlank(userinfoDTO.getNickname())) {
            ParaCheckUtil.checkInvalidCharacter(userinfoDTO.getNickname(),
                "invalid nickname, please use a-z A-Z 0-9 Chinese - _ , . spaces");
          }
          ParaCheckUtil.checkParaNotBlank(userinfoDTO.getUid(), "uid");
          ParaCheckUtil.checkParaBoolean(
              checkMembers(Collections.singletonList(userinfoDTO.getUid())),
              userinfoDTO.getUid() + " is not in current tenant scope.");
          ParaCheckUtil.checkParaId(userinfoDTO.getId());
        }

        @Override
        public void doManage() {
          Userinfo userinfo = userinfoConverter.dtoToDO(userinfoDTO);
          MonitorScope ms = RequestContext.getContext().ms;
          MonitorUser mu = RequestContext.getContext().mu;
          if (null != mu && StringUtils.isBlank(userinfo.getCreator())) {
            userinfo.setCreator(mu.getLoginName());
          }
          if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
            userinfo.setTenant(ms.tenant);
          }
          if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
            userinfo.setWorkspace(ms.workspace);
          }
          userinfo.setGmtCreate(new Date());
          userinfo.setGmtModified(new Date());
          userinfo.setDeleted(false);

          doVerify(userinfo, userinfoDTO);

          userinfoMapper.insert(userinfo);
          Long id = userinfo.getId();

          userOpLogService.append("userinfo", id, OpType.CREATE, mu.getLoginName(), ms.getTenant(),
              ms.getWorkspace(), J.toJson(userinfo), null, null, "userinfo_create");
          JsonResult.createSuccessResult(result, id);
        }
      });
    } catch (Exception e) {
      log.error("{} [holoinsight][userinfo][create] error", requestId, e);
      JsonResult<Long> fail = new JsonResult<>();
      if (e instanceof MonitorException) {
        JsonResult.fillFailResultTo(fail, e.getMessage());
      } else {
        JsonResult.fillFailResultTo(fail, "Fail to create userinfo, requestId " + requestId);
      }
      return fail;
    }

    return result;
  }

  private void doVerify(Userinfo userinfo, UserinfoDTO userinfoDTO) {
    if (userinfoDTO.getUserinfoVerificationId() != null) {
      UserinfoVerification userinfoVerification =
          userinfoVerificationMapper.selectById(userinfoDTO.getUserinfoVerificationId());
      if (!validVerification(userinfoVerification, userinfoDTO)) {
        throw new MonitorException(
            "fail to verify code " + userinfoDTO.getUserinfoVerificationCode());
      }
      switch (userinfoVerification.getContentType()) {
        case "email":
          userinfo.setEmail(userinfoVerification.getVerificationContent());
          break;
        case "phone":
          userinfo.setPhoneNumber(userinfoVerification.getVerificationContent());
      }

      UserinfoVerification update = new UserinfoVerification();
      update.setId(userinfoDTO.getUserinfoVerificationId());
      update.setStatus("expire");
      userinfoVerificationMapper.updateById(update);
    }
  }

  @PostMapping("/update")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> update(@RequestBody UserinfoDTO userinfoDTO) {
    String requestId = UUID.randomUUID().toString();
    final JsonResult<Boolean> result = new JsonResult<>();
    try {
      facadeTemplate.manage(result, new ManageCallback() {
        @Override
        public void checkParameter() {
          ParaCheckUtil.checkParaNotNull(userinfoDTO.getId(), "id");

          if (StringUtils.isNotBlank(userinfoDTO.getTenant())) {
            ParaCheckUtil.checkEquals(userinfoDTO.getTenant(),
                RequestContext.getContext().ms.getTenant(), "tenant is illegal");
          }
          if (StringUtils.isNotBlank(userinfoDTO.getNickname())) {
            ParaCheckUtil.checkInvalidCharacter(userinfoDTO.getNickname(),
                "invalid nickname, please use a-z A-Z 0-9 Chinese - _ , . spaces");
          }
          if (StringUtils.isNotEmpty(userinfoDTO.getUid())) {
            ParaCheckUtil.checkParaBoolean(
                checkMembers(Collections.singletonList(userinfoDTO.getUid())),
                userinfoDTO.getUid() + " is not in current tenant scope.");
          }
        }

        @Override
        public void doManage() {

          MonitorScope ms = RequestContext.getContext().ms;
          QueryWrapper<Userinfo> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("id", userinfoDTO.getId());
          queryWrapper.eq("tenant", ms.getTenant());

          List<Userinfo> userinfos = userinfoMapper.selectList(queryWrapper);

          if (CollectionUtils.isEmpty(userinfos)) {
            throw new MonitorException("cannot find record: " + userinfoDTO.getId());
          }
          Userinfo original = userinfos.get(0);

          MonitorUser mu = RequestContext.getContext().mu;
          if (null != mu && StringUtils.isBlank(userinfoDTO.getModifier())) {
            userinfoDTO.setModifier(mu.getLoginName());
          }
          Userinfo updateItem = userinfoConverter.dtoToDO(userinfoDTO);
          updateItem.setGmtModified(new Date());
          doVerify(updateItem, userinfoDTO);
          int save = userinfoMapper.updateById(updateItem);

          userOpLogService.append("userinfo", userinfoDTO.getId(), OpType.UPDATE,
              RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
              J.toJson(original), J.toJson(updateItem), null, "userinfo_update");

          JsonResult.createSuccessResult(result, save > 0);
        }
      });

    } catch (Exception e) {
      log.error("{} [holoinsight][userinfo][update] error", requestId, e);
      JsonResult<Boolean> fail = new JsonResult<>();
      fail.setData(false);
      if (e instanceof MonitorException) {
        JsonResult.fillFailResultTo(fail, e.getMessage());
      } else {
        JsonResult.fillFailResultTo(fail, "Fail to update userinfo, requestId " + requestId);
      }
      return fail;
    }

    return result;
  }

  private boolean validVerification(UserinfoVerification userinfoVerification,
      UserinfoDTO userinfoDTO) {
    if (!StringUtils.equals(userinfoVerification.getStatus(), "valid")) {
      return false;
    }
    if (!StringUtils.equals(userinfoVerification.getCode(),
        userinfoDTO.getUserinfoVerificationCode())) {
      return false;
    }
    long cur = System.currentTimeMillis();
    Long expireTimestamp = userinfoVerification.getExpireTimestamp();
    if (expireTimestamp != null && expireTimestamp >= cur) {
      return true;
    }
    return false;
  }

  @GetMapping("/query/{id}")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<UserinfoDTO> queryById(@PathVariable("id") Long id) {

    final JsonResult<UserinfoDTO> result = new JsonResult<>();
    String requestId = UUID.randomUUID().toString();
    try {
      facadeTemplate.manage(result, new ManageCallback() {
        @Override
        public void checkParameter() {
          ParaCheckUtil.checkParaNotNull(id, "id");
        }

        @Override
        public void doManage() {
          MonitorScope ms = RequestContext.getContext().ms;
          QueryWrapper<Userinfo> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("id", id);
          queryWrapper.eq("tenant", ms.getTenant());

          List<Userinfo> userinfos = userinfoMapper.selectList(queryWrapper);
          UserinfoDTO userinfoDTO = null;
          if (!CollectionUtils.isEmpty(userinfos)) {
            Userinfo userinfo = userinfos.get(0);
            userinfoDTO = userinfoConverter.doToDTO(userinfo);
          }
          JsonResult.createSuccessResult(result, userinfoDTO);
        }
      });
    } catch (Exception e) {
      log.error("{} [holoinsight][userinfo][query] error", requestId, e);
      JsonResult<UserinfoDTO> fail = new JsonResult<>();

      if (e instanceof MonitorException) {
        JsonResult.fillFailResultTo(fail, e.getMessage());
      } else {
        JsonResult.fillFailResultTo(fail, "Fail to query userinfo, requestId " + requestId);
      }
      return fail;
    }

    return result;
  }

  @DeleteMapping(value = "/delete/{id}")
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Boolean> deleteById(@PathVariable("id") Long id) {

    final JsonResult<Boolean> result = new JsonResult<>();
    String requestId = UUID.randomUUID().toString();
    try {
      facadeTemplate.manage(result, new ManageCallback() {
        @Override
        public void checkParameter() {
          ParaCheckUtil.checkParaNotNull(id, "id");
        }

        @Override
        public void doManage() {
          MonitorScope ms = RequestContext.getContext().ms;
          QueryWrapper<Userinfo> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("id", id);
          queryWrapper.eq("tenant", ms.getTenant());

          List<Userinfo> userinfos = userinfoMapper.selectList(queryWrapper);
          MonitorUser mu = RequestContext.getContext().mu;
          Userinfo userinfo = CollectionUtils.isEmpty(userinfos) ? null : userinfos.get(0);
          if (userinfo != null) {
            Userinfo deleteItem = new Userinfo();
            deleteItem.setId(userinfo.getId());
            deleteItem.setDeleted(true);
            if (null != mu) {
              deleteItem.setModifier(mu.getLoginName());
            }
            deleteItem.setGmtModified(new Date());
            userinfoMapper.updateById(deleteItem);
          }

          userOpLogService.append("userinfo", id, OpType.DELETE,
              RequestContext.getContext().mu.getLoginName(), ms.getTenant(), ms.getWorkspace(),
              J.toJson(userinfo), null, null, "userinfo_delete");

          JsonResult.createSuccessResult(result, true);
        }
      });
    } catch (Exception e) {
      log.error("{} [holoinsight][userinfo][delete] error", requestId, e);
      JsonResult<Boolean> fail = new JsonResult<>();
      fail.setData(false);
      if (e instanceof MonitorException) {
        JsonResult.fillFailResultTo(fail, e.getMessage());
      } else {
        JsonResult.fillFailResultTo(fail, "Fail to delete userinfo, requestId " + requestId);
      }
      return fail;
    }
    return result;
  }

  @PostMapping("/pageQuery")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.VIEW)
  public JsonResult<MonitorPageResult<UserinfoDTO>> pageQuery(
      @RequestBody MonitorPageRequest<UserinfoDTO> pageRequest) {
    final JsonResult<MonitorPageResult<UserinfoDTO>> result = new JsonResult<>();
    String requestId = UUID.randomUUID().toString();
    try {
      facadeTemplate.manage(result, new ManageCallback() {
        @Override
        public void checkParameter() {
          ParaCheckUtil.checkParaNotNull(pageRequest.getTarget(), "target");
        }

        @Override
        public void doManage() {
          UserinfoDTO target = pageRequest.getTarget();
          MonitorScope ms = RequestContext.getContext().ms;
          if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
            target.setTenant(ms.tenant);
          }
          QueryWrapper<Userinfo> queryWrapper = getQueryWrapper(target);
          List<Userinfo> userinfos = userinfoMapper.selectList(queryWrapper);
          MonitorPageResult<UserinfoDTO> pageResult = new MonitorPageResult<>();
          pageResult.setPageSize(pageRequest.getPageSize());
          pageResult.setPageNum(pageRequest.getPageNum());
          pageResult.setItems(new ArrayList<>());
          if (!CollectionUtils.isEmpty(userinfos)) {
            for (Userinfo userinfo : userinfos) {
              pageResult.getItems().add(userinfoConverter.doToDTO(userinfo));
            }
          }
          JsonResult.createSuccessResult(result, pageResult);
        }
      });
    } catch (Exception e) {
      log.error("{} [holoinsight][userinfo][pageQuery] error", requestId, e);
      JsonResult<MonitorPageResult<UserinfoDTO>> fail = new JsonResult<>();
      if (e instanceof MonitorException) {
        JsonResult.fillFailResultTo(fail, e.getMessage());
      } else {
        JsonResult.fillFailResultTo(fail, "Fail to pageQuery userinfo, requestId " + requestId);
      }
      return fail;
    }
    return result;
  }

  private QueryWrapper<Userinfo> getQueryWrapper(UserinfoDTO target) {
    QueryWrapper<Userinfo> queryWrapper = new QueryWrapper<>();
    if (target == null) {
      queryWrapper.eq("id", -1);
      return queryWrapper;
    }
    if (StringUtils.isNotBlank(target.getNickname())) {
      queryWrapper.eq("nickname", target.getNickname());
    }
    if (target.getDeleted() != null) {
      queryWrapper.eq("deleted", target.getDeleted());
    }
    if (StringUtils.isNotBlank(target.getStatus())) {
      queryWrapper.eq("status", target.getStatus());
    }
    if (StringUtils.isNotBlank(target.getCreator())) {
      queryWrapper.eq("creator", target.getCreator());
    }
    if (StringUtils.isNotBlank(target.getModifier())) {
      queryWrapper.eq("modifier", target.getModifier());
    }
    if (StringUtils.isNotBlank(target.getTenant())) {
      queryWrapper.eq("tenant", target.getTenant());
    }
    if (StringUtils.isNotBlank(target.getWorkspace())) {
      queryWrapper.eq("workspace", target.getWorkspace());
    }
    return queryWrapper;
  }

  private boolean checkMembers(List<String> uidList) {
    if (CollectionUtils.isEmpty(uidList)) {
      return true;
    }
    MonitorScope ms = RequestContext.getContext().ms;
    MonitorUser mu = RequestContext.getContext().mu;

    Set<String> userIds = ulaFacade.getCurrentULA().getUserIds(mu, ms);

    for (String uid : uidList) {
      if (!userIds.contains(uid)) {
        return false;
      }
    }
    return true;
  }
}
