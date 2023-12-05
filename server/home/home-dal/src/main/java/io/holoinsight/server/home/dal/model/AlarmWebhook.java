/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author wangsiyuan
 * @date 2022/6/15 4:36 下午
 */
@Data
@Table(name = "alarm_webhook")
public class AlarmWebhook {
  /**
   * id
   */
  @TableId(type = IdType.AUTO)
  private Long id;

  /**
   * 创建时间
   */
  @Column(name = "gmt_create")
  private Date gmtCreate;

  /**
   * 修改时间
   */
  @Column(name = "gmt_modified")
  private Date gmtModified;

  /**
   * 创建者
   */
  private String creator;

  /**
   * 修改者
   */
  private String modifier;

  /**
   * 回调名称
   */
  @Column(name = "webhook_name")
  private String webhookName;

  /**
   * 回调状态
   */
  private Byte status;

  /**
   * 请求方式
   */
  @Column(name = "request_type")
  private String requestType;

  /**
   * 请求地址
   */
  @Column(name = "request_url")
  private String requestUrl;

  /**
   * 回调类型
   */
  private Byte type;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * 角色
   */
  private String role;

  /**
   * 请求头
   */
  @Column(name = "request_headers")
  private String requestHeaders;

  /**
   * 请求体
   */
  @Column(name = "request_body")
  private String requestBody;

  /**
   * 额外信息
   */
  private String extra;

  /**
   * 调试数据
   */
  @Column(name = "webhook_test")
  private String webhookTest;

  /**
   * 获取id
   *
   * @return id - id
   */
  public Long getId() {
    return id;
  }

  /**
   * 设置id
   *
   * @param id id
   */
  public void setId(Long id) {
    this.id = id;
  }

  /**
   * 获取创建时间
   *
   * @return gmt_create - 创建时间
   */
  public Date getGmtCreate() {
    return gmtCreate;
  }

  /**
   * 设置创建时间
   *
   * @param gmtCreate 创建时间
   */
  public void setGmtCreate(Date gmtCreate) {
    this.gmtCreate = gmtCreate;
  }

  /**
   * 获取修改时间
   *
   * @return gmt_modified - 修改时间
   */
  public Date getGmtModified() {
    return gmtModified;
  }

  /**
   * 设置修改时间
   *
   * @param gmtModified 修改时间
   */
  public void setGmtModified(Date gmtModified) {
    this.gmtModified = gmtModified;
  }

  /**
   * 获取创建者
   *
   * @return creator - 创建者
   */
  public String getCreator() {
    return creator;
  }

  /**
   * 设置创建者
   *
   * @param creator 创建者
   */
  public void setCreator(String creator) {
    this.creator = creator;
  }

  /**
   * 获取修改者
   *
   * @return modifier - 修改者
   */
  public String getModifier() {
    return modifier;
  }

  /**
   * 设置修改者
   *
   * @param modifier 修改者
   */
  public void setModifier(String modifier) {
    this.modifier = modifier;
  }

  /**
   * 获取回调名称
   *
   * @return webhook_name - 回调名称
   */
  public String getWebhookName() {
    return webhookName;
  }

  /**
   * 设置回调名称
   *
   * @param webhookName 回调名称
   */
  public void setWebhookName(String webhookName) {
    this.webhookName = webhookName;
  }

  /**
   * 获取回调状态
   *
   * @return status - 回调状态
   */
  public Byte getStatus() {
    return status;
  }

  /**
   * 设置回调状态
   *
   * @param status 回调状态
   */
  public void setStatus(Byte status) {
    this.status = status;
  }

  /**
   * 获取请求方式
   *
   * @return request_type - 请求方式
   */
  public String getRequestType() {
    return requestType;
  }

  /**
   * 设置请求方式
   *
   * @param requestType 请求方式
   */
  public void setRequestType(String requestType) {
    this.requestType = requestType;
  }

  /**
   * 获取请求地址
   *
   * @return request_url - 请求地址
   */
  public String getRequestUrl() {
    return requestUrl;
  }

  /**
   * 设置请求地址
   *
   * @param requestUrl 请求地址
   */
  public void setRequestUrl(String requestUrl) {
    this.requestUrl = requestUrl;
  }

  /**
   * 获取回调类型
   *
   * @return type - 回调类型
   */
  public Byte getType() {
    return type;
  }

  /**
   * 设置回调类型
   *
   * @param type 回调类型
   */
  public void setType(Byte type) {
    this.type = type;
  }

  /**
   * 获取租户id
   *
   * @return tenant - 租户id
   */
  public String getTenant() {
    return tenant;
  }

  /**
   * 设置租户id
   *
   * @param tenant 租户id
   */
  public void setTenant(String tenant) {
    this.tenant = tenant;
  }

  /**
   * 获取请求头
   *
   * @return request_headers - 请求头
   */
  public String getRequestHeaders() {
    return requestHeaders;
  }

  /**
   * 设置请求头
   *
   * @param requestHeaders 请求头
   */
  public void setRequestHeaders(String requestHeaders) {
    this.requestHeaders = requestHeaders;
  }

  /**
   * 获取请求体
   *
   * @return request_body - 请求体
   */
  public String getRequestBody() {
    return requestBody;
  }

  /**
   * 设置请求体
   *
   * @param requestBody 请求体
   */
  public void setRequestBody(String requestBody) {
    this.requestBody = requestBody;
  }

  /**
   * 获取额外信息
   *
   * @return extra - 额外信息
   */
  public String getExtra() {
    return extra;
  }

  /**
   * 设置额外信息
   *
   * @param extra 额外信息
   */
  public void setExtra(String extra) {
    this.extra = extra;
  }

  public String getWebhookTest() {
    return webhookTest;
  }

  public void setWebhookTest(String webhookTest) {
    this.webhookTest = webhookTest;
  }
}
