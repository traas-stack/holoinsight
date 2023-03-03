/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.apm.web.handler;

import io.holoinsight.server.apm.web.model.FailResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author jiwliu
 * @version : ApiExceptionHandler.java, v 0.1 2022年09月20日 17:56 xiangwanpeng Exp $
 */
@ControllerAdvice({"io.holoinsight.server.apm"})
@Slf4j
public class ApiExceptionHandler {

  @ExceptionHandler
  ResponseEntity<FailResponse> exceptionHandler(Exception e) {
    log.error(e.getMessage(), e);
    return new ResponseEntity<>(FailResponse.builder()
        .code(String.valueOf(HttpStatus.BAD_REQUEST.value())).msg(e.getMessage()).build(),
        HttpStatus.BAD_REQUEST);
  }
}
