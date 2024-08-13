/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.common;

import java.io.Serializable;

/**
 * <p>
 * JsonResult class.
 * </p>
 *
 * @author xzchaoo
 * @version 1.0: JsonResult.java, v 0.1 2022年02月23日 11:55 上午 jinsong.yjs Exp $
 */
public class JsonResult<T> implements Serializable {
  private static final long serialVersionUID = 8987276244569040348L;
  private boolean success;
  private String message;
  private String resultCode;
  private T data;
  private String requestId;


  /**
   * <p>
   * createSuccessResult.
   * </p>
   */
  public static <T> JsonResult<T> createSuccessResult(T data) {
    JsonResult<T> result = new JsonResult<>();
    result.setSuccess(true);
    result.setData(data);
    result.setRequestId(
        AesUtil.encodeBase64(AesUtil.aesTest, AesUtil.aesTest, AddressUtil.getLocalHostIPV4()));
    return result;
  }

  /**
   * <p>
   * createFailResult.
   * </p>
   */
  public static <T> JsonResult<T> createFailResult(String message) {
    JsonResult<T> result = new JsonResult<>();
    result.setSuccess(false);
    result.setMessage(message);
    result.setRequestId(
        AesUtil.encodeBase64(AesUtil.aesTest, AesUtil.aesTest, AddressUtil.getLocalHostIPV4()));
    return result;
  }

  public static <T> JsonResult<T> createFailResult(String message, String resultCode) {
    JsonResult<T> result = new JsonResult<>();
    result.setSuccess(false);
    result.setMessage(message);
    result.setResultCode(resultCode);
    result.setRequestId(
        AesUtil.encodeBase64(AesUtil.aesTest, AesUtil.aesTest, AddressUtil.getLocalHostIPV4()));
    return result;
  }

  /**
   * <p>
   * createSuccessResult.
   * </p>
   */
  public static <T> void createSuccessResult(JsonResult<T> result, T data) {
    result.setSuccess(true);
    result.setData(data);
    result.setRequestId(
        AesUtil.encodeBase64(AesUtil.aesTest, AesUtil.aesTest, AddressUtil.getLocalHostIPV4()));
  }

  /**
   * <p>
   * fillFailResultTo
   * </p>
   */
  public static <T> void fillFailResultTo(JsonResult<T> result, String message) {
    result.setSuccess(false);
    result.setMessage(message);
    result.setRequestId(
        AesUtil.encodeBase64(AesUtil.aesTest, AesUtil.aesTest, AddressUtil.getLocalHostIPV4()));
  }

  /**
   * <p>
   * fillFailResultTo
   * </p>
   */
  public static void fillFailResultTo(JsonResult<?> result, String resultCode, String message) {
    result.setSuccess(false);
    result.setMessage(message);
    result.setResultCode(resultCode);
    result.setRequestId(
        AesUtil.encodeBase64(AesUtil.aesTest, AesUtil.aesTest, AddressUtil.getLocalHostIPV4()));
  }

  /**
   * <p>
   * isSuccess.
   * </p>
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * <p>
   * Setter for the field <code>success</code>.
   * </p>
   */
  public void setSuccess(boolean success) {
    this.success = success;
  }

  /**
   * <p>
   * Getter for the field <code>message</code>.
   * </p>
   */
  public String getMessage() {
    return message;
  }

  /**
   * <p>
   * Setter for the field <code>message</code>.
   * </p>
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * <p>
   * Getter for the field <code>resultCode</code>.
   * </p>
   */
  public String getResultCode() {
    return resultCode;
  }

  /**
   * <p>
   * Setter for the field <code>resultCode</code>.
   * </p>
   */
  public void setResultCode(String resultCode) {
    this.resultCode = resultCode;
  }

  public String getRequestId() {
    return requestId;
  }

  public void setRequestId(String requestId) {
    this.requestId = requestId;
  }

  /**
   * <p>
   * Getter for the field <code>data</code>.
   * </p>
   */
  public Object getData() {
    return data;
  }

  /**
   * <p>
   * Setter for the field <code>data</code>.
   * </p>
   */
  public void setData(T data) {
    this.success = true;
    this.data = data;
    this.requestId =
        AesUtil.encodeBase64(AesUtil.aesTest, AesUtil.aesTest, AddressUtil.getLocalHostIPV4());
  }

  /**
   * <p>
   * setFail.
   * </p>
   */
  public void setFail(String message) {
    this.success = false;
    this.message = message;
    this.requestId =
        AesUtil.encodeBase64(AesUtil.aesTest, AesUtil.aesTest, AddressUtil.getLocalHostIPV4());
  }
}
