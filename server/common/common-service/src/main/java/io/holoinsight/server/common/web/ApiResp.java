/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common.web;

import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * created at 2022/3/2
 *
 * @author xzchaoo
 */
@Getter
@Setter
public class ApiResp {
  private boolean success;
  private String msg;
  private Object data;

  public static ApiResp success() {
    ApiResp resp = new ApiResp();
    resp.setSuccess(true);
    resp.setMsg("OK");
    return resp;
  }

  public static ApiResp error(String msg) {
    ApiResp resp = new ApiResp();
    resp.setSuccess(false);
    resp.setMsg(msg);
    return resp;
  }

  public static ApiResp success(Object data) {
    return success(data, "OK");
  }

  public static ApiResp success(Object data, String msg) {
    ApiResp resp = new ApiResp();
    resp.setSuccess(true);
    resp.setMsg(msg);
    resp.setData(data);
    return resp;
  }
}
