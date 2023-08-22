/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.common.config.EnvironmentProperties;
import io.holoinsight.server.home.biz.ula.ULAFacade;
import io.holoinsight.server.home.biz.common.MetaDictUtil;
import io.holoinsight.server.home.common.util.GlobalFlag;
import io.holoinsight.server.home.common.util.scope.AuthTargetType;
import io.holoinsight.server.home.common.util.scope.PowerConstants;
import io.holoinsight.server.home.web.common.TokenUrls;
import io.holoinsight.server.home.web.interceptor.MonitorScopeAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author jsy1001de
 * @version 1.0: SysFacadeImpl.java, v 0.1 2022年03月15日 8:04 下午 jsy1001de Exp $
 */
@RestController
@RequestMapping("/webapi/sys")
@TokenUrls("/webapi/sys/debug")
public class SysFacadeImpl extends BaseFacade {

  @Autowired
  private ULAFacade ulaFacade;
  @Autowired
  private EnvironmentProperties environmentProperties;

  @GetMapping(value = "/time")
  @ResponseBody
  public JsonResult time() {
    return JsonResult.createSuccessResult(System.currentTimeMillis());
  }

  @ResponseBody
  @GetMapping(value = "/config")
  public JsonResult<Map<String, Object>> sys() {

    Map<String, Object> sysMap = new HashMap<>();
    sysMap.put("authUrl", ulaFacade.getCurrentULA().getLoginUrl());
    sysMap.put("ula", ulaFacade.getCurrentULA().name());
    sysMap.put("site", this.environmentProperties.getDeploymentSite());
    sysMap.put("authApplyUrl", ulaFacade.getCurrentULA().authApplyUrl());
    sysMap.put("systemNotice", MetaDictUtil.getSystemNotice());
    sysMap.put("logTimeLayout", MetaDictUtil.getLogTimeLayoutMap());
    return JsonResult.createSuccessResult(sysMap);
  }

  @RequestMapping(value = "/debug/enable/{ukFlag}", method = RequestMethod.GET)
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.SRE, needPower = PowerConstants.TOKEN)
  public JsonResult<String> enableDebug(@PathVariable("ukFlag") String ukFlag) {
    GlobalFlag.enableDebug(ukFlag);
    return JsonResult.createSuccessResult("debug enable success");
  }

  @RequestMapping(value = "/debug/disable", method = RequestMethod.GET)
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.SRE, needPower = PowerConstants.TOKEN)
  public JsonResult<String> disableDebug() {
    GlobalFlag.disableDebug();
    return JsonResult.createSuccessResult("debug disable success");
  }

  @RequestMapping(value = "/debug/ukFlags", method = RequestMethod.GET)
  @ResponseBody
  @MonitorScopeAuth(targetType = AuthTargetType.SRE, needPower = PowerConstants.TOKEN)
  public JsonResult<Set<String>> getUkFlags() {
    return JsonResult.createSuccessResult(GlobalFlag.getUkFlags());
  }
}
