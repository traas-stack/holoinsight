/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */

package io.holoinsight.server.home.web.controller.model.open;

import io.holoinsight.server.common.AddressUtil;

import java.io.Serializable;

/**
 * @author zanghaibo
 * @time 2022-09-08 3:23 下午
 */
public class GrafanaJsonResult<T> implements Serializable {
    private static final long serialVersionUID = 8987276244569040347L;
    private boolean success;
    private String message;
    private String resultCode;
    private T result;
    private String host;

    public static <T> GrafanaJsonResult<T> createSuccessResult(T result) {
        GrafanaJsonResult<T> jsonResult = new GrafanaJsonResult<>();
        jsonResult.setSuccess(true);
        jsonResult.setResult(result);
        jsonResult.setHost(AddressUtil.getLocalHostIPV4());
        return jsonResult;
    }

    public static GrafanaJsonResult<Object> createFailResult(String message) {
        GrafanaJsonResult<Object> jsonResult = new GrafanaJsonResult<>();
        jsonResult.setSuccess(false);
        jsonResult.setMessage(message);
        jsonResult.setHost(AddressUtil.getLocalHostIPV4());
        return jsonResult;
    }

    public static <T> void createSuccessResult(GrafanaJsonResult<T> jsonResult, T result) {
        jsonResult.setSuccess(true);
        jsonResult.setResult(result);
        jsonResult.setHost(AddressUtil.getLocalHostIPV4());
    }

    public static <T> void createFailResult(GrafanaJsonResult<T> jsonResult, String message) {
        jsonResult.setSuccess(false);
        jsonResult.setMessage(message);
        jsonResult.setHost(AddressUtil.getLocalHostIPV4());
    }

    public static void createFailResult(GrafanaJsonResult<Object> jsonResult, String resultCode, String message) {
        jsonResult.setSuccess(false);
        jsonResult.setMessage(message);
        jsonResult.setResultCode(resultCode);
        jsonResult.setHost(AddressUtil.getLocalHostIPV4());
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(T result) {
        this.success = true;
        this.result = result;
        this.host = AddressUtil.getLocalHostIPV4();
    }

    public void setFail(String message) {
        this.success = false;
        this.message = message;
        this.host = AddressUtil.getLocalHostIPV4();
    }
}
