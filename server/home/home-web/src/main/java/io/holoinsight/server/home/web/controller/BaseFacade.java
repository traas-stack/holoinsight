/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.common.JsonResult;
import io.holoinsight.server.home.common.util.scope.MonitorScope;
import io.holoinsight.server.home.common.util.scope.RequestContext;
import io.holoinsight.server.home.facade.ApiSecurity;
import io.holoinsight.server.home.web.common.ApiSecurityFactory;
import io.holoinsight.server.home.common.util.FacadeTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Method;
import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: BaseFacade.java, v 0.1 2022年03月21日 3:55 下午 jinsong.yjs Exp $
 */
@Slf4j
@ControllerAdvice({"io.holoinsight.server.home"})
public class BaseFacade {

  @Autowired
  public FacadeTemplate facadeTemplate;

  private static DatePropertyEditorSupport dateEditorSupport = new DatePropertyEditorSupport();

  @InitBinder
  public void initBinder(WebDataBinder webDataBinder) {
    webDataBinder.registerCustomEditor(Date.class, dateEditorSupport);
  }


  @ExceptionHandler(Throwable.class)
  public ResponseEntity<JsonResult> handleException(Throwable e) {
    log.error(e.getMessage(), e);
    JsonResult jsonResult = new JsonResult();
    JsonResult.fillFailResultTo(jsonResult, HttpStatus.BAD_REQUEST.name(), e.getMessage());
    return new ResponseEntity<>(jsonResult, HttpStatus.BAD_REQUEST);
  }

  private static class DatePropertyEditorSupport extends PropertyEditorSupport {

    static final String FORMAT1 = "yyyy-MM-dd HH:mm:ss";
    static final String FORMAT2 = "yyyyMMddHHmmss";

    static FastDateFormat fdf1 = FastDateFormat.getInstance(FORMAT1);
    static FastDateFormat fdf2 = FastDateFormat.getInstance(FORMAT2);

    @Override
    public String getAsText() {
      return super.getAsText();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException {
      Object value = null;
      if (StringUtils.isEmpty(text)) {
        return;
      }
      int length = text.length();
      try {
        // 时间戳
        if (length == 13) {
          value = new Date(Long.parseLong(text));
        }
        // yyyy-MM-dd HH:mm:ss
        else if (length == FORMAT1.length()) {
          value = fdf1.parse(text);
        }
        // yyyyMMddHHmmss
        else if (length == FORMAT2.length()) {
          value = fdf2.parse(text);
        }
      } catch (Exception e) {
        log.error("Convert date createFailResult.", e);
        value = null;
      }
      this.setValue(value);
    }

  }

  protected void check(ApiSecurity securityParam) {
    StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
    Class<?> clazz = getClass();
    for (StackTraceElement element : stackTraceElements) {
      String stackMethodName = String.join(".", element.getClassName(), element.getMethodName());
      log.info("[API_SECURITY] try to look for {} ", stackMethodName);
      MonitorScope ms = RequestContext.getContext().ms;
      for (Method m : clazz.getMethods()) {
        String reflectedMethodName = String.join(".", clazz.getName(), m.getName());
        if (!StringUtils.equals(reflectedMethodName, stackMethodName)) {
          continue;
        }
        String fullMethodName = ApiSecurityFactory.getFullMethodName(clazz, m);
        log.info("[API_SECURITY] begin to check {}", fullMethodName);
        if (ApiSecurityFactory.createParameterMap.containsKey(fullMethodName)) {
          log.info("[API_SECURITY] check create method {}", fullMethodName);
          securityParam.checkCreate(ms.getTenant(), ms.getWorkspace());
        }
        if (ApiSecurityFactory.updateParameterMap.containsKey(fullMethodName)) {
          log.info("[API_SECURITY] check update method {}", fullMethodName);
          securityParam.checkUpdate(ms.getTenant(), ms.getWorkspace());
        }
        if (ApiSecurityFactory.readParameterMap.containsKey(fullMethodName)) {
          log.info("[API_SECURITY] check read method {}", fullMethodName);
          securityParam.checkRead(ms.getTenant(), ms.getWorkspace());
        }
      }
    }
  }

}
