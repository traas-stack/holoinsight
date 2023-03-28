/*
 * Copyright 2022 Holoinsight Project Authors. Licensed under Apache-2.0.
 */
package io.holoinsight.server.home.dal.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "alarm_subscribe")
public class AlarmSubscribe {
  /**
   * id
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
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
   * 订阅者
   */
  private String subscriber;

  /**
   * 订阅组id
   */
  @Column(name = "group_id")
  private Long groupId;

  /**
   * 告警id
   */
  @Column(name = "unique_id")
  private String uniqueId;

  /**
   * 通知方式
   */
  @Column(name = "notice_type")
  private String noticeType;

  /**
   * 通知是否生效
   */
  private Byte status;

  /**
   * 租户id
   */
  private String tenant;

  /**
   * workspace
   */
  private String workspace;

  /**
   * 环境类型
   */
  @Column(name = "env_type")
  private String envType;

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
   * 获取订阅者
   *
   * @return subscriber - 订阅者
   */
  public String getSubscriber() {
    return subscriber;
  }

  /**
   * 设置订阅者
   *
   * @param subscriber 订阅者
   */
  public void setSubscriber(String subscriber) {
    this.subscriber = subscriber;
  }

  /**
   * 获取订阅组id
   *
   * @return group_id - 订阅组id
   */
  public Long getGroupId() {
    return groupId;
  }

  /**
   * 设置订阅组id
   *
   * @param groupId 订阅组id
   */
  public void setGroupId(Long groupId) {
    this.groupId = groupId;
  }

  /**
   * 获取告警id
   *
   * @return unique_id - 告警id
   */
  public String getUniqueId() {
    return uniqueId;
  }

  /**
   * 设置告警id
   *
   * @param uniqueId 告警id
   */
  public void setUniqueId(String uniqueId) {
    this.uniqueId = uniqueId;
  }

  /**
   * 获取通知方式
   *
   * @return notice_type - 通知方式
   */
  public String getNoticeType() {
    return noticeType;
  }

  /**
   * 设置通知方式
   *
   * @param noticeType 通知方式
   */
  public void setNoticeType(String noticeType) {
    this.noticeType = noticeType;
  }

  /**
   * 获取通知是否生效
   *
   * @return status - 通知是否生效
   */
  public Byte getStatus() {
    return status;
  }

  /**
   * 设置通知是否生效
   *
   * @param status 通知是否生效
   */
  public void setStatus(Byte status) {
    this.status = status;
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

  public String getEnvType() {
    return envType;
  }

  public void setEnvType(String envType) {
    this.envType = envType;
  }

  public String getWorkspace() {
    return workspace;
  }

  public void setWorkspace(String workspace) {
    this.workspace = workspace;
  }
}
