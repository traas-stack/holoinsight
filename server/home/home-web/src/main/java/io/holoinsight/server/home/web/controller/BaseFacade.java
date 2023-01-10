/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */


package io.holoinsight.server.home.web.controller;

import io.holoinsight.server.home.web.common.FacadeTemplate;
import io.holoinsight.server.common.JsonResult;
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
import java.util.Date;

/**
 *
 * @author jsy1001de
 * @version 1.0: BaseFacade.java, v 0.1 2022年03月21日 3:55 下午 jinsong.yjs Exp $
 */
@Slf4j
@ControllerAdvice({"io.holoinsight.server.home", "com.alipay.cloudmonitor.prod.web"})
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
         JsonResult.createFailResult(jsonResult, String.valueOf(HttpStatus.BAD_REQUEST.value()),e.getMessage());
         return new ResponseEntity<>(jsonResult,HttpStatus.BAD_REQUEST);
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
                //时间戳
                if (length == 13) {
                    value = new Date(Long.parseLong(text));
                }
                //yyyy-MM-dd HH:mm:ss
                else if (length == FORMAT1.length()) {
                    value = fdf1.parse(text);
                }
                //yyyyMMddHHmmss
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

}