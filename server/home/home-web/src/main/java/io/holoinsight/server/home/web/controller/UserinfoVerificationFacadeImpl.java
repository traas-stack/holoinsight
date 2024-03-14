/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.holoinsight.server.common.J;
import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.dao.entity.MetaDataDictValue;
import io.holoinsight.server.common.dao.mapper.MetaDataDictValueMapper;
import io.holoinsight.server.home.biz.service.UserOpLogService;
import io.holoinsight.server.home.biz.service.UserinfoVerificationService;
import io.holoinsight.server.home.common.util.MonitorException;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.MonitorUser;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.dal.mapper.UserinfoVerificationMapper;
import io.holoinsight.server.home.dal.model.OpType;
import io.holoinsight.server.home.dal.model.UserinfoVerification;
import io.holoinsight.server.home.facade.emuns.PeriodType;
import io.holoinsight.server.home.common.util.ManageCallback;
import io.holoinsight.server.home.web.common.ParaCheckUtil;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * @author masaimu
 * @version 2023-06-08 17:51:00
 */
@Slf4j
@RestController
@RequestMapping("/webapi/userinfoVerification")
public class UserinfoVerificationFacadeImpl extends BaseFacade {

  @Resource
  private UserinfoVerificationMapper userinfoVerificationMapper;

  @Autowired
  private UserOpLogService userOpLogService;

  @Resource
  private MetaDataDictValueMapper metaDataDictValueMapper;

  @Autowired
  private UserinfoVerificationService userinfoVerificationService;

  @PostMapping("/create")
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.TENANT, needPower = PowerConstants.EDIT)
  public JsonResult<Long> save(@RequestBody UserinfoVerification userinfoVerification) {
    String requestId = UUID.randomUUID().toString();
    final JsonResult<Long> result = new JsonResult<>();
    try {
      facadeTemplate.manage(result, new ManageCallback() {
        @Override
        public void checkParameter() {
          ParaCheckUtil.checkParaNotBlank(userinfoVerification.getVerificationContent(),
              "verificationContent");
          ParaCheckUtil.checkParaNotBlank(userinfoVerification.getContentType(), "contentType");
          ParaCheckUtil.checkInvalidUserinfoVerificationContentType(
              userinfoVerification.getContentType(), "invalid contentType");
          ParaCheckUtil.checkParaId(userinfoVerification.getId());
        }

        @Override
        public void doManage() {

          MonitorScope ms = RequestContext.getContext().ms;
          MonitorUser mu = RequestContext.getContext().mu;
          if (null != mu) {
            userinfoVerification.setCreator(mu.getLoginName());
          }
          if (null != ms && !StringUtils.isEmpty(ms.tenant)) {
            userinfoVerification.setTenant(ms.tenant);
          }
          if (null != ms && !StringUtils.isEmpty(ms.workspace)) {
            userinfoVerification.setWorkspace(ms.workspace);
          }
          userinfoVerification.setGmtCreate(new Date());

          checkFrequency(userinfoVerification);
          sendVerificationMsg(userinfoVerification);
          userinfoVerificationMapper.insert(userinfoVerification);
          Long id = userinfoVerification.getId();

          userOpLogService.append("userinfo_verification", id, OpType.CREATE, mu.getLoginName(),
              ms.getTenant(), ms.getWorkspace(), null, J.toJson(userinfoVerification), null,
              "userinfo_create");
          JsonResult.createSuccessResult(result, id);
        }
      });
    } catch (Exception e) {
      log.error("{} [holoinsight][userinfo_verification][create] error", requestId, e);
      JsonResult<Long> fail = new JsonResult<>();
      if (e instanceof MonitorException) {
        JsonResult.fillFailResultTo(fail, e.getMessage());
      } else {
        JsonResult.fillFailResultTo(fail,
            "Fail to create userinfo_verification, requestId " + requestId);
      }
      return fail;
    }

    return result;
  }

  private void sendVerificationMsg(UserinfoVerification userinfoVerification) {
    String code = generateVerificationCode();
    userinfoVerification.setCode(code);
    Long expireTimestamp = userinfoVerification.getGmtCreate().getTime()
        + (3L * PeriodType.FIVE_MINUTE.intervalMillis());
    userinfoVerification.setExpireTimestamp(expireTimestamp);
    userinfoVerificationService.sendMessage(userinfoVerification);
    userinfoVerification.setStatus("valid");
  }

  public static String generateVerificationCode() {
    Random random = new Random();
    int code = random.nextInt(1000000);
    return String.format("%06d", code);
  }

  private void checkFrequency(UserinfoVerification userinfoVerification) {
    String tenant = userinfoVerification.getTenant();
    if (StringUtils.isBlank(tenant)) {
      throw new MonitorException("tenant can not be empty");
    }
    long cur = System.currentTimeMillis();
    long day = PeriodType.DAY.rounding(cur);
    String dict_key = String.join("_", tenant, String.valueOf(day));
    QueryWrapper<MetaDataDictValue> queryWrapper = new QueryWrapper<>();
    queryWrapper.eq("type", "userinfo_verification_threshold");
    queryWrapper.eq("dict_key", dict_key);
    queryWrapper.eq("version", 1);
    List<MetaDataDictValue> metaDataDictValueList =
        metaDataDictValueMapper.selectList(queryWrapper);
    MetaDataDictValue metaDataDictValue;
    if (CollectionUtils.isEmpty(metaDataDictValueList)) {
      metaDataDictValue = new MetaDataDictValue();
      metaDataDictValue.setType("userinfo_verification_threshold");
      metaDataDictValue.setDictKey(dict_key);
      metaDataDictValue.setDictValue("1");
      metaDataDictValue.setVersion(1);
      metaDataDictValue.setCreator("userinfo_verification");
      metaDataDictValue.setModifier("userinfo_verification");
      metaDataDictValue.setGmtCreate(new Date());
      metaDataDictValue.setGmtModified(new Date());
      metaDataDictValue.setDictDesc(tenant + "day verification count");
      metaDataDictValueMapper.insert(metaDataDictValue);
    } else {
      metaDataDictValue = metaDataDictValueList.get(0);
      int count = Integer.parseInt(metaDataDictValue.dictValue);
      if (count > 19) {
        throw new MonitorException("Verification count reach the upper limit.");
      }
      MetaDataDictValue updateItem = new MetaDataDictValue();
      updateItem.setId(metaDataDictValue.getId());
      updateItem.setDictValue(String.valueOf(count + 1));
      updateItem.setGmtModified(new Date());
      metaDataDictValueMapper.updateById(updateItem);
    }
  }
}
